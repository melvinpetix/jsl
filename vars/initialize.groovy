@Library(value="github.com/melvinpetix/jsl@main", changelog=false)_

def call(){
  node(jenkins_agent){        
     checkout scm  
     taskRunner params.runbook 
  }
}

