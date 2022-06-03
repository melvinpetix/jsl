def call(yamlName){
    def userInput
    def j = readYaml file: "runbook/${yamlName}.yml"
    j.parameters.each{params->
        switch(params.type){
            case 'string':
            userInput = input message: '', parameters: [string(description: "${params.description}", name: "${params.name}")]
            break
            case 'choice':
            userInput = input message: '', parameters: [choice(description: "${params.description}", name: "${params.name}", 
            choices: params.choices.toString())]
            break
        }
        
    }
   return env["${params.name}"] = userInput   
}
    
    
   /* 
    if(j.parameters.type == ){
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
    }
}      

*/
