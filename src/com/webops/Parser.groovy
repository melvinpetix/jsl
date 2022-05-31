package com.webops;

import com.wolox.PipelineBuilder;

class ConfigParser {

    static ProjectConfiguration parse(def yaml, def env) {
      
        ProjectConfiguration projectConfiguration = new ProjectConfiguration();

         // parse the environment variables and jenkins environment variables to be passed
        projectConfiguration.environment = parseEnvironment(yaml.environment, yaml.jenkinsEnvironment, env);

        // parse the execution steps
        projectConfiguration.steps = parseSteps(yaml.steps);

        // load the project name
        projectConfiguration.projectName = parseProjectName(yaml.project_name);

        projectConfiguration.env = env;

        return projectConfiguration;
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
            return "woloxci-project";
        }

        return config["project_name"];
    }
}
