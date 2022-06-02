import com.webops.*;
def common = new com.webops.Common()

def call(String repo=null, String branch=null) {
  def yamlName
  node{
    deleteDir()
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
