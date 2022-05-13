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
 
  def now = "${new Date().format('yyyyMMdd')}"  
  
  def emoji
  
  if(currentBuild.result == ('FAILURE')){
    emoji = "❌" } else { emoji = "🚀" }

  sh "curl -X POST -H \'Content-Type: application/json\'\
    -d \'{\"title\": \"${emoji} Unified-Notifier :  ${jobName}\", \"text\": \"${buildStatus}\"}\' ${webhookUrl}" 
}

def execute(Map config){
  def command = [:]
  def args = "ssh -F + -t "
  if(!config.server){
    echo 'local[SHELL]'
    res = sh script: config.cmd, 
    returnStdout: false
    return res 
  } 
  def slist = config.server.toString().split(',')
  if(slist.size() > 1){ 
    for(i in slist){ 
     def s = i.trim()
        command[s] = { sh script: args + "${s}" + " " + "${config.cmd}" } 
    } 
    parallel command
  } else { 
    sh script: args + "${config.server}" + " " + config.cmd 
  }
}

def run(stageName, Closure stageCmd){
  try{ stage(stageName){ stageCmd() }  
  } catch(err){ 
    sendTeamsNotif "${stageName} failed" + err, "${webops}"
    error stageName + "!! " + "Failed with the ff. error:\n" + err 
    currentBuild.result = 'FAILURE'
  }
} 


def gitCheckout(String repoUrl, String repo, String credentialsId, String branch='master') {
  checkout([
    $class: 'GitSCM',
      branches: [[name: "${branch}"]],
      extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: '.']],
      userRemoteConfigs: [[credentialsId: 
      credentialsId, url: 'git@' + repoUrl + ':' + repo]]])
        sh "chmod 600 config/*.key" 
}

def gitClone(String repoUrl, String token, String branch='master'){
    ws = pwd()
    git branch: "${branch}", url: 'https://oauth:' + token + '@' + repoUrl
        sh "chmod 600 config/*.key" 
    
}
