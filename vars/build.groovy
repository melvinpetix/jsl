#!groovy
import com.webops.*

def call(body){
   def common = new Common()
   def config = body
   def yaml = readYaml file: 'runbook/' + config.yaml + '.yml'
  
   if(yaml.parameters){
      def paramsConfig = parseParams yaml.parameters
      stage('pipeline parameters'){
        timeout(time: 120, unit: 'SECONDS') {  
          input parameters: paramsConfig
             
        }
      }
   }
             
             /*
             
             
            switch(params.type){
               case 'string':
               userInput = input message: '', parameters: [string(name: params.args.name)]
               break    
               case 'choice':
               userInput = input message: '', parameters: [choice(name: params.args.name, choices: params.args.choices)]; 
               break
               case 'password':
               userInput = input parameters: [password(name: params.args.name)]; 
               break           
             }  
               return env."${params.args.name}" = userInput
           }     
         }
      }
   }
   
   */
   if(yaml.notification){    
      def userName = "${currentBuild.getBuildCauses()[0].userId}"
      common.sendTeamsNotif(
        msg: "Started by: ${userName}", 
        job: "${yaml.project_name}", 
        url: "${yaml.notification.webhook}"
      )
    } 
}

@NonCPS
def parseParams(parameters) {
    parameters.collect { params ->
      this.invokeMethod params.type, params.args.collectEntries { name, value ->
        [
          name, 
          value instanceof String ? interp(value) : value
        ]
      }
    }
}

@NonCPS
def interp(value) {
  new groovy.text.GStringTemplateEngine()
    .createTemplate(value)
    .make([env:env])
    .toString()
}

