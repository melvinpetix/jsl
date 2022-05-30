@Library('github.com/melvinpetix/jsl@dev')_
import com.webops.*;
import com.webops.parser.ConfigParser;
import com.webops.steps.Step;


def call(String yamlName) {
    def yaml = readYaml file: "${workspace}/runbook/" + yamlName + ".yml";
    // load project's configuration
    ProjectConfiguration projectConfig = ConfigParser.parse(yaml, env);
    // adds the last step of the build.
    def closure = buildSteps(projectConfig);
    closure([:])
}
