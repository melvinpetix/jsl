def call(yamlName){
    def userInput
    j = readYaml file: "${workspace}/runbook/" + yamlName + ".yml"
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
       userInput = input parameters: [
       password(name: j.parameters.password.name)] 
       env["${j.parameters.password.name}"] = userInput
    }
    
}

