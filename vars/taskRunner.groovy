import com.webops.Parser
import com.webops.PipelineConfiguration

def call(String runbook){
    Map projectConfig = readYaml(file: "${WORKSPACE}/runbook/" + runbook + ".yml")
    return projectConfig
}

