@Library('webops-ci')
import com.webops.*;
import com.webops.steps.Step;

def call(ProjectConfiguration projectConfig) {
  return { variables ->
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

