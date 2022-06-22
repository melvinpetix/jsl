def call(){
  node(jenkins_agent){        
     sh 'ls -al'
     taskRunner params.runbook 
  }
}

