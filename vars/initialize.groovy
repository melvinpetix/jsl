import com.webops.*;

def call(){
  node(jenkins_agent){        
    checkout scm
    def common = new Common() 
    .loadKey() 
    .stage(params.runbook){
       taskRunner params.runbook 
    }
  }
}

