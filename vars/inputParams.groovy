def call(yamlName){
    j = jobCfg("$workspace/runbook/" + yamlName + ".yml")
    if(j.parameters){
        if(j.parameters.string){
          params = input(id: 'userInput', message: "${j.parameters.string.name}", 
          parameters: [[$class: 'StringParameterDefinition', 
          defaultValue: '', description: "${j.parameters.string.name}", 
          name: "${j.parameters.string.name}", trim: true]])
          sh "set +x; echo \"${j.parameters.string.name}=${params}\" >> config.env"
        }

        if(j.parameters.choice){
          def choices = j.parameters.choice.choices.replaceAll(',',"\n")
          params = input(id: '', message: "${j.parameters.choice.name}", 
          parameters: [[$class: 'ChoiceParameterDefinition', 
          choices: "${choices}", description: '', 
          name: "${j.parameters.choice.name}"]])  
          sh "set +x; echo \"${j.parameters.choice.name}=${params}\" >> config.env"
        }
    } else {
        println 'no parameters settings for this job'
    }
}

@NonCPS
def populateEnv(){ binding.variables.each{k,v -> env."$k" = "$v"} }
