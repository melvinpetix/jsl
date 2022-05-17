def call(yaml){
    builder = jobCfg("${yaml}")
    def userInput
    
    if(builder.parameters.string){
        userInput = input(id: 'string', message: "${builder.parameters.string.name}", 
        parameters: [[$class: 'StringParameterDefinition', defaultValue: '', 
        description: "${builder.parameters.string.name}", name: '', trim: true]]) 
        writeFile file: "${builder.parameters.string.name}.params", text: """
        ${builder.parameters.string.name}=${userInput}"""
    } 
    if(builder.parameters.choice){       
        def choices = []
        choices = builder.parameters.choice.choices.toString().replaceAll(',','\n')
        userInput = input(id: '', message: "${builder.parameters.choice.name}", 
        parameters: [[$class: 'ChoiceParameterDefinition', 
        choices: "${choices}", name: "${builder.parameters.choice.name}"]])  
        writeFile file: "${builder.parameters.choice.name}.params", text: """
        ${builder.parameters.choice.name}=${userInput}"""
        
    }
    if(builder.parameters.password){
        PASSWORD = input(id: 'password', message: '', parameters: [
        [$class: 'PasswordParameterDefinition', name: "Password"]])
        writeFile file: 'password', text: """
        ${builder.parameters.password.name}=${PASSWORD}"""
        
    }
  
}

