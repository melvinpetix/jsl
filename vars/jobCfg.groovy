def call(String configFile='./dir/config.yml'){
  Map jobCfg = readFile file: configFile
  return jobCfg
 }

