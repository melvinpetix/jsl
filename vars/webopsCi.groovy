@Library('https://github.com/melvinpetix/jsl@main')_

def call(){
    
    stage('define task'){
        deleteDir()

        git branch: 'main', url: 'https://oauth:glpat-GxfR6J-STGecxjDPGz8z@gitlab.com/me1824/jsl.git'
        sh 'chmod 600 snp.key; chmod 600 +'   
        def folders = sh(returnStdout: true, script: "ls $workspace/task").replaceAll('.yml',"")
        writeFile file:'task.txt', text: "${folders}"
        tasklist = readFile("$workspace/task.txt") 
        userInput = input(id: 'tasklist', message: 'task', parameters: [
        [$class: 'ChoiceParameterDefinition', choices: "${tasklist}", description: '', name: 'tasklist']]) 
        def j = jobCfg("$workspace/task/${userInput}.yml")
        def shArgs = "ssh -F + "
        list stepsA = j.steps
        stepsA.collect{k,v->           
            stage("${k}"){
                v.each{command->
                    j.server.each{x->   
                    sh"""#!/bin/bash
                    set +x  
                    export TERM=xterm-256color 
                    ssh -F + -tt ${x} '${command}'
                    """
                    }
                }
            }
        }   
    }
}
