@Library('github.com/melvinpetix/jsl@main')_
import com.webops.*;


//node("${env.jenkins_agent}"){  


def call(body){
  def common = new com.webops.Common()
  common.gitClone 'gitlab.com/me1824/jsl', 'glpat-GxfR6J-STGecxjDPGz8z', 'main'
  def started_by = currentBuild.getBuildCauses()[0].shortDescription}

  def config = body
  def sshArgs
  def stringParams
  def choiceParams
  def PASSWORD
  def pipelineConfig

    def files = sh(returnStdout: true, 
      script: "ls $workspace/runbook").replaceAll(".yml", "")
        
    def runbook = input(id: 'tasklist', message: 'task', 
        parameters: [[$class: 'ChoiceParameterDefinition', 
        choices: "${files}", description: '', name: 'tasklist']]) 
            
    pipelineConfig = jobCfg("$workspace/runbook/${runbook}.yml")

      if(pipelineConfig.parameters){
        switch(pipelineConfig.parameters.type){
          case 'string':
              stringParams = input(id: 'input', message: '', parameters:[
                [$class: 'StringParameterDefinition',
                defaultValue: '', description: '', 
                name: "${pipelineConfig.parameters.name}", trim: true]])
              break
          case 'choice':
              def choices = pipelineConfig.parameters.choice.choices.replaceAll(',',"\n")            
                choiceParams = input(id: '', message: "${k}", parameters: [
                [$class: 'ChoiceParameterDefinition', choices: "${v}", name: "${k}"]])
              break
          case 'password':
              PASSWORD = input(id: 'password', message: '', parameters: [
                [$class: 'PasswordParameterDefinition', name: "Password"]])
              break
        }
      }

    stage("task: ${runbook}"){
      def command = [:]
      
      if(pipelineConfig.notification){
        common.sendTeamsNotif("${startedby}", pipelineConfig.project_name, pipelineConfig.notification.webhook)
      }
 
      pipelineConfig.steps.each{step->
        step.name.collect{name->
          stage("${name}"){
            pipelineConfig.steps.command.collect{v->
                v.each{cmd->
                    serversInParallel(server: "${server}", cmd: "${cmd}")
                }        
            }
          }
        }
      }
    }
}


def serversInParallel(Map config){
  def command = [:] 

  if(!server || server == 'localhost'){
    echo 'local[SHELL]'
    res = config.cmd 
    returnStdout: false
    return res 
  } 

  def serverlist = config.server.toString().split(',')

  if(serverlist.size() > 1){ 
    for(i in serverlist){ 
      def s = i.trim()
        command[s] = { 
        """#!/bin/bash +x\n\
              export="\$(cat .env)"
  ${sshArgs} ${server} "export TERM=xterm-256color;\n\
  set -x; ${config.cmd}"
        """ 
        }
    }   
    parallel command
  }
  else { 
    sh script: args + "${config.server}" + " " + config.cmd 
  }
}
