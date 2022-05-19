def call(){
    projectConfig = readYaml(file: "${WORKSPACE}/runbook/" + yamlName + ".yml")
    return projectConfig
}
