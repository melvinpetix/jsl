#!groovy
@Library(value="github.com/melvinpetix/jsl@main", changelog=false)_
import com.webops.*;


def call(yamlName){
    def common = new com.webops.Common()
    common.loadKey()
    
    try{
        
        def yaml = readYaml file: 'runbook/' + yamlName + '.yml'
    
        if(yaml.parameters){
            common.stage("${yaml.project_name}  parameters"){ common.buildParams(yamlName) }
                        
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
                list commands = step.command
                commands.each{command->
                    build(step.name){
                        build.execute server: step.server,
                        cmd: command
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
