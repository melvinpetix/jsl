def call(){
  node(jenkins_agent){        
     checkout scm  
     taskRunner params.runbook 
  }
}

