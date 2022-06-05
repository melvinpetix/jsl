@Library(value="github.com/melvinpetix/jsl@main", changelog=false)_


def call(){
 node {
    git url: 'https://oauth2:glpat-GxfR6J-STGecxjDPGz8z@gitlab.com/me1824/jsl.git', branch: 'test'
    def folders = sh(returnStdout: true, script: "ls $WORKSPACE/runbook").replaceAll(".yml", "")
    writeFile file: 'parameters', 
    text: """ 
${folders}
""" 
    def runbooks = readFile('parameters')
    properties([parameters([choice(choices: runbooks, name: 'runbook')])])
    taskRunner params.runbook
 }  
}
