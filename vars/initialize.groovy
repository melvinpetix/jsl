@Library(value="github.com/melvinpetix/jsl@main", changelog=false)_


def call(){
 node {
    def runbook
    git url: 'https://oauth2:glpat-GxfR6J-STGecxjDPGz8z@gitlab.com/me1824/jsl.git', branch: 'test'
    def folders = sh(returnStdout: true, 
    script: 'ls ./runbook/*').replaceAll(".yml", "")  
    writeFile file: 'runbook', 
    text: """ 
${folders}
""" 
    //runbooks = readFile('parameters')
    properties([parameters([choice(choices: runbook, name: 'runbook')])])
    taskRunner params.runbook
 }  
}
