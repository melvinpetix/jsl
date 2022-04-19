def call(String configFile='./dir/config.yml'){
  def yaml = readFile file: configFile
  return yaml
 }

