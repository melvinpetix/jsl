import com.webops.*;

def call(yamlName){  
  
  def common = new Common()
  
  def yaml = readYaml file: 'runbook/' + yamlName + '.yml'
  
  common.loadKey()

  if(!yamlName || yamlName == 'null'){ 
    try { groovyShell() } catch(err){ 
      currentBuild.description = 'test/debug'
      currentBuild.result = 'SUCCESS'
      return   
    }
  } 
  
  try{ 

    if(yaml.parameters){
      def inputPrompt = parseParams yaml.parameters
      timeout(time: 120, unit: 'SECONDS') {
        yaml.parameters.each{params->
          params.args.name = input parameters: inputPrompt 
        }      
      }             
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
def parseParams(parameters) {
  parameters.collect { params ->
    this.invokeMethod params.type, 
    params.args.collectEntries { name, value ->
      [
        name, value instanceof String ? exportEnv(value) : value
      ]
    }
  }
}

@NonCPS
def exportEnv(value) {
  new groovy.text.GStringTemplateEngine()
    .createTemplate(value)
    .make([env:env])
    .toString()
}        
     
    /*
    if(yaml.parameters){          
      def myProps = common.parseParams yaml.parameters
      timeout(time: 120, unit: 'SECONDS') {
        input parameters: myProps         
      }      
    } 
     
    if(yaml.parameters){
       common.stage("${yamlName} parameters"){
           yaml.parameters.each{params->
               common.inputParams(params)
            }
       }    
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
   */
