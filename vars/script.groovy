import org.jenkinsci.plugins.workflow.cps.CpsThread
import org.jenkinsci.plugins.workflow.actions.LabelAction

def call(def Map vars) {
  def script = vars["script"]
  def stepName = vars["stepName"]
  def returnStatus = vars.get("returnStatus")
  def output
  try{
      if (returnStatus) {
        output = sh(script: script,returnStatus: true)
      } else {
        output = sh(script: script,returnStdout: true)
      }
  } finally {
      CpsThread.current().head.get().addAction(new LabelAction("Shell script ${stepName} "))
  }
  return output
}
