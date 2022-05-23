@Library(value="github.com/melvinpetix/jsl@main", changelog=false)_
import com.webops.*;


def call(string yamlName){
    def common = new com.webops.Common()
    .loadKey()

    def yaml = readYaml file: "${workspace}/runbook/" + yamlName + ".yml"

    if(yml.parameters){
        inputParameter(runbook)
    }  
    if(yml.environment){
        j.environment.collectEntries { name, value ->
            [name, value instanceof String ? interp(value) : value]
        }
    }
    if(yml.notification){
        def by = "${currentBuild.getBuildCauses()[0].userId}"
        common.sendTeamsNotif("started: ${by}", j.project_name, j.notification.webhook)
    }
    if(yml.environment){
        yml.environment.collectEntries { name, value ->
            [name, value instanceof String ? interp(value) : value]
        }
    }
    
    if(!yml.steps){
        currentBuild.description == 'test/update'
        return    
    }  
    else {
        list stepsA = yml.steps
        
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
   
