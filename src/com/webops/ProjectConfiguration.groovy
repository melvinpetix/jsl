package com.webops;

import com.webops.docker.DockerConfiguration;
import com.webops.steps.Steps;

class ProjectConfiguration {
    def environment;
    def services;
    Steps steps;
    def dockerfile;
    def projectName;
    def buildNumber;
    DockerConfiguration dockerConfiguration;
    def env;
    def timeout;
}
