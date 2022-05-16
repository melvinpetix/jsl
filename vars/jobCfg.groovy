def call(String yamlName){
    Map projectconfig = readYaml(file: "${WORKSPACE}/runbook/" + yamlName + ".yml")
    return projectconfig
}
