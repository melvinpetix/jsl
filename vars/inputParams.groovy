def call(Map config){
    def type = config.type
    def name = config.name
    def args = config.args
    
    if(type == 'string'){
        userInput = input(id: 'string', message: "${name}", parameters: [[$class: 'StringParameterDefinition', defaultValue: '', 
        description: "${args}", name: '', trim: true]]) 
        sh "set +x; echo \"${name}\"=\"${userInput}\" >> .env"       
    }
    if(type == 'choice'){
        def choices = config.args.toString.replaceAll(',','\n')
        userInput = input(id: 'choice', message: "${name}", parameters: [[$class: 'ChoiceParameterDefinition', 
        choices: "${choices}", name: parameterName]])
        sh "set +x; echo \"${name}\"=\"${userInput}\" >> .env"
    }
    if(type == 'password'){
        userInput = input(id: 'string', message: "${name}", parameters: [
         [$class: 'PasswordParameterDefinition', name: "Password"]]) 
         sh "set +x; echo \"${name}\"=\"${userInput}\" >> .env"
    }
    
}

  


/*

if(builder.parameters.string){
        userInput = input(id: 'string', message: parameterName, parameters: [[$class: 'StringParameterDefinition', defaultValue: '', 
        description: "${builder.parameters.string.name}", name: '', trim: true]]) 
        if(userInput){
           sh 'set +x; echo \"${builder.parameters.string.name}=${userInput}\" >> .env'
        }
    } 
   if(builder.parameters.choice){       
        def choices = []
        choices = builder.parameters.choice.choices.toString().replaceAll(',','\n')
        userInput = input(id: '', message: "${builder.parameters.choice.name}", 
        parameters: [[$class: 'ChoiceParameterDefinition', 
        choices: "${choices}", name: "${builder.parameters.choice.name}"]])  
        if(userInput){
            sh 'set +x; echo \"${builder.parameters.choice.name}=${userInput}\" >> .env'       
        }   
    }    
    if(builder.parameters.password){
        PASSWORD = input(id: 'password', message: '', parameters: [
        [$class: 'PasswordParameterDefinition', name: "Password"]])
        if(PASSWORD){
            sh 'set +x; echo \"${builder.parameters.choice.name}=${PASSWORD}\" >> .env' */
