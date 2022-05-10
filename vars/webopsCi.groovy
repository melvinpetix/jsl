@Library('github.com/melvinpetix/jsl@main')_
import com.webops.*;

def call(){
  node("${env.jenkins_agent}"){  
    def common = new com.webops.Common()
    def BUILD_TRIGGER_BY = "${currentBuild.getBuildCauses()[0].shortDescription} / ${currentBuild.getBuildCauses()[0].userId}" 
    def cause = currentBuild.getBuildCauses('hudson.model.Cause$UserIdCause')

    deleteDir()

    git branch: 'main', url: 'https://oauth:glpat-GxfR6J-STGecxjDPGz8z@gitlab.com/me1824/jsl.git'
    sh 'chmod 600 .config/*'   

    stage('define task'){
      def folders = sh(returnStdout: true, script: "ls $workspace/runbook").replaceAll(".yml", "")
      writeFile file:'task.txt', text: "${folders}"
      tasklist = readFile("$workspace/task.txt") 
      userInput = input(id: 'tasklist', message: 'task', parameters: [
      [$class: 'ChoiceParameterDefinition', choices: "${tasklist}", description: '', name: 'tasklist']]) 
    }

    stage("${userInput}"){
      def j = jobCfg("$workspace/runbook/${userInput}.yml")
      list stepsA = j.steps
      list enV = j.environment
      def params = ''
       
      if(j.parameters){
        if(j.parameters.type == 'string'){
          params = input(id: 'String1', message: "${j.parameters.name}", parameters: [
          [$class: 'StringParameterDefinition', description: '', name: '', trim: 'true' ]])  
        }
      }

      if(j.notification){
        common.sendTeamsNotif("${BUILD_TRIGGER_BY}", j.project_name, j.notification.webhook)
      }
    
      if(j.environment){
        enV.collect{a,b->
          withEnv(["${a}=${b}"]){
            stepsA.collect{k,v->
              stage("${k}"){
                v.each{command->
                  sh"""#!/bin/bash +x
                  export TERM=xterm-256color
                  export "${j.parameters.name}"="${params}"
                  ssh -F + ${server} "${command}"
                  """ 
                }
              }
            }
          }   
        }
      } 
      else{
        stepsA.collect{k,v->
          stage("${k}"){
            v.each{command->
              if(!j.server || !j.server."${k}" || j.server."${k}" == 'local'){
                sh"""#!/bin/bash +x
                export TERM=xterm-256color
                export snapshot_date="${params}"
                ${command}
                """ 
              } else {
              def server = j.server."${k}"
                sh"""#!/bin/bash +x
                export TERM=xterm-256color
                export "${j.parameters.name}"="${params}"
                ssh -F + ${server} "${command}"
                """ 
              }                      
            }
          }   
        }
      }  
    }
  }
}

