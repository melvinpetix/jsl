def call(yaml){
    builder = jobCfg("${yaml}")
    def userInput
    
    if(builder.parameters.string){
        userInput = input(id: 'string', message: "${builder.parameters.string.name}", 
        parameters: [[$class: 'StringParameterDefinition', defaultValue: '', 
        description: "${builder.parameters.string.name}", name: '', trim: true]]) 
        sh "set +x; echo ${builder.parameters.string.name}=${userInput} >> .env"
    } 
    if(builder.parameters.choice){       
        def choices = []
        choices = builder.parameters.choice.choices.toString().replaceAll(',','\n')
        params = input(id: '', message: "${builder.parameters.choice.name}", 
        parameters: [[$class: 'ChoiceParameterDefinition', 
        choices: "${choices}",n ame: "${builder.parameters.choice.name}"]])  
        sh "set +x; echo ${builder.parameters.choice.name}=${params} >> .env"
    }
    if(builder.parameters.password){
        PASSWORD = input(id: 'password', message: '', parameters: [
        [$class: 'PasswordParameterDefinition', name: "Password"]])
        sh "set +x; echo PASSWORD=${params} >> .env"
    }
  
}

