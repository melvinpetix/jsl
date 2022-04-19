def call(String configFile='./dir/test.yaml'){
  Map jobCfg = readFile file: configFile
  return jobCfg
 }

