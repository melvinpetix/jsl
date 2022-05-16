def call(String fileName){
    Map jobCfg = readYaml(file: "${WORKSPACE}/runbook/" + fileName + ".yml")
    return jobCfg
}
