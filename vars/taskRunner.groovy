import com.webops.Parser
import com.webops.PipelineConfiguration

def call(
    String configFile='./runbook/*.yml',
    String baseRepository=null,
    String baseBranch=null) {

    checkoutRepository(baseRepository, baseBranch)
  
    def yaml = readYaml file: configFile

    ProjectConfiguration projectConfig = null
}
def checkoutRepository(String repository, String branch='master') {
     checkout([
            $class: 'GitSCM',
            branches: [[name: "*/${branch}"]],
            extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: '.']],
            userRemoteConfigs: [[url: repository]]])
    }
    else {
        checkout scm
    }
}
