def call(yamlName){
    j = jobCfg("$workspace/runbook/" + yamlName + ".yml")
    if(j.parameters){
        if(j.parameters.string){
            params = input(id: 'userInput', message: '', 
            parameters: [[$class: 'StringParameterDefinition', 
            defaultValue: '', description: j.parameters.string.name, 
            name: j.parameters.string.name, trim: true]])
            sh "set +x; echo ${j.parameters.string.name}=${params}"
            //sh 'set +x; echo \"${j.parameters.string.name}=${params}\" >> config.sh'
            echo "${snapshot_date}"
        }

        if(j.parameters.choice){
          def choices = j.parameters.choice.choices.replaceAll(',',"\n")
          params = input(id: '', message: "${j.parameters.choice.name}", 
          parameters: [[$class: 'ChoiceParameterDefinition', 
          choices: "${choices}", description: '', 
          name: j.parameters.choice.name]])  
          //sh 'set +x; echo \'${j.parameters.choice.name}=${params}\' >> config.sh'
        }
    } else {
        println 'no parameters settings for this job'
    }
}


