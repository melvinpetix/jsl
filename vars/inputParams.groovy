def call(yamlName){
 
    j = jobCfg("$workspace/runbook/" + yamlName + ".yml")
    
    if(j.parameters){
        if(j.parameters.string){            
            stringParams = input(id: 'userInput', message: j.parameters.string.name, parameters: [[$class: 'StringParameterDefinition', efaultValue: '', description: '', trim: true]])
            sh "set +x; echo \"${stringParams}\"=\"${j.parameters.string.name}\" >> .env"  
        }
        if(j.parameters.choice){
            choices = j.parameters.choice.choices.replaceAll(',',"\n")    
            choiceParams = input(id: '', message: j.parameters.choice.name, 
            parameters: [[$class: 'ChoiceParameterDefinition', choices: choices, description: '', name: j.parameters.choice.name]])  
            sh "set +x; echo \"${choiceParams}\"=\"${j.parameters.string.name}\" >> .env"        
        }
    }
     else {
        println 'no parameters settings for this job'
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
