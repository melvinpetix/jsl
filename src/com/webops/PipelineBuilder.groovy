package com.webops;
import com.webops.ProjectConfiguration;
import com.webops.Common;


class PipelineBuilder implements Serializable {

  ProjectConfiguration parse(yaml) {
    ProjectConfiguration projectConfiguration = new ProjectConfiguration();  
    projectConfiguration.parseProjectName = parseProjectName(yaml.project_name);
    projectConfiguration.parameters = parseParameters(yaml.parameters);
    projectConfiguration.notification = parseNotification(yaml.notification);    
    return projectConfiguration;
 }
  
def parseParameters(def parameters){
  def userInput
  if(yaml.parameters){ 
    def inputPrompt = parseParams yaml.parameters
    timeout(time: 120, unit: 'SECONDS') {
      userInput = step.input parameters: inputPrompt     
    } 
    userInput.each{x,v-> env."$x"="$v"}      
}
  
@NonCPS
def parseParams(parameters) {
  parameters.collect { params ->
    this.invokeMethod params.type, 
    params.args.collectEntries { name, value ->
      [
        name, value instanceof String ? exportEnv(value) : value
      ]
    }
  }
}

@NonCPS
def exportEnv(value) {
  new groovy.text.GStringTemplateEngine()
    .createTemplate(value)
    .make([env:env])
    .toString()
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
    return environment.collect { k, v -> "${k}=${v}"}
  }

  def parseProjectName(def project_name) {
    if (!yaml.project_name) {
        return "webopsInitiative"
    }
    return yaml.project_name
  }
}
