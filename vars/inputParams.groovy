def call(yamlName){
        def userInput
        readYaml file: "${workspace}/runbook/" + yamlName + ".yml"

        if(j.parameters.string){
            userInput = input parameters: [string(defaultValue: '', 
            description: j.parameters.string.description, 
            name: j.parameters.string.name)]
            env["${j.parameters.string.name}"] = userInput   
        }
        if(j.parameters.choice){
            def choices = j.parameters.choice.choices.replaceAll(',',"\n")
            userInput = input parameters: [
            choice(choices: choices ,name: j.parameters.choice.name)]
            env["${j.parameters.choice.name}"] = userInput    
        }
        if(j.parameters.password){
             P4SSWORD = input parameters: [
             password(name: 'P4SSWORD')] 
             env["${j.parameters.password.name}"] = P4SSWORD
        }
         
        else {
                echo "build has no parameters"        
        }
}
