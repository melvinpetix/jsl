def call(yamlName){
  stage 'parameter definition'
  j = readYaml file: "runbook/" + yamlName + ".yml"
  if(j.parameters.type == 'choice'){
    input parameters:[config.type(name: name, description: config.description, choices: config.choices)]  
  } else {
    input parameters:[config.type(name: name, description: config.description)]
  }
}
    
