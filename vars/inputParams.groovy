def call(params){
    if(string){
        input message: '', parameters: [string(name: params.string.name)]
    }
    if(choice){
        input message: '', parameters: [string(name: params.string.choice.name)]
    }
}
    
    
    /*
    def userInput
    def j = readYaml file: "runbook/${yamlName}.yml"

    if(string){
        userInput = input parameters: [string(defaultValue: '', 
        description: j.parameters.string.description, 
        name: j.parameters.string.name)]
        env["${j.parameters.string.name}"] = userInput
    }
    if(j.parameters.choice){
        def choices = j.parameters.choice.choices.toString().replaceAll(',',"\n")
        userInput = input parameters: [
        choice(choices: choices ,name: j.parameters.choice.name)]
        env["${j.parameters.choice.name}"] = userInput    
    }
    if(j.parameters.password){
         P4SSWORD = input parameters: [
         password(name: 'P4SSWORD')] 
         env["${j.parameters.password.name}"] = P4SSWORD
    */
    }
    
}      
