@Library('github.com/melvinpetix/jsl@dev')_
import com.webops.*;
import com.webops.parser.ConfigParser;
import com.webops.steps.Step;


def call(String yamlName) {
    def yaml = readYaml file: "${workspace}/runbook/" + yamlName + ".yml";
    // load project's configuration
    ProjectConfiguration projectConfig = ConfigParser.parse(yaml, env);
    // adds the last step of the build.
    List<Step> stepsA = projectConfig.steps.step
    stepsA.each { step ->
     stage(step.name) {
        step.commands.each { command ->
           sh command
        }
      }
    }
    
}
