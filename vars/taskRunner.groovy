#!groovy
@Library(value="github.com/melvinpetix/jsl@main", changelog=false)_
import com.webops.*;


def call(yamlName){
    try{
    def common = new com.webops.Common()
    .loadKey()
    
    def yaml = readYaml file: 'runbook/' + yamlName + '.yml'
    
    if(yaml.parameters){
        common.buildStage('parameters'){ common.buildParams(yamlName) }
    }   
    if(yaml.environment){
        yaml.environment.each{env->
            env.collect{k,v-> env."${k}"="${v}"}
        }
    }
    if(yaml.notification){
        def userName = "${currentBuild.getBuildCauses()[0].userId}"
        common.sendTeamsNotif('Started by ' + userName, yaml.project_name, yaml.notification.webhook)
    }
        
    if(!yaml.steps){
        currentBuild.description = 'test/update'
        currentBuild.result = 'SUCCESS'
        return    
    }  
    else {
        list stageSteps = yaml.steps
        stageSteps.each{step->
            list commands = step.command
            commands.each{command->
                common.buildStage(stageStep.name){
                   common.execute server: stageStep.server,
                   cmd: command
                }
            }
        }
    }
    } catch(err){
        currentBuild.result = 'FAILURE'
        deleteDir()
    }
    deleteDir()
}
   
             //inputParams(yamlName)
