import com.webops.*;

def call(){
  node("${env.jenkins_agent}"){  
    def common = new com.webops.Common()
    def BUILD_TRIGGER_BY = "${currentBuild.getBuildCauses()[0].shortDescription} / ${currentBuild.getBuildCauses()[0].userId}" 
    def cause = currentBuild.getBuildCauses('hudson.model.Cause$UserIdCause')

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

    stage("${userInput}"){
      def j = jobCfg("$workspace/runbook/${userInput}.yml")
      list stepsA = j.steps
      list enV = j.environment
      def params = ''
       
      if(j.parameters){
       println j.parameters
       println j.parameters.type
       println j.parameters.name
       if(j.parameters.type == 'string'){
        params = input(id: 'String1', message: "${j.parameters.name}", parameters: [
           [$class: 'StringParameterDefinition', description: '', name: '', trim: 'true' ]])  
        }
      }
      j.parameters.name = "${params}"
      println "${j.parameters.name}"
      
      /*
      if(j.parameters == 'string'){
         params = input(id: 'String1', message: '', parameters: [
         [$class: 'StringParameterDefinition', description: '', name: '', trim: 'true' ]])  
      }
      if(j.parameters == 'choice'){
         params = input(id: 'String1', message: '', parameters: [
         [$class: 'ChoiceParameterDefinition', choices: j.choices, description: '', name: '']])   
      }
       

      if(j.parameters){
        if(j.parameter.type == 'string'){
           j.parameter.name = input(id: 'String1', message: '', parameters: [
           [$class: 'StringParameterDefinition', description: '', name: j.parameter.name, trim: 'true' ]])  
        }
        else if(j.parameter.type == 'choice'){
           j.parameter.name = input(id: 'snap', message: 'snapshot', parameters: [
           [$class: 'ChoiceParameterDefinition', choices: j.parameter.choices, description: '', name: '']])  
       
        }
       }
      */
   /*   
    if(userInput == 'test_mt'){
     def snapshot = sh(returnStdout: true, script: "ssh -F + csendrepo01 'ls /data/ENDECA_DATA_REPO_6.5/FULL/MERGE'").trim()
     writeFile file:'snapshot.txt', text: "${snapshot}"
     snaplist = readFile("$workspace/snapshot.txt") 
     def snapshot_date = input(id: 'snap', message: 'snapshot', parameters: [
     [$class: 'ChoiceParameterDefinition', choices: "${snaplist}", description: '', name: '']])  
       stepsA.collect{k,v->
        stage("${k}"){
          v.each{command->
            if(!j.server){
               sh script: "${command}"
            } else {
              def server = j.server."${k}"
              sh"""#!/bin/bash +x
              export TERM=xterm-256color
              export snapshot_date="${snapshot_date}"
              ssh -F + ${server} "${command}"
              """ 
            }                      
          }
        }   
      }
    }

    */ 
   // println "${snapshot_date}"

    if(j.notification){
      common.sendTeamsNotif("${BUILD_TRIGGER_BY}", j.project_name, j.notification.webhook)
     }
    
    if(j.environment){
      enV.collect{a,b->
        withEnv(["${a}=${b}"]){
          stepsA.collect{k,v->
            stage("${k}"){
              v.each{command->
                sh"""#!/bin/bash +x
              export TERM=xterm-256color
              export snapshot_date="${params}"
              ssh -F + ${server} "${command}"
              """ 
              }
            }
          }
        }   
      }
    } else{
      stepsA.collect{k,v->
        stage("${k}"){
          v.each{command->
            if(!j.server || !j.server."${k}" || j.server."${k}" == 'local'){
               sh script: "${command}"
            } else {
              def server = j.server."${k}"
              sh"""#!/bin/bash +x
              export TERM=xterm-256color
              export snapshot_date="${params}"
              ssh -F + ${server} "${command}"
              """ 
            }                      
          }
        }   
      }
    }  
  }
}
}

