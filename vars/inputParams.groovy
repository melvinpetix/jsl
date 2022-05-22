def call(yamlName){
    j = jobCfg("$workspace/runbook/" + yamlName + ".yml")
    if(j.parameters){
        if(j.parameters.string){
          params = input(id: 'userInput', message: "${j.parameters.string.name}", 
          parameters: [[$class: 'StringParameterDefinition', 
          defaultValue: '', description: "${j.parameters.string.name}", 
          name: "${j.parameters.string.name}", trim: true]])
          writeFile file: "${j.parameters.string.name}.env", 
          text: "${j.parameters.string.name}=${params}"
        }

        if(j.parameters.choice){
          def choices = j.parameters.choice.choices.replaceAll(',',"\n")
          params = input(id: '', message: "${j.parameters.choice.name}", 
          parameters: [[$class: 'ChoiceParameterDefinition', 
          choices: "${choices}", description: '', 
          name: "${j.parameters.choice.name}"]])  
          writeFile file: "${j.parameters.choice.name}.env",
          text: "${j.parameters.choice.name}=${params}"
        }
    } else {
        println 'no parameters settings for this job'
    }
}

@NonCPS
def populateEnv(){ binding.variables.each{k,v -> env."$k" = "$v"} }
