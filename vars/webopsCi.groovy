@Library('github.com/melvinpetix/jsl@dev')_
import com.webops.parser.ConfigParser;
import com.webops.*;

def call(String yamlName) {
    def yaml = readYaml file: yamlName;
    // load project's configuration
    ProjectConfiguration projectConfig = ConfigParser.parse(yaml, env);
    // adds the last step of the build.
    List<Step> stepsA = projectConfig.steps.steps
    
    stepsA.each { step ->
     stage(step.name) {
        step.commands.each { command ->
           sh command
        }
      }
    }
    
}
