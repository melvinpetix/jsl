def call(){
    Map projectconfig = readYaml(file: "${WORKSPACE}/runbook/" + yamlName + ".yml")
    return projectconfig
}
