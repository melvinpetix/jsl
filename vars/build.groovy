#!groovy

def call(yamlName){
   def yaml = readYaml file: 'runbook/' + yamlName + '.yml'
}
def params(){
  stage('build parameters'){
     yaml.parameters.each{params->
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
}
