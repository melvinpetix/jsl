#!groovy
@Library(value="github.com/melvinpetix/jsl@main", changelog=false)_
import com.webops.*;


def call(yamlName){
    def yaml
    try{
    def common = new com.webops.Common()
    .loadKey()
    
    yaml = readYaml file: "runbook/${yamlName}.yml"
    
    if(yaml.parameters){
        build('parameters'){ inputParams(yamlName) }
    }   
    if(yaml.environment){
        yaml.environment.each{env->
            env.collect{k,v-> env."${k}"="${v}"}
        }
    }
    if(yaml.notification){
        def by = "${currentBuild.getBuildCauses()[0].userId}"
        msTeamsNotif("started: ${by}", yaml.project_name, yaml.notification.webhook)
    }
        
    if(yaml.environment){
        yaml.environment.collect { name, value ->
            [env."${name}"="${value}"]
        }
    }
    
    if(!yaml.steps){
        currentBuild.description == 'test/update'
        return    
    }  
    else {
        list stageSteps = yaml.steps
        stageSteps.each{step->
            list commands = step.command
            commands.each{command->
                common.build(stageStep.name){
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
