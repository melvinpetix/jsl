package com.webops;

import com.webops.PipelineBuilder;

class Parser {

    static PipelineBuilder parse(def yaml, def env) {
      
        PipelineBuilder pipelineBuilder = new PipelineBuilder();

         // parse the environment variables and jenkins environment variables to be passed
        pipelineBuilder.environment = parseEnvironment(yaml.environment, yaml.jenkinsEnvironment, env);

        // parse the execution steps
        pipelineBuilder.steps = parseSteps(yaml.steps);

        // load the project name
        pipelineBuilder.projectName = parseProjectName(yaml.project_name);

        pipelineBuilder.env = env;

        return pipelineBuilder;
    }

    def parseEnvironment(def environment, def jenkinsEnvironment, def env) {
        def config = [];

        if (environment) {
            config += environment.collect { k, v -> "${k}=${v}"};
        }

        if (jenkinsEnvironment) {
            config += jenkinsEnvironment.collect { k -> "${k}=${env.getProperty(k)}"};
        }

        return config;
    }
  
     def parseProjectName(def config) {
        if (!config || !config["project_name"]) {
            return "webops-generic-template";
        }

        return config["project_name"];
    }
}
