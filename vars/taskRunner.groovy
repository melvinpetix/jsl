import com.webops.*;


def call(yamlName){

  def common = new Common()
  common.loadKey()
  
  def userInput
  
  def yaml = readYaml file: 'runbook/' + yamlName + '.yml'
  
  println yaml.toString()
  
  try{ 
    
    if(yaml.parameters){
      common.stage('define job parameters'){
        def inputPrompt = common.parseParams yaml.parameters
        timeout(time: 120, unit: 'SECONDS') {
          userInput = input parameters: inputPrompt     
       } 
       userInput.each{x,v-> env."$x"="$v"}      
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
     
