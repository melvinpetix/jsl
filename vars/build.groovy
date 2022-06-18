#!groovy
import com.webops.*

def call(yaml){
   def common = new Common()
  // def yaml = readYaml file: yaml
  
   if(yaml.parameters){
      stage('pipeline parameters'){
        timeout(time: 120, unit: 'SECONDS') {  
           yaml.parameters.each{params->
            switch(params.type){
               case 'string':
                 input message: '', parameters: [string(name: params.args.name)]; break    
               case 'choice':
                 input message: '', parameters: [choice(name: params.args.name, choices: params.args.choices)]; break
               case 'password':
                 input parameters: [password(name: params.args.name)]; break           
             }  
             return env."${params.args.name}" = params.args.name
           }     
         }
      }
   }
   
   if(yaml.notification){    
      def userName = "${currentBuild.getBuildCauses()[0].userId}"
      common.sendTeamsNotif(
        msg: "Started by: ${userName}", 
        job: "${yaml.project_name}", 
        url: "${yaml.notification.webhook}"
      )
    } 
}

