import com.webops.*;
def common = new com.webops.Common()


def call(String repo=null, String branch=null) {
  def yamlName
  
  node{
    common.gitClone("${branch}", "${repo}")
    common.loadKey()
    stage('task'){
      def folders = sh(returnStdout: true,  script: "ls $workspace/runbook").replaceAll(".yml", "")
      yamlName = input(id: 'tasklist', message: 'task', parameters: [
      [$class: 'ChoiceParameterDefinition', choices: "${folders}", description: '', name: '']]) 
      deleteDir()
    }
  }
  stage 'start taskRunner'
    node("${env.jenkins_agent}"){
      deleteDir()
      checkout S
      taskRunner(yamlName)
  } 
}
