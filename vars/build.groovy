#!groovy
import com.webops.*

def call(yaml){
   def common = new Common()
   if(yaml.parameters){
      def var
      stage('pipeline parameters'){
        timeout(time: 120, unit: 'SECONDS') {  
           yaml.parameters.each{params->
            switch(params.type){
               case 'string':
                 var = input parameters: [string(name: params.args.name)]; break    
               case 'choice':
                 var = input parameters: [choice(name: params.args.name, choices: params.args.choices)]; break
               case 'password':
                 var = input parameters: [password(name: params.args.name)]; break           
             }  
             return env."${params.args.name}" = var
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
