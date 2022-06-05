@Library(value="github.com/melvinpetix/jsl@main", changelog=false)_

def call(String repo, String branch) {
  def yamlName
  node("${env.jenkins_agent}"){
    deleteDir()  
    git branch: "${branch}", url: "${repo}"
    //stage('define runbooks'){
    def folders = sh(returnStdout: true,  
    script: "ls $workspace/runbook").replaceAll(".yml", "")      
    taskRunner(yamlName)
  }     
}
