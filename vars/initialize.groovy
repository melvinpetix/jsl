import com.webops.*;

def call(String repo=null, String branch=null) {
  def yamlName
  def common
  
  node{
    deleteDir()
    common = new com.webops.Common()
    common.gitClone("${branch}", "${repo}")
    common.loadKey()
    stage('task'){
      def folders = sh(returnStdout: true,  script: "ls $workspace/runbook").replaceAll(".yml", "")
      yamlName = input(id: 'tasklist', message: 'task', parameters: [
      [$class: 'ChoiceParameterDefinition', choices: "${folders}", description: '', name: '']]) 
    }
  }
  stage 'task runner'
  node("${env.jenkins_agent}"){
      taskRunner(yamlName)
  } 
}
