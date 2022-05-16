package com.webops.parser;
import com.webops.ProjectConfiguration;
import com.webops.services.*;
import com.webops.steps.*;

class ConfigParser {
    static ProjectConfiguration parse(def yaml, def env) {
        ProjectConfiguration projectConfiguration = new ProjectConfiguration();
        projectConfiguration.buildNumber = env.BUILD_ID;
        // parse the environment variables and jenkins environment variables to be passed
        projectConfiguration.environment = parseEnvironment(yaml.environment, yaml.jenkinsEnvironment, env);
        // add Build Number environment variables
        projectConfiguration.environment.add("BUILD_ID=${env.BUILD_ID}");
        // add SCM environment variables
        projectConfiguration.environment.add("BRANCH_NAME=${env.BRANCH_NAME.replace('origin/','')}");
        // parse the execution steps
        projectConfiguration.steps = parseSteps(yaml.steps);
        // parse the necessary services
        projectConfiguration.projectName = parseProjectName(yaml.config);
        return projectConfiguration;
    }
    static def parseEnvironment(def environment, def jenkinsEnvironment, def env) {
        def config = [];
        if (environment) {
            config += environment.collect { k, v -> "${k}=${v}"};
        }
        if (jenkinsEnvironment) {
            config += jenkinsEnvironment.collect { k -> "${k}=${env.getProperty(k)}"};
        }
        return config;
    }
    static def parseSteps(def yamlSteps) {
        List<Step> step = yamlSteps.collect { k, v ->
            Step step = new Step(name: k)
            v.each {
                step.commands.add(it);
            }
            return step
        }
    }
    
    static def parseProjectName(def config) {
        if (!config || !config["project_name"]) {
            return "webopsci-project";
        }

        return config["project_name"];
    }
}
