@Library(value="github.com/melvinpetix/jsl@main", changelog=false)_


def call(){
def yamlName
def runbook
  node {
    git url: 'https://oauth2:glpat-GxfR6J-STGecxjDPGz8z@gitlab.com/me1824/jsl.git', branch: 'test'
    def folders = sh(returnStdout: true, 
    script: "ls $WORKSPACE/runbook").replaceAll(".yml", "")  
    writeFile file: 'parameters', text: """\n\${folders}"""        
    runbooks = readFile('parameters')
    properties([parameters([choice(choices: "${runbook}", name: 'runbook')])])
    yamlName = runbook
    deleteDir()
  }
  node(${env.jenkins_agent}){
    git url: 'https://oauth2:glpat-GxfR6J-STGecxjDPGz8z@gitlab.com/me1824/jsl.git', branch: 'test'
    taskRunner yamlName
  }
}
