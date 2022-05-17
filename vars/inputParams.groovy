def call(yaml){
    builder = jobCfg("${yaml}")
    def userInput
    
    if(builder.parameters.string){
        userInput = input(id: 'string', message: "${builder.parameters.string.name}", 
        parameters: [[$class: 'StringParameterDefinition', defaultValue: '', 
        description: "${builder.parameters.string.name}", name: '', trim: true]]) 
        sh """set +x; echo 'export ${builder.parameters.string.name}=${userInput} >> .env"""
    } 
    if(builder.parameters.choice){       
        def choices = []
        choices = builder.parameters.choice.choices.toString().replaceAll(',','\n')
        userInput = input(id: '', message: "${builder.parameters.choice.name}", 
        parameters: [[$class: 'ChoiceParameterDefinition', 
        choices: "${choices}", name: "${builder.parameters.choice.name}"]])  
        sh """set +x; echo 'export ${builder.parameters.choice.name}=${userInput}"""
        
        writeFile file: "${builder.parameters.choice.name}.params", text: """
        export ${builder.parameters.choice.name}=${userInput} >> .env"""
        
    }
    if(builder.parameters.password){
        PASSWORD = input(id: 'password', message: '', parameters: [
        [$class: 'PasswordParameterDefinition', name: "Password"]])
        sh """set +x; echo export ${builder.parameters.choice.name}=${userInput} >> .env"""
        
    }
  
}

