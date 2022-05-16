@Library('webops-ci')
import com.webops.parser.ConfigParser;
import com.webops.*;

def call(String yamlName) {
    def yaml = readYaml file: yamlName;
    def buildNumber = Integer.parseInt(env.BUILD_ID);
    // load project's configuration
    ProjectConfiguration projectConfig = ConfigParser.parse(yaml, env);
    // adds the last step of the build.
    def closure = buildSteps(projectConfig)

    projectConfig.services.each {

        closure = "${it.service.getVar()}"(projectConfig, it.version, closure);
    }


    try {
        closure([:]);
    } finally{
        deleteDockerImages(projectConfig);
    }
}
