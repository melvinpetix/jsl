package com.webops;

import com.webops.PipelineBuilder;

class Parser {

    static PipelineBuilder parse(def yaml) {
      
        PipelineBuilder pipelineBuilder = new PipelineBuilder();

        pipelineBuilder.steps = parseSteps(yaml.steps);

        pipelineBuilder.projectName = parseProjectName(yaml.project_name);
        
        pipelineBuilder.parameter = parseParameters(yaml.parameters);
        
        pipelineBuilder.notification = parseNotification(yaml.notification)

        return pipelineBuilder;
    }


     def parseProjectName(def config) {
        if (!config || !config["project_name"]) {
            return "webops-generic-template";
        }

        return config["project_name"];
    }
}
