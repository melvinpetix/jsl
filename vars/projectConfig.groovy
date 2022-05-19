def call(){
    Map projectConfig = readYaml(file: "${WORKSPACE}/runbook/" + yamlName + ".yml")
    return projectConfig
}
