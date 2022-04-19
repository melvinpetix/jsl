def call(String configFile){
  def yaml = readYaml file: "${WORKSPACE}" + "/" + configfile + '.yaml'
  return yaml
 }
