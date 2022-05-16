@Library('github.com/melvinpetix/jsl@dev')_
import com.webops.parser.ConfigParser;
import com.webops.*;

def call(String yamlName) {
    def yaml = readYaml file: yamlName;
    def buildNumber = Integer.parseInt(env.BUILD_ID);
    // load project's configuration
    ProjectConfiguration projectConfig = ConfigParser.parse(yaml, env);
    // adds the last step of the build.
    def closure = buildSteps(projectConfig)
}
