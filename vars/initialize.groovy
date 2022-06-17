@Library(value="github.com/melvinpetix/jsl@main", changelog=false)_

def call(){
 
 node {
    
  checkout scm
  
    def folders = sh(returnStdout: true, 
    script: "ls $WORKSPACE/runbook").replaceAll(".yml", "")
  
    String jp = """\n${folders}\n""" 

    properties([parameters([choice(choices: "${jp}", name: 'runbook')])])
     
    taskRunner params.runbook 
  } 
     
}
