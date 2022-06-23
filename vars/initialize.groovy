import com.webops.*;

def call(){
  if(!params.runbook || params.runbook == 'null'){
    try{
      node{
        checkout scm
          listPipelines()
          groovyShell()    
      }
    } catch(err){ 
       currentBuild.description = 'test/debug'
       currentBuild.result = 'SUCCESS'
       return   
    }
  } else {
    node(jenkins_agent){        
      checkout scm
       taskRunner params.runbook 
    }
  }
}

