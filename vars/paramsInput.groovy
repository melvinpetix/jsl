def call(yamlName){
  def j = readYaml file: yamlName
  def config = j.parameters
  if(config.password){
    input parameters:[password(name: P4SSWORD]
  } 
  if(config.string){
    input parameters:[string(name: config.name, 
    description: config.description)]
  }
  if(config.choice){
    def choices = config.choice.choices.replaceAll(',',"\n")  
    input parameters:[choice(name: config.name, 
    description: config.description, 
    choices: config.choices)]
    
  }
}
    
