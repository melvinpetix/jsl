def call(yamlName){
    j = jobCfg("$workspace/runbook/" + yamlName + ".yml")
    if(j.parameters){
        if(j.parameters.string){
          params = input(id: 'userInput', message: "${j.parameters.string.name}", 
          parameters: [[$class: 'StringParameterDefinition', 
          defaultValue: '', description: "${j.parameters.string.name}", 
          name: "${j.parameters.string.name}", trim: true]])
          println params
          println j.parameters.string.name
            println "${j.parameters.string.name}
          sh "set +x; echo \"${j.parameters.string.name}=${params}\" >> config.sh"
        }

        if(j.parameters.choice){
          def choices = j.parameters.choice.choices.replaceAll(',',"\n")
          params = input(id: '', message: "${j.parameters.choice.name}", 
          parameters: [[$class: 'ChoiceParameterDefinition', 
          choices: "${choices}", description: '', 
          name: "${j.parameters.choice.name}"]])  
          sh "set +x; echo \"${j.parameters.choice.name}=${params}\" >> config.sh"
        }
    } else {
        println 'no parameters settings for this job'
    }
}

@NonCPS
def call(Map config=[:]) {

    def template = libraryResource("com/company/distribution/pod-templates/${config.name}.yaml")

    if (config.binding) {
        def engine = new groovy.text.GStringTemplateEngine()
        template = engine.createTemplate(template).make(config.binding).toString()
    }

    return template
}
