@Library(value="github.com/melvinpetix/jsl@main", changelog=false)_
import com.webops.*;

def call(yamlName){
    
  def common = new Common()
  common.loadKey()
  
  def userInput
  
  if(!yamlName || yamlName == 'debug'){ 
    try { interactiveShell() } catch(err){ 
      currentBuild.description = 'test/debug'
      currentBuild.result = 'SUCCESS'
      return   
    }
  } 

  def yaml = readYaml file: 'runbook/' + yamlName + '.yml'
  
  try{   
    if(yaml.parameters){          
      def myProps = readMyProps yaml.parameters
        
      timeout(time: 120, unit: 'SECONDS') {
        input parameters: myProps         
      }    
        populateEnv
    }             
    if(yaml.environment){
      yaml.environment.each{env->
        env.collect{k,v-> env."${k}"="${v}"}
      }
    }
    if(yaml.notification){
      def userName = "${currentBuild.getBuildCauses()[0].userId}"
      common.sendTeamsNotif(
        msg: "Started by: ${userName}", 
        job: "${yaml.project_name}", 
        url: "${yaml.notification.webhook}"
      )
    }

    if(!yaml.steps){
      currentBuild.description = 'test/update'
      currentBuild.result = 'SUCCESS'
      return    
    }  
      
    else {
      list steplist = yaml.steps
      steplist.each{step->
        common.stage(step.name){
          list commands = step.command
          commands.each{command->
            common.execute(cmd: command, server: step.server) 
          }
        }
      }  
    }
  } 
  catch(err){
      def msg = "execution failed with the following error\n"
      println err
      currentBuild.result = 'FAILURE'
      deleteDir()      
  }

  deleteDir()  
}

@NonCPS
def readMyProps(parameters) {
    parameters.collect { params ->
      this.invokeMethod params.type, params.args.collectEntries { name, value ->
        [name, value.toString() ]
      }
    }
 }

@NonCPS
    def populateEnv(){ binding.variables.each{k,v -> env."$k" = "$v"} }

