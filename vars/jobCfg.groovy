def call(String runbook){
    Map projectconfig = readYaml(file: "${WORKSPACE}/runbook/" + pipeline + ".yml")
    return projectconfig
}
