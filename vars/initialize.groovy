import com.webops.*;


def call(){
  def yamlName
  node{
    deleteDir()
    def common = new com.webops.Common()
    .gitCheckout 'mbiscarra/legacy-task.git', 'prd-private-gitlab', 'script'
    .loadKey()

    stage('task'){  
      def folders = sh(returnStdout: true, script: "ls $workspace/runbook").replaceAll(".yml", "")
      yamlName = input(id: 'tasklist', message: 'task', parameters: [
      [$class: 'ChoiceParameterDefinition', choices: "${folders}", description: '', name: '']]) 

    }
  }
  node{
    taskRunner(yamlName)
  }
}  

