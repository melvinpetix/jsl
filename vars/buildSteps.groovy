@Library('github.com/melvinpetix/jsl@dev')_
import com.webops.*;
import com.webops.steps.Step;

def call(ProjectConfiguration projectConfig) {
  return { ->
    List<Step> stepsA = projectConfig.steps.steps
     stepsA.each { step ->
         stage(step.name) {
            step.commands.each { command ->
               sh command
            }
          }
        }
      }
    }

