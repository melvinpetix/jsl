#!groovy

def call(body){
   def config = body
   def yamlName = config.yaml
   def yaml = readYaml file: 'runbook/' + yamlName + '.yml'
   //def params = yaml.parameters
   //def notification = yaml.notification
   stage('define pipeline config'){
      def userInput
      if(yaml.parameters){     
        timeout(time: 120, unit: 'SECONDS') {  
          yaml.parameters.each{params->   
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
}
