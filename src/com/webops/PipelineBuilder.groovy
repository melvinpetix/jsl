package com.webops;

import com.webops.ProjectConfiguration;
import com.webops.Common;


class PipelineBuilder implements Serializable {

  def script

  ProjectConfiguration parse(yaml) {

    ProjectConfiguration projectConfiguration = new ProjectConfiguration();
    
    projectConfiguration.parseProjectName = parseProjectName(yaml.project_name);

    projectConfiguration.parameters = parseParameters(yaml.parameters);

    projectConfiguration.notification = parseNotification(yaml.notification);
    
    return projectConfiguration;

  }

   def parseParameters(yaml.parameters){
    def userInput
    if(yaml.parameters){    
      steps.stage('pipeline parameters'){
        timeout(time: 120, unit: 'SECONDS') {  
          yaml.parameters.each{params->
            switch(params.type){
              case 'string':
              userInput = input parameters: [string(name: params.args.name)]; break    
              case 'choice':
              userInput = input parameters: [choice(name: params.args.name, choices: params.args.choices)]; break
              case 'password':
              userInput = input parameters: [password(name: params.args.name)]; break           
            }  
            return env."${params.args.name}" = userInput
          }     
        }
      }
    }
  }

  def parseNotification(yaml){
    if(yaml.notification){    
      def common = new com.webops.Common()
      def userName = "${currentBuild.getBuildCauses()[0].userId}"
      common.sendTeamsNotif(
        msg: "Started by: ${userName}", 
        job: "${yaml.project_name}", 
        url: "${yaml.notification.webhook}"
      )
    } 
  }

  def parseEnvironment(def environment) {
    if (!environment) {
      return ''
    }
      return environment.collect { k, v -> "${k}=${v}"}
  }

  def parseProjectName(yaml) {
    if (!yaml.project_name) {
      return "webops"
    }
    return yaml.project_name
  }

}
