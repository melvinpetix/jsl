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
      common.checkoutRepo(
      repo: 'mbiscarra/legacy-task.git', 
      branch: 'script')
      common.loadKey()
    }  
    stage 'task'
      def folders = sh(returnStdout: true,  script: "ls $workspace/runbook").replaceAll(".yml", "")
      yamlName = input(id: 'tasklist', message: 'task', parameters: [
      [$class: 'ChoiceParameterDefinition', choices: "${folders}", description: '', name: '']]) 
  }
    stage 'task2'
      node("${env.jenkins_agent}"){
        taskRunner(yamlName)
      } 
