def call(jobName){
    def job = getJobByName(jobName)
    def prop = job.getProperty(ParametersDefinitionProperty.class)
    def params = new java.util.HashMap<String,String>()
    if(prop != null) {
        for(param in prop.getParameterDefinitions()) {
            params.put(param.name, param.defaultValue)
        }
    }
    return params
}
