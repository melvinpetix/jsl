@Library(value="github.com/melvinpetix/jsl@main", changelog=false)_
import com.webops.*;

def call(yamlName){
    
    def common = new Common()
    common.loadKey()
   
    if(!yamlName || yamlName == 'debug'){ 
        try { interactiveShell() } catch(err){ 
            currentBuild.description = 'test/debug'
            currentBuild.result = 'SUCCESS'
            return   
        }
    }   
    
    try{  
        
        def yaml = readYaml file: 'runbook/' + yamlName + '.yml'
        
        if(yaml.parameters){
            yaml.parameters.each{params->
                timeout(time: 120, unit: 'SECONDS') {  
                    switch(params.type){
                        case 'string':
                            input message: '', parameters: [string(name: params.name)]; 
                        break    
                        case 'choice':
                            input message: '', parameters: [choice(name: params.name, choices: params.choices)]; 
                        break
                        case 'password':
                            input parameters: [password(name: '')]; 
                        break           
                    }
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
    

