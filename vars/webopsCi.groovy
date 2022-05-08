@Library('github.com/melvinpetix/jsl@main')_
import com.webops.*;
def common = new com.webops.Common()

node("${env.jenkins_agent}"){  
    def BUILD_TRIGGER_BY = "${currentBuild.getBuildCauses()[0].shortDescription} / ${currentBuild.getBuildCauses()[0].userId}" 
    def cause = currentBuild.getBuildCauses('hudson.model.Cause$UserIdCause')

    deleteDir()
  
    stage('checkout'){
      git branch: 'main', url: 'https://oauth:glpat-GxfR6J-STGecxjDPGz8z@gitlab.com/me1824/jsl.git'
      sh 'chmod 600 snp.key; chmod 600 +'   
  }
  
  stage('define task'){
    def folders = sh(returnStdout: true, script: "ls $workspace/runbook").replaceAll(".yml", "")
    writeFile file:'task.txt', text: "${folders}"
    tasklist = readFile("$workspace/task.txt") 
    userInput = input(id: 'tasklist', message: 'task', parameters: [
    [$class: 'ChoiceParameterDefinition', choices: "${tasklist}", description: '', name: 'tasklist']]) 
  }
  def j = jobCfg("$workspace/runbook/${userInput}.yml")
  list stepsA = j.steps
  list enV = j.environment
  
  if(j.notification){
    common.sendTeamsNotif("${BUILD_TRIGGER_BY}", j.project_name, j.notification.webhook)
  }
  
  if(j.environment){
    enV.collect{a,b->
      withEnv(["${a}=${b}"]){
        stepsA.collect{k,v->
          stage("${k}"){
            v.each{command->
              sh "ssh -F + ${server} '${command}'"
            }
          }
        }
      }   
    }
  } else{
    stepsA.collect{k,v->
      stage("${k}"){
        v.each{command->
          if(!j.server){
             sh script: "${command}"
          } else {
            def server = j.server."${k}"
            sh"""#!/bin/bash +x\n\
            export TERM=xterm-256color\n\
            ssh -F + ${server} '${command}'
            """ 
          }                      
        }
      }   
    }
  }  
}
