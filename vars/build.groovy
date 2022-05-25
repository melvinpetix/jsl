#!groovy

def call(String stageName, Closure stageCmd){
  try{ 
    stage(stageName){ 
      stageCmd()
    } 
 } catch(err){ 
    error stageName + "Failed!! error:\n" + err 
  }
} 

def execute(Map config){
  def command = [:]
  def args = "ssh -F + -tt "
  
  if(!config.server){
    echo 'local[SHELL]'
    ${command}
  } else {  
    def slist = config.server.toString().split(',')
    if(slist.size() > 1){ 
        for(i in slist){ 
            def s = i.trim()
            command[s] = { shCommand("${s}", config.cmd) }
        } 
        parallel command
    } else {
        shCommand(config.server, config.cmd) 
        
    } 
  }
}

def shCommand(String server, String command){
  def args = "ssh -F + -t"
  sh """#!/bin/bash\nset +x; ${args} ${server} \"export TERM=xterm-256color; 
  set -x; ${command}\"
  """
}

def params(yamlName){
  def userInput
  def j = readYaml file: "runbook/${yamlName}.yml"

  if(j.parameters.string){
    userInput = input parameters: [string(defaultValue: '', 
    description: j.parameters.string.description, 
    name: j.parameters.string.name)]
    env["${j.parameters.string.name}"] = userInput
  }
  if(j.parameters.choice){
    def choices = j.parameters.choice.choices.replaceAll(',',"\n")
    userInput = input parameters: [
    choice(choices: choices ,name: j.parameters.choice.name)]
    env["${j.parameters.choice.name}"] = userInput    
  }
  if(j.parameters.password){
    P4SSWORD = input parameters: [
    password(name: '')] 
    env["${j.parameters.password.name}"] = P4SSWORD
  }       
}

def notifier(String buildStatus, String jobName, webhookUrl) {     
  if(currentBuild.result == ('FAILURE')){
    emoji = "❌"
    COLOR = "ff0000"
  } else {
    emoji = "🚀"
    COLOR = "00FF00"
  }  
   sh "curl -X POST -H \'Content-Type: application/json\'\
  -d \'{\"title\": \"${emoji}Unified-Notifier: ${params.SNAPSHOT}\",\
    \"themeColor\": \"${COLOR}\", \"text\": \"${buildStatus}\" }' ${webhookUrl}"
}
