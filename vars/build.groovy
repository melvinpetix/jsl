#!groovy
import com.webops.*

def call(yaml){
   def common = new Common()
   if(yaml.parameters){
      def userInput
      stage('pipeline parameters'){
        timeout(time: 120, unit: 'SECONDS') {  
           yaml.parameters.each{params->
            switch(params.type){
               case 'string':
                 userInput = input parameters: [string(name: params.args.name)]; break    
               case 'choice':
                 userInput = input parameters: [choice(name: params.args.name, choices: params.args.choices)]; break
               case 'password':
                 userInput = input parameters: [password(name: params.args.name)]; break           
             }  
             return env."${params.args.name}" = userInput
           }     
         }
      }
   }
   
   println """
   
   ${choicesample}
   
   ${username}
   
   ${password}
   
   ${snapshot_date}
   
   """
   
  if(yaml.notification){    
    def userName = "${currentBuild.getBuildCauses()[0].userId}"
    common.sendTeamsNotif(
      msg: "Started by: ${userName}", 
      job: "${yaml.project_name}", 
      url: "${yaml.notification.webhook}"
    )
  } 
}
