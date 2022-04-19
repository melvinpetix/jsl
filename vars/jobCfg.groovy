def call(String name){
  Map jobCfg = readFile file: "${WORKSPACE}/${name}.yaml"
  return jobCfg
 }

