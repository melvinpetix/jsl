#!groovy
@Library(value="github.com/melvinpetix/jsl@main", changelog=false)_
import com.webops.*;
def common


def call(yamlName){
    common = new com.webops.Common()
    common.gitClone('gitlab.com/me1824/jsl', 'glpat-GxfR6J-STGecxjDPGz8z', 'test')
    //.gitCheckout 'mbiscarra/legacy-task.git', 'prd-private-gitlab', 'script'
    common.loadKey()
   
    try{
    def yaml = readYaml file: 'runbook/' + yamlName + '.yml'
    
    if(yaml.parameters){
        
       build('parameters'){ build.params(yamlName) }
        //def closure = { build.params(yamlName) 
    }      
        
    if(yaml.environment){
        yaml.environment.each{env->
            env.collect{k,v-> env."${k}"="${v}"}
        }
    }
    if(yaml.notification){
        def userName = "${currentBuild.getBuildCauses()[0].userId}"
        build.notifier('Started by: ' + userName, yaml.project_name, yaml.notification.webhook)
    }
        
    if(!yaml.steps){
        currentBuild.description = 'test/update'
        currentBuild.result = 'SUCCESS'
        return    
    }  
    else {
        list steplist = yaml.steps
            node("maintenance-script-ec2-spot-worker"){
                steplist.each{step->
                    build(step.name){
                        list commands = step.command
                        commands.each{command->
                             build.execute server: step.server,
                                      cmd: command
                        }
                    }
                }
            }
        }
    } 
    
    catch(err){
        println err
        currentBuild.result = 'FAILURE'
        deleteDir()
    }
    deleteDir()
}
   
             //inputParams(yamlName)
