package com.webops;

import com.webops.PipelineBuilder;

class Parser {

    static PipelineConfig parse(def yaml) {
      
        PipelineConfig pipelineConfig = new PipelineConfig();

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
