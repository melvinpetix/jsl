def call(String runbook){
    Map jobCfg = readYaml(file: "${WORKSPACE}/runbook/" + pipeline + ".yml")
    return jobCfg
}
