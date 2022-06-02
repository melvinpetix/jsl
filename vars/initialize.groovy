import com.webops.*;

def call(String repo=null, String branch=null) {
  def yamlName
  
  node{
    def common = new Common()
    common.gitClone("${branch}", "${repo}")
    stage('define task'){
      def folders = sh(returnStdout: true,  script: "ls $workspace/runbook").replaceAll(".yml", "")
      yamlName = input(id: 'tasklist', message: 'task', parameters: [
      [$class: 'ChoiceParameterDefinition', choices: "${folders}", description: '', name: '']]) 
      deleteDir()
    }
  }
  node("${env.jenkins_agent}"){
      def common = new Common()
      deleteDir()
      common.gitClone("${branch}", "${repo}")
      taskRunner(yamlName
    )
  } 
}
