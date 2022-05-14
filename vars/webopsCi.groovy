@Library('github.com/melvinpetix/jsl@main')_
import com.webops.*;

def call(){
  
  def common = new com.webops.Common()
  def BUILD_TRIGGER_BY = "${currentBuild.getBuildCauses()[0].shortDescription} / ${currentBuild.getBuildCauses()[0].userId}" 
  def cause = currentBuild.getBuildCauses('hudson.model.Cause$UserIdCause')
  def stringParams
  def choiceParams
  def j

  deleteDir()

  common.gitClone 'gitlab.com/me1824/jsl', 'glpat-GxfR6J-STGecxjDPGz8z', 'main'
  //common.gitCheckout 'mbiscarra/legacy-task.git', 'prd-private-gitlab', 'script'


   def folders = sh(returnStdout: true, script: "ls $workspace/runbook").replaceAll(".yml", "")
   writeFile file:'task.txt', text: "${folders}"
   tasklist = readFile("$workspace/task.txt") 
   userInput = input(id: 'tasklist', message: 'task', parameters: [
      [$class: 'ChoiceParameterDefinition', choices: "${tasklist}", description: '', name: 'tasklist']]) 

        /*
        if(j.project_name == 'MASTER_TEMPLATE'){
            def snapshot = sh(returnStdout: true, 
            script: "ssh -F config/config csendrepo01 'ls /data/ENDECA_DATA_REPO_6.5/FULL/MERGE'").trim()
            writeFile file:'snapshot.txt', text: "${snapshot}"
            snaplist = readFile("$workspace/snapshot.txt") 
            snapshot_date = input(id: 'snap', message: 'snapshot', parameters: [
            [$class: 'ChoiceParameterDefinition', choices: "${snaplist}", description: '', name: '']])  
            sh "sed -i 's|SNAPSHOT|${snapshot_date}|g' scripts/hotcopy2.sh"
        }
        */  
  

 
      j = jobCfg("$workspace/runbook/${userInput}.yml")
      list stepsA = j.steps
      list enV = j.environment
      list commandA = j.steps.command
      def serverA = j.steps.server
      def sshArgs = "ssh -F + -t "

      if(j.parameters.string){
        stringParams = input(id: 'userInput', message: "${j.parameters.string.name}", parameters: [
        [$class: 'StringParameterDefinition', defaultValue: '', description: "${j.parameters.string.name}", 
        name: "${j.parameters.string.name}", trim: true]])
        println stringParams
      }
    
      if(j.parameters.choice){
        def choices = j.parameters.choice.choices.replaceAll(',',"\n")
        choiceParams = input(id: '', message: "${j.parameters.choice.name}", parameters: [
        [$class: 'ChoiceParameterDefinition', choices: "${choices}", description: '', 
        name: "${j.parameters.choice.name}"]])  
        println choiceParams
      }
  
      if(j.notification){
        common.sendTeamsNotif("${BUILD_TRIGGER_BY}", j.project_name, j.notification.webhook)
      }

      stepsA.name.each{step->
        stage("${step}"){
          serverA.each{server->
            commandA.collect{v->
              v.each{command->
sh """#!/bin/bash +x\n
export "${j.parameters.string.name}"="${stringParams}"\n\
export "${j.parameters.choice.name}"=${choiceParams}\n\
${sshArgs} ${server} "export TERM=xterm-256color;
set -x; ${command}"
"""
              }
            }
          }
        }
      }
    }
}

