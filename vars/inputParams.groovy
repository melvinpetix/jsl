def call(yamlName){
    def c  = []
    j = jobCfg("$workspace/runbook/" + yamlName + ".yml")
    if(j.parameters){
        if(j.parameters.string){
           stringParams = input(id: 'userInput', message: "${j.parameters.string.name}", parameters: [[$class: 'StringParameterDefinition', 
           defaultValue: '', description: "${j.parameters.string.name}", name: "${j.parameters.string.name}", trim: true]])
           sh "set +x; echo \"${stringParams}\"=\"${j.parameters.string.name}\" >> .env"  
        }
        if(j.parameters.choice){
            j.parameters.choice.choices.split().each{-> c << it }                                                   
          choiceParams = input(id: '', message: j.parameters.choice.name, 
          parameters: [[$class: 'ChoiceParameterDefinition', 
          choices: "${choices}", name: j.parameters.choice.name]])  
          sh "set +x; echo \"${choiceParams}\"=\"${j.parameters.string.name}\" >> .env"

        }
    } else {
        println 'no parameters settings for this job'
    }
}

