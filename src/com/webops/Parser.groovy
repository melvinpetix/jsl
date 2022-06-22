package com.webops;

import com.webops.ProjectConfiguration;

class Parser {

    static ProjectConfiguration parse(def yaml) {
      
        ProjectConfiguration pipelineConfig = new ProjectConfiguration();

        pipelineConfig.steps = parseSteps(yaml.steps);

        pipelineConfig.projectName = parseProjectName(yaml.project_name);
        
        pipelineConfig.parameter = parseParameters(yaml.parameters);
        
        pipelineConfig.notification = parseNotification(yaml.notification)

        return pipelineConfig;
    }


     def parseProjectName(def config) {
        if (!config || !config["project_name"]) {
            return "webops-generic-template";
        }

        return config["project_name"];
    }
}
