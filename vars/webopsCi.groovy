@Library('github.com/melvinpetix/jsl@main')_
import com.webops.*;
def cause = currentBuild.getBuildCauses('hudson.model.Cause$UserIdCause')
def common = new com.webops.Common()
//node("${env.jenkins_agent}"){  

def call(Map config){
  def body = config
  
  
  def sshArgs
  def stringParams
  def choiceParams
  def PASSWORD
  def j
  //common.gitCheckout("${repo}", 'prd-private-gitlab', "${}")
  common.gitClone 'gitlab.com/me1824/jsl', 'glpat-GxfR6J-STGecxjDPGz8z', 'main'
   
  def folders = sh(returnStdout: true, script: "ls $workspace/runbook").replaceAll(".yml", "")
    writeFile file:'task.txt', text: "${folders}" 
  tasklist = readFile("$workspace/task.txt") 

  yaml = input(id: 'tasklist', message: 'task', parameters: [
  [$class: 'ChoiceParameterDefinition', choices: "${tasklist}", description: '', name: 'tasklist']]) 
        
  j = jobCfg(yaml)

  if(j.parameters.string){
    stringParams = input(id: 'userInput', message: "${j.parameters.string.name}", parameters: [
      [$class: 'StringParameterDefinition', defaultValue: '', 
      description: "${j.parameters.string.name}", name: "${j.parameters.string.name}", 
      trim: true]
    ]) 
    println stringParams
  }

  if(j.parameters.type == 'choice'){
    def choices = j.parameters.choice.choices.replaceAll(',',"\n")
      choiceParams = input(id: '', message: "${j.parameters.choice.name}", parameters: [
      [$class: 'ChoiceParameterDefinition', choices: "${choices}",name: "${j.parameters.choice.name}"]
    ])  
    println choiceParams
  }

  if(j.parameters.password){
    PASSWORD = input(id: 'password', message: '', parameters: [
      [$class: 'PasswordParameterDefinition', name: "Password"]
    ])
  }

  stage("${userInput}"){
    List<String> stepsA = j.steps
    def serverA = j.steps.server
    
    if(j.notification){
      common.sendTeamsNotif("${BUILD_TRIGGER_BY}", j.project_name, j.notification.webhook)
    }
    if(!j.steps.server){
      stepsA.collect{k, v->
        stage(name: "${k}"){
          v.each{command->
            sh command
          }
        }
      }
    } else {
      stepsA.each{step->
        step.name.collect{stepName->
          stage("${stepName}"){
            serverA.each{server->
              commandA.collect{v->
                v.each{command->
sh """#!/bin/bash +x\n
export "${j.parameters.string.name}"="${stringParams}"\n\
export "${j.parameters.choice.name}"=${choiceParams}\n\
export PASSWORD="${PASSWORD}"\n\
${sshArgs} ${server} "export TERM=xterm-256color;
set -x; ${command}"
"""                         }
                        }
                    }
                }
            }
        }
     }
   }  
}

    
