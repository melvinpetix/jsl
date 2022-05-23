@Library(value="github.com/melvinpetix/jsl@main", changelog=false)_
import com.webops.*;


def call(string yamlName){
    def common = new com.webops.Common()
    .loadKey()

    def yaml = readYaml file: "${workspace}/runbook/" + yamlName + ".yml"

    if(yaml.parameters){
        inputParameter(runbook)
    }  
    if(yaml.environment){
        yaml.environment.collectEntries { name, value ->
            [name, value instanceof String ? interp(value) : value]
        }
    }
    if(yaml.notification){
        def by = "${currentBuild.getBuildCauses()[0].userId}"
        common.sendTeamsNotif("started: ${by}", yaml.project_name, yaml.notification.webhook)
    }
    if(yaml.environment){
        yaml.environment.collectEntries { name, value ->
            [name, value instanceof String ? interp(value) : value]
        }
    }
    
    if(!yaml.steps){
        currentBuild.description == 'test/update'
        return    
    }  
    else {
        list stepsA = yaml.steps
        
        stepsA.each{step->
            list commands = step.command
            commands.each{command->
                buildStage(step.name){
                    execute server: step.server,
                    cmd: command
                }
            }
        }
    }
}
   
