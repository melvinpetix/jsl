import com.webops.*;
def common = new com.webops.Common()

def call(body){
  def config = body
  def yamlName
  node{
    deleteDir()
    if(!config.repo){
      checkout scm
    } else {
      common.gitClone("test", "https://gitlab.com/me1824/jsl.git")
    }  
    
    common.loadKey()
    
    stage 'task'
      def folders = sh(returnStdout: true,  script: "ls $workspace/runbook").replaceAll(".yml", "")
      yamlName = input(id: 'tasklist', message: 'task', parameters: [
      [$class: 'ChoiceParameterDefinition', choices: "${folders}", description: '', name: '']]) 
  }
    stage 'task2'
      node("${env.jenkins_agent}"){
        taskRunner(yamlName)
      } 
}

def checkoutRepo(body) {
  def config = body
  

  checkout([
    $class: 'GitSCM',
    branches: [[name: "*/${branch}"]],
    extensions: [[$class: 'RelativeTargetDirectory', 
    relativeTargetDir: '.']],
    userRemoteConfigs: [[
      url: 'git@gitlab.usautoparts.io:' + repo]]])
   
  checkout([$class: 'GitSCM',
      branches: [[name: config.branch]],
      extensions: [[ ]],
      userRemoteConfigs: [[
          credentialsId: 'prd-private-gitlab', 
          
      ]]
   ])     
}
