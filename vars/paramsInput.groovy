def call(body){
  def config = body
  def type = config.type
  def name = config.name
  def description = config.description
  def choices
  def args

  if(config.choices){
  input parameters:[config.type(name: name, choices: config.choices)]  
  } 
    else {
  input parameters:[config.type(name: name, description: description,  )]
  }
}
