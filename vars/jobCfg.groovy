def call(String yamlName) {
  Map jobCfg = readYaml file: yamlName
  return jobCfg
}
