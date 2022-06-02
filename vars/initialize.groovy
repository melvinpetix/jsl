import com.webops.*;

def call(String repo=null, String branch=null) {
  def yamlName
  node("${env.jenkins_agent}"){
    deleteDir()  
    def common = new Common()
    common.gitClone("${branch}", "${repo}")
    stage('define task'){
      def folders = sh(returnStdout: true,  
      script: "ls $workspace/runbook").replaceAll(".yml", "")
      yamlName = input(message: 'tasklist', parameters: [
            [$class: 'ChoiceParameterDefinition', 
            choices: "${folders}", 
            description: '', name: '']])   
    }  
    
    taskRunner(yamlName)
  } 
}
