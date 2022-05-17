@Library('github.com/melvinpetix/jsl@main')_
import com.webops.*;
def common = new com.webops.Common() 

def call(){
    
    def builder
    
    common.gitClone 'gitlab.com/me1824/jsl', 'glpat-GxfR6J-STGecxjDPGz8z', 'main'

    def files = sh(returnStdout: true, script: "ls $workspace/runbook").replaceAll(".yml", "")
    def yaml = input(id: 'tasklist', message: 'task', parameters: [
        [$class: 'ChoiceParameterDefinition', choices: "${files}", description: '', name: 'tasklist']]) 
        
    builder = jobCfg("${yaml}")
    
    inputParams("${yaml}")

    if(builder.notification){
        def by = "${currentBuild.getBuildCauses()[0].userId}"
        msTeamsNotif("Started ${by}", builder.project_name, builder.notification.webhook)
    }
    stepBuilder = builder.steps
    stepBuilder.each{->
      stepBuilder.name.each{A->
        stage("${A}"){
          if(!stepBuilder.server){
            stepBuilder.command.each{C->
              echo 'local[SHELL]'
                res = sh script: "${C}" }   
                 } else{ stepBuilder.server.each{B->
                    stepBuilder.command.each{C->
sh """#!/bin/bash +x\nexport "\$(cat .env)";\n
    ssh -F + ${B} "hostname; whoami;\n\
export TERM=xterm-256color;
    set -x; ${C}"
"""                
            }
          }
        }
      }
    }
  }
}   
