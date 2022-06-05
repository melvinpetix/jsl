def call(String repo, String branch) {
  def yamlName
  node("${env.jenkins_agent}"){
    deleteDir()  
    git branch: "${branch}", url: "${repo}"
    stage('define runbooks'){
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
