package com.webops.parser;
import com.webops.ProjectConfiguration;
import com.webops.services.*;
import com.webops.steps.*;

class ConfigParser {
    static ProjectConfiguration parse(def yaml, def env) {
        ProjectConfiguration projectConfiguration = new ProjectConfiguration();
        projectConfiguration.environment = parseEnvironment(yaml.environment, yaml.jenkinsEnvironment, env);
        projectConfiguration.steps = parseSteps(yaml.steps);
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
        List<Step> steps = yamlSteps.collect { k, v ->
            Step step = new Step(name: k)
            v.each {
                step.commands.add(it);
            }
            return step
        }
        return new Steps(steps: steps);
    }
    
    static def parseProjectName(def config) {
        if (!config || !config["project_name"]) {
            return "webopsci-project";
        }

        return config["project_name"];
    }
}
