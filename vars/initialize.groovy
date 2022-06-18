@Library(value="github.com/melvinpetix/jsl@main", changelog=false)_

def call(){
   if(!params.runbook || params.runbook == 'null'){ 
   node {    
      checkout scm  
      def folders = sh(returnStdout: true, 
      script: "ls $WORKSPACE/runbook").replaceAll(".yml", "")  
      String jp = """\n${folders}\n""" 
      properties([parameters([choice(choices: "${jp}", name: 'runbook')])])   
   } else {
    node(jenkins_agent){  
     taskRunner params.runbook   
  }
}
