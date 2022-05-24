def call(yamlName){
    j = readYaml file: "${workspace}/runbook/" + yamlName + ".yml"
    if(j.parameters.string){
       userinput = input parameters: [string(defaultValue: '', description: j.parameters.string.description ,name: j.parameters.string.name)]
       env["${j.parameters.string.name}"] = userInput
       
    }
    
    

    if(j.parameters.choice){
        def choices = j.parameters.choice.choices.replaceAll(',',"\n")
        params = input(id: '', message: "${j.parameters.choice.name}", 
        parameters: [[$class: 'ChoiceParameterDefinition', 
        choices: "${choices}", description: '', 
        name: j.parameters.choice.name]])  
        sh "set +x; echo \"${j.parameters.choice.name}=${params}\" >> config.sh"
    }

}


