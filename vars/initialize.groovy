def call(){
  node(jenkins_agent){        
     checkout scm
      stage 'parse pipeline config'
     taskRunner params.runbook 
  }
}

