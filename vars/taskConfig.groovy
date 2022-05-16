import com.webops.Parser
import com.webops.PipelineConfiguration

def call(String runbook){
    Map taskConfig = readYaml(file: "${WORKSPACE}/runbook/" + runbook + ".yml")
    return taskConfig
}

