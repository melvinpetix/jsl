#!groovy
@Library(value="github.com/melvinpetix/jsl@main", changelog=false)_
import com.webops.*;


def call(yamlName){
    def common = new com.webops.Common()
    common.loadKey()
    
    try{
        
        def yaml = readYaml file: 'runbook/' + yamlName + '.yml'
    
        if(yaml.parameters){
            common = new com.webops.Common()
            .Stage(yaml.project_name){ common.buildParams(yamlName) }          
        }      
        
        if(yaml.environment){
            yaml.environment.each{env->
                env.collect{k,v-> env."${k}"="${v}"}
            }
        }
        if(yaml.notification){
            def userName = "${currentBuild.getBuildCauses()[0].userId}"
            common.sendTeamsNotif(m: "Started by: ${userName}", 
                                  j: "${yaml.project_name}", 
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
                node(env.jenkins_agent){
                    common.Stage(step.name){
                    list commands = step.command
                    commands.each{command->
                        common.execute(cmd: command, server: step.server)
                        
                }
            }
        }
    }
        }        
    } catch(err){
        println err
        currentBuild.result = 'FAILURE'
        deleteDir()
    }
    deleteDir()
}
   
             //inputParams(yamlName)
