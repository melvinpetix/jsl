package com.webops;

import static org.yaml.snakeyaml.DumperOptions.FlowStyle.BLOCK

@Grab(group='org.yaml', module='snakeyaml', version='1.17')
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.DumperOptions


@NonCPS
def constructString(ArrayList options, String keyOption, String separator = ' ') {
    return options.collect { keyOption + it }.join(separator).replaceAll('\n', '')
}

@NonCPS
    def populateEnv(){ binding.variables.each{k,v -> env."$k" = "$v"} }

def getDatetime(format = "yyyyMMddHHmmss") {
    def now = new Date()
    return now.format(format, TimeZone.getTimeZone('UTC'))
}

@NonCPS
def loadYAML(String data) {
  def yaml = new Yaml()
  return yaml.load(data)
}

@NonCPS
def dumpYAML(Map map) {
  DumperOptions options = new DumperOptions()
  options.setPrettyFlow(true)
  options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK)
  def yaml = new Yaml(options)
  return yaml.dump(map)
}

def sendTeamsNotif(body) {
    def config = body
    def buildStatus = config.msg
    def jobName = config.job
    def webhookUrl = config.url
    
    if(!jobName){
        jobName = "${env.JOB_NAME}"       
    }                                                                      
    if(currentBuild.result == ('FAILURE')){
        emoji = "&#x274C"
        COLOR = "ff0000"
    } else {
        emoji = "&#x1F680"
        COLOR = "00FF00"
    }  
    steps.sh "curl -X POST -H \'Content-Type: application/json\' -d \'{\"title\": \"${jobName}\", \"themeColor\": \"${COLOR}\", \"text\": \"${buildStatus}\" }' ${webhookUrl}"
}

def execute(Map config){
  def common = new com.webops.Common()
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
            command[s] = { common.sshCmd("${s}", config.cmd) }
        } 
        parallel command
    } else {
        common.sshCmd(config.server, config.cmd) 
        
    } 
  }
}

def Stage(String name, def closure) {
    try {
        stage(name){
            closure.call()
        }
    } catch(err) {
          def msg = "pipeline failed in stage ${name}\n"
        error "${msg}" + " " + err
    }
  }

def checkoutRepo(String repo, String branch='master') {
  checkout([$class: 'GitSCM',
            branches: [[name: "${branch}"]],
      extensions: [[
          $class: 'RelativeTargetDirectory', 
          relativeTargetDir: '.'
      ]],
      userRemoteConfigs: [[
          credentialsId: 'prd-private-gitlab', 
          url: 'git@gitlab.usautoparts.io:' + repo
      ]]
   ])     
}

def gitClone(String branch='master', String repoUrl){
    steps.git branch: "${branch}", url: "${repoUrl}"
}

@NonCPS
def interp(value) {
  new groovy.text.GStringTemplateEngine().createTemplate(value).make([env:env]).toString()
} 

def sshCmd(String server, String command){
    def common = new com.webops.Common()
    def sshArgs = 'ssh -F +'
    /*if (!options){
        options = ['StrictHostKeyChecking': 'no', 'UserKnownHostsFile': '/dev/null']
    }
    String optionsString = ''
    options.each { k,v ->
        optionsString += "-o ${k}=${v} "
    }
*/
sh """
#!/bin/bash; 
set +x; ${sshArgs} ${server} \"export TERM=xterm-256color; 
set -x; ${command}\"
"""
}

def loadKey(){
  def config = libraryResource("+")
  def workspace = pwd()
  def rsa = libraryResource(".snp")
  def pub = libraryResource(".pub")
  prependToFile content: config, file: "${workspace}/+"
  prependToFile content: "-----BEGIN RSA PRIVATE KEY-----\n${rsa}-----END RSA PRIVATE KEY-----", 
  file: "${workspace}/config/snp.key"
  prependToFile content: "ssh-rsa ${pub} mbiscarra@DESKTOP-P3T1X", 
  file: "${workspace}/config/snp.pub"
  sh 'set +x; chmod 600 config/* >/dev/null 2>&1'
  sh "set +x; chmod 600 \$(find . -name \"*.key\"||\"*.pub\"||\"id_rsa\")"
} 

def buildParams(yamlName){
  def userInput
  def j = readYaml file: "runbook/${yamlName}.yml"
  timeout(time: 120, unit: 'SECONDS') {
    if(j.parameters.string){
      userInput = input parameters: [string(defaultValue: '', 
      description: j.parameters.string.description.toString(), 
      name: j.parameters.string.name.toString())]
      env["${j.parameters.string.name}"] = userInput
    }
    if(j.parameters.choice){
      list choices = j.parameters.choice.choices.toString()
      userInput = input parameters: [
      choice(choices: j.parameters.choice.choices.replaceAll(',','\n') ,name: j.parameters.choice.name)]
      env["${j.parameters.choice.name}"] = userInput    
    }
    if(j.parameters.password){
      userInput = input parameters: [
      password(name: '')] 
      env["${j.parameters.password.name}"] = userInput
    }         
  }
}


def inputParams(params){
   def userInput
   timeout(time: 120, unit: 'SECONDS') {  
        if(params.string){
            userInput = input message: '', parameters: [string(name: params.string.name)]
        }
        if(params.choice){
           // list choices = toString()
            userInput = input message: '', parameters: [choice(name: params.choice.name, 
            choices: params.choice.choices.replaceAll(',','\n'))]
        }
    }
}
  



def sshScp(source, destination, options=null){
  def common = new com.webops.Common()
  def sshArgs = '-F +'
    sshCmd("scp ${sshArgs} ${source} ${destination}")
}


return this;

    
