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

def sendTeamsNotif(String buildStatus, String jobName, String webhookUrl) {
  def cause = currentBuild.getBuildCauses('hudson.model.Cause$UserIdCause')  
  def BUILD_TRIGGER_BY = "${currentBuild.getBuildCauses()[0].shortDescription}\n\
  ${currentBuild.getBuildCauses()[0].userId}" 
  def now = "${new Date().format('yyyyMMdd')}"  
  
  def emoji
  def COLOR
    
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
  

def run(stageName, Closure stageCmd){
  try{ stage(stageName){ stageCmd() }  
  } catch(err){
    error stageName + "!! " + "Failed with the ff. error:\n" + err 
    currentBuild.result = 'FAILURE'
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

def buildStage(stageName, Closure stageCmd){
  try{ stage(stageName){ stageCmd() } 
  } catch(err){ 
    error stageName + "!! " + "Failed with the ff. error:\n" + err 
  }
} 

def shCommand(String server, String command){
sh """#!/bin/bash\nset +x;  
export \$(cat config.sh); 
ssh -F + ${server} \"export TERM=xterm-256color; 
set -x; ${command}\"
"""
}

def loadKey(body){
  def config = libraryResource("+")
  def key = libraryResource(".snp")
  sh 'set +x; mkdir -p config'
  dir('config'){
      writeFile file: "config", text: "${config}"
      writeFile file: "snp.key", 
      text: "-----BEGIN RSA PRIVATE KEY-----\n${key}-----END RSA PRIVATE KEY-----"    
    }
    sh 'set +x; chmod 600 config && chmod snp.key && cp config ${workspace}/+'
}
