#!groovy
@Library(value="github.com/melvinpetix/jsl@main", changelog=false)_
import com.webops.*;


def call(yamlName){
    def common = new com.webops.Common()
    common.loadKey()
    
    if(yamlName == "debug"){ 
        interactiveShell()
        currentBuild.description = 'test/debug'
        currentBuild.result = 'SUCCESS'
        return   
    }
    
    try{    
        def yaml = readYaml file: 'runbook/' + yamlName + '.yml'
        
        if(yaml.parameters){
            common.Stage("${yaml.project_name}  parameters"){ common.buildParams(yamlName) }             
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
                url: "${yaml.notification.webhook}")
       
        }
        
        if(!yaml.steps){
            currentBuild.description = 'test/update'
            currentBuild.result = 'SUCCESS'
            return    
        }  
        else {
            list steplist = yaml.steps
            steplist.each{step->
               common.Stage(step.name){
                   if(step.commands){
                       step.commands.each{c->
                           c.command.each{command->
                               common.execute(cmd: command, server: step.server)
                           }
                       }
                   } else {
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
   
             //inputParams(yamlName)
