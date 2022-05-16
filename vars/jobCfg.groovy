def call(String filename){
    Map taskConfig = readYaml(file: "${WORKSPACE}/runbook/" + filename + ".yml")
    return taskConfig
}
