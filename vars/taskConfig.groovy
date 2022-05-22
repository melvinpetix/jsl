def call(String yamlName){
    Map taskConfig = readYaml(file: "${WORKSPACE}/runbook/" + yamlName + ".yml")
    return taskConfig
}

