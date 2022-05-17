def call(yaml){
    builder = jobCfg("${yaml}")
    if(builder.parameters.string){
        stringParams = input(id: 'string', message: "${builder.parameters.string.name}", parameters: [
        [$class: 'StringParameterDefinition', defaultValue: '', description: "${builder.parameters.string.name}", 
        name: '', trim: true]]) 
      }
      if(builder.parameters.choice){
        def choices = []
        choices = builder.parameters.choice.choices.toString()
        choices = choices.replaceAll(',','\n')
        choiceParams = input(id: '', message: "${builder.parameters.choice.name}", parameters: [
        [$class: 'ChoiceParameterDefinition', choices: "${choices}", name: "${builder.parameters.choice.name}"]])  
      }
      if(builder.parameters.password){
         PASSWORD = input(id: 'password', message: '', parameters: [
         [$class: 'PasswordParameterDefinition', name: "Password"]])
       }
}
return this;
