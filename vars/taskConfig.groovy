def call(String runbook){
    Map taskConfig = readYaml(file: "${WORKSPACE}/runbook/" + runbook + ".yml")
    return taskConfig
}

