def call(String configFile='./dir/config.yml'){
  def yaml = readYaml file: configFile
  return yaml
 }

