package com.webops.parser

import com.webops.PipelineConfig
import com.webops.PipelineBuilder

import com.webops.JenkinsDefinitions

/**
 * Configuration Parser
 */
@groovy.transform.InheritConstructors
class ConfigParser extends JenkinsDefinitions implements Serializable {

    PipelineConfig parse(yaml, env) {

        new PipelineBuilder()
            .setEnvironment(parseEnvironment(yaml.environment))
            .setProjectName(parseProjectName(yaml.config))
            .build()
    }

   // def getNodeAgent(yaml) {
    //    configToClass[(yaml.config.node_agent == null) ? DEFAULT_AGENT : yaml.config.node_agent]
 //   }

    def parseEnvironment(def environment) {
        if (!environment) {
            return ''
        }

        return environment.collect { k, v -> "${k}=${v}"}
    }

    def parseProjectName(def config) {
        if (!config || !config["project_name"]) {
            return "composeci-project"
        }

        return config["project_name"]
    }

}
