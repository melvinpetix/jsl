package com.webops;

import groovy.text.GStringTemplateEngine;
import groovy.text.Template;
import groovy.text.TemplateEngine;
import static org.yaml.snakeyaml.DumperOptions.FlowStyle.BLOCK
//@Grab(group='org.yaml', module='snakeyaml', version='1.17')
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.DumperOptions


@NonCPS
def parseParams(parameters) {
  parameters.collect { params ->
    this.invokeMethod params.type, 
    params.args.collectEntries { name, value ->
      [
        name, value instanceof String ? exportEnv(value) : value
      ]
    }
  }
}

@NonCPS
def exportEnv(value) {
  new groovy.text.GStringTemplateEngine()
    .createTemplate(value)
    .make([env:env])
    .toString()
} 


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
    config.cmd
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

 def stage(String name, Closure stageCommands) {
    try {
      steps.stage(name, stageCommands)
    } catch(err) {
      def msg = "pipeline failed in stage ${name}\n"
      error "${msg}" + " " + err
      throw err
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

def sshCmd(String server, String command){
    def common = new com.webops.Common()
    def sshArgs = 'set +x; ssh -F + -tt'
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
${sshArgs} ${server} \"export TERM=xterm-256color; 
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

def inputParams(params){
   def userInput
   def env
   timeout(time: 120, unit: 'SECONDS') {  
    if(params.string){
        input message: '', parameters: [string(name: params.string.name)]
        env["${params.string.name}"] = params.string.name
    }
    if(params.choice){
        input message: '', parameters: [choice(name: params.choice.name, choices: params.choice.choices)] 
        env["${params.choice.name}"] = params.choice.name
    }
    if(params.password){
        input parameters: [password(name: '')]
        env["${PASSWORD}"] = params.password.name
    }   
   }    
     
}

def sshScp(source, destination, options=null){
  def common = new com.webops.Common()
  def sshArgs = '-F +'
    sshCmd("scp ${sshArgs} ${source} ${destination}")
}

    
