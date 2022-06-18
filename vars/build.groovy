#!groovy

def call(yamlName){
   Map yaml = readYaml file: 'runbook/' + yamlName + '.yml'
   return yaml
}

def params(){
  params = yaml.parameters
  stage('build parameters'){
    timeout(time: 120, unit: 'SECONDS') {  
      switch(params.type){
        case 'string':
        userInput = input message: '', parameters: [string(name: params.args.name)]
        break    
        case 'choice':
        userInput = input message: '', parameters: [choice(name: params.args.name, choices: params.args.choices)]; 
        break
        case 'password':
        userInput = input parameters: [password(name: password)]; 
        break           
     } 
      return env."${params.args.name}" = userInput   
    }
  }
}

def notification(String buildStatus, String jobName, webhookUrl) {     
  if(currentBuild.result == ('FAILURE')){
    emoji = "???"
    COLOR = "ff0000"
  } else {
    emoji = "????"
    COLOR = "00FF00"
  }  
   sh "curl -X POST -H \'Content-Type: application/json\'\
  -d \'{\"title\": \"${emoji}Unified-Notifier: ${params.SNAPSHOT}\",\
    \"themeColor\": \"${COLOR}\", \"text\": \"${buildStatus}\" }' ${webhookUrl}"
}
