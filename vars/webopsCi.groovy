def call(){
  
  node("${env.jenkins_agent}"){  
    
    deleteDir()
    
    git branch: 'main', url: 'https://oauth:glpat-GxfR6J-STGecxjDPGz8z@gitlab.com/me1824/jsl.git'
    sh 'chmod 600 snp.key; chmod 600 +'   
    
    stage('define task'){
      def folders = sh(returnStdout: true, script: "ls $workspace/runbook").replaceAll(".yml", "")
      writeFile file:'task.txt', text: "${folders}"
      tasklist = readFile("$workspace/task.txt") 
      userInput = input(id: 'tasklist', message: 'task', parameters: [
      [$class: 'ChoiceParameterDefinition', choices: "${tasklist}", description: '', name: 'tasklist']]) 
    }
    
    def j = jobCfg("$workspace/runbook/${userInput}.yml")
    list stepsA = j.steps
    list enV = j.environment

    if(j.environment){
      enV.collect{a,b->
        withEnv(["${a}=${b}"]){
          stepsA.collect{k,v->
            stage("${k}"){
              v.each{command->
                sh "ssh -F + ${server} '${command}'"
              }
            }
          }
        }   
      }
    } else{
      stepsA.collect{k,v->
        stage("${k}"){
          v.each{command->
            if(!j.server){
              sh script: "${command}"
            } else {
              def server = j.server."${k}"
              sh"""#!/bin/bash +x\n\
              export TERM=xterm-256color\n\
              ssh -F + ${server} '${command}'
              """ 
            }                      
          }
        }   
      }
    }  
  }
}
def gitCheckout(String repo, String credentialsId, String branch='master') {
  checkout([$class: 'GitSCM', branches: [[name: "*/${branch}"]], 
  userRemoteConfigs: [[credentialsId: credentialsId, url: 'git@gitlab.usautoparts.io:' + repo]]])
  sh "chmod +x *.sh; chmod 600 *.key; chmod 600 +" 
}
