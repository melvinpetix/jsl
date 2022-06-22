import com.webops.*;


def call(yamlName){
  
  def yaml = readYaml file: 'runbook/' + yamlName + '.yml'
  
  def common = new Common()
  
  if(!yamlName || yamlName == 'null'){ 
    try { groovyShell() } catch(err){ 
      currentBuild.description = 'test/debug'
      currentBuild.result = 'SUCCESS'
      return   
    }
  } 
  
  try{ 

    if(yaml.parameters){
     def userInput    
     def inputPrompt = common.parseParams yaml.parameters
     timeout(time: 120, unit: 'SECONDS') {
       userInput = input parameters: inputPrompt     
     } 
      userInput.each{x,v-> env."$x"="$v"}      
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
   
