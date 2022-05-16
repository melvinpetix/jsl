import com.webops.Parser
import com.webops.PipelineConfiguration

def call(config){
    checkoutRepository(baseRepository, baseBranch)
    def yaml = readYaml file: configFile
    ProjectConfiguration projectConfig = null
}
def gitCheckout(String repo, String branch='main') {
    checkout([$class: 'GitSCM', branches: 
    [[name: "${main}"]], extensions: [[$class: 'RelativeTargetDirectory', 
     relativeTargetDir: '.']], serRemoteConfigs: [[credentialsId: 
    credentialsId, url: 'git@gitlab.usautoparts.io:' + repo]]])
         sh "set +x; chmod 600 \$(find . -name \"*.key\"||\"*.pub\"||\"id_rsa\")"
        }
        else { checkout scm }
    }
