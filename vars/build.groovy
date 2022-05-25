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
  def args = "ssh -F + -tt"
  sh """#!/bin/bash +x;  
  export \$(cat config.sh); 
  ${args} ${server} \"export TERM=xterm-256color; 
  set -x; ${command}\"
  """
}
