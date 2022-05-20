def call(yamlName){
    j = jobCfg("$workspace/runbook/" + yamlName + ".yml")
    if(j.parameters){
        if(j.parameters.string){
           stringParams = input(id: 'userInput', message: "${j.parameters.string.name}", parameters: [[$class: 'StringParameterDefinition', 
           defaultValue: '', description: "${j.parameters.string.name}", name: "${j.parameters.string.name}", trim: true]])
           sh "set +x; echo \"${stringParams}\"=\"${j.parameters.string.name}\" >> .env"  
        }
        if(j.parameters.choice){
            def choices = j.parameters.choice.choices.replaceAll(',',"\n")
            choiceParams = input(id: '', message: "${j.parameters.choice.name}", 
            parameters: [[$class: 'ChoiceParameterDefinition', choices: "${choices}", 
                          description: '', name: "${j.parameters.choice.name}"]])  
            sh "set +x; echo \"${choiceParams}\"=\"${j.parameters.string.name}\" >> .env"
        }
    } 
    else {
        println 'no parameters settings for this job'
    }
}
