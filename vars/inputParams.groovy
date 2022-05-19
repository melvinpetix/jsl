def call(Map config){
    def type = config.type
    def parameterName = config.name
    def choices = config.choices.toString.replaceAll(',','\n')
    def args
        switch('config.type'){
     case 'string':
        args = description
        userInput = input(id: 'string', message: "${parameterName}", parameters: [[$class: 'StringParameterDefinition', defaultValue: '', 
        description: description, name: '', trim: true]]) 
        sh "set +x; echo "${config.name}=${userInput}" >> .env"       
        break
     case 'choice':
        args = choices
        userInput = input(id: 'choice', message: parameterName, parameters: [[$class: 'ChoiceParameterDefinition', 
        choices: "${choices}", name: parameterName]])
        sh "set +x; echo "${config.name}=${userInput}" >> .env"
        break
     case 'password':
        args = parameterName
        userInput = input(id: 'string', message: "${parameterName}", parameters: [
         [$class: 'PasswordParameterDefinition', name: "Password"]]) 
         sh "set +x; echo "${config.name}=${userInput}" >> .env"
        break
    
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
