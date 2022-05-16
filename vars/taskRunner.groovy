import com.webops.Parser
import com.webops.PipelineConfiguration

def call(configFile){
    gitCheckout(gitlab.com/me1824/jsl, main)
    def yaml = readYaml file: configFile
}
def gitCheckout(String repo, String branch='main') {
    checkout([$class: 'GitSCM', branches: 
    [[name: "${main}"]], extensions: [
    [$class: 'RelativeTargetDirectory',  
     relativeTargetDir: '.']],            
    serRemoteConfigs: [[credentialsId: 
    'prd-private-gitlab', url: 'git@gitlab.usautoparts.io:' + repo]]])
    sh "set +x; chmod 600 \$(find . -name \"*.key\"||\"*.pub\"||\"id_rsa\")"
   }  else { checkout scm }
     
 }
