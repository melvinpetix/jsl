package com.webops;

import static org.yaml.snakeyaml.DumperOptions.FlowStyle.BLOCK
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream


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
    def buildStatus = config.m
    def jobName = config.j
    def webhookUrl = config.url
    
    if(!jobName){
        jobName = "${env.JOB_NAME}"       
    }                                                                      
    if(currentBuild.result == ('FAILURE')){
        emoji = "���"
        COLOR = "ff0000"
    } else {
        emoji = "����"
        COLOR = "00FF00"
    }  
    steps.sh "curl -X POST -H \'Content-Type: application/json\'\
    -d \'{\"title\": \"${jobName}\", \"themeColor\": \"${COLOR}\", \"text\": \"${buildStatus}\" }' ${webhookUrl}"
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

def gitCheckout(String repo, String credentialsId, String branch='master') {
  checkout([
    $class: 'GitSCM',
      branches: [[name: "${branch}"]],
      extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: '.']],
      userRemoteConfigs: [[credentialsId: 
      credentialsId, url: 'git@gitlab.usautoparts.io:' + repo]]])
      //sh "set +x; chmod 600 \$(find . -name \"*.key\"||\"*.pub\"||\"id_rsa\")"
        
}

def gitClone(String repoUrl, String token, String branch='master'){
    git branch: "${branch}", url: 'https://oauth:' + token + '@' + repoUrl
    //sh "set +x; chmod 600 \$(find . -name \"*.key\"||\"*.pub\"||\"id_rsa\")"
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
  def key = libraryResource(".snp")
  prependToFile content: config, file: "${workspace}/+"
  prependToFile content: "-----BEGIN RSA PRIVATE KEY-----\n${key}-----END RSA PRIVATE KEY-----", 
  file: "${workspace}/config/snp.key"
  sh 'set +x; chmod 600 config/* >/dev/null 2>&1'
  sh "set +x; chmod 600 \$(find . -name \"*.key\"||\"*.pub\"||\"id_rsa\")"
} 

def buildParams(yamlName){
    def userInput
    def j = readYaml file: 'runbook/' + yamlName + '.yml'

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
      password(name: 'P4SSWORD')] 
      env["${j.parameters.password.name}"] = P4SSWORD
    }
     
} 

def sshScp(source, destination, options=null){
  def common = new com.webops.Common()
  def sshArgs = '-F +'
  if (!options){
     options = ['StrictHostKeyChecking': 'no', 'UserKnownHostsFile': '/dev/null']
  }

    sshCmd("scp ${sshArgs} ${source} ${destination}")
}


return this;

    
