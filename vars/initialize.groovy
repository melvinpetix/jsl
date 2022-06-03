import com.webops.*;

def call(String repo, String branch) {
  def yamlName
  node("${env.jenkins_agent}"){
    deleteDir()  
    def common = new Common()
    common.gitClone("${repo}", "${branch}")
    stage('define task'){
      def folders = sh(returnStdout: true,  
      script: "ls $workspace/runbook").replaceAll(".yml", "")
      yamlName = input(message: 'runbook', parameters: [
            [$class: 'ChoiceParameterDefinition', 
            choices: "${folders}", 
            description: '', name: '']])   
    }  
    
    taskRunner(yamlName)
  } 
}
