def call(String name){
  Map jobCfg = readYaml file: "${WORKSPACE}/${name}.yaml"
  return jobCfg
 }

