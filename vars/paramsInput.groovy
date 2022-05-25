def call(yamlName){
  j = readYaml file: "${workspace}/runbook/" + yamlName + ".yml"
  if(j.parameters.type == 'choice'){
    input parameters:[config.type(name: name, description: config.description, choices: config.choices)]  
  } else {
    input parameters:[config.type(name: name, description: config.description)]
  }
}
    
