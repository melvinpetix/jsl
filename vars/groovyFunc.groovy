def sendTeamsNotif(String buildStatus, String webhookUrl) {
  def now = "${new Date().format('yyyyMMdd')}"  
  
  def message

  if (buildStatus == 'STARTED' || buildStatus == 'in-progress') {
    emoji = "⚡"
  } else if (buildStatus == 'FAILED') {
    buildStatus = buildStatus + " " + err
    emoji = "❌"
  } else if (buildStatus == 'SUCCESS'){
    emoji = "👍"
  }

  message = "🚀 Unified-Notifier\ntask: ${params.SNAPSHOT}\nstatus: ${buildStatus} ${emoji}"

  sh "curl -X POST -H \'Content-Type: application/json\'\
  -d \'{\"text\": \"${message}\"}\' ${webhookUrl}" 
}
return this;


def gitCheckout(String giturl, String repo, String branchName) {
  git branch: branchName, 
    credentialsId: 'prd-private-gitlab', 
    url: 'git@' + giturl + ':' + repo   
    //sh 'chmod 600 snp.key; chmod 775 *.sh; chmod +x *.sh; chmod 600 +'     
}

def execute(Map config){
  def command = [:]
  
  if(config.stage){
    stage "$config.stage"
  }

  if(!config.server){
    echo 'local[SHELL]'
    res = sh script: config.cmd, 
    returnStdout: false
    return res 
  } 
  def slist = config.server.toString().split(',')
  if(slist.size() > 1){ 
    slist.each{ s-> command[s] = { 
      sh script: "${args} ${s} ${config.cmd}" } 
    } 
    parallel command
  } else { sh script: "${args} ${config.server} ${config.cmd}" }
}
return this;
  

def interactiveShell() {
  timeout(time: 10, unit: 'MINUTES') {
    println "[Starting shell session]"
    String cmd = ''
    while (true) {
      cmd = input(id: 'cmd', message: 'Command:', parameters: [
      [$class: 'TextParameterDefinition',
      description: 'shell session of the fly',
      defaultValue: cmd, name: 'cmd']])
      
      if (cmd == 'exit') { break }
      
      def ret = sh(returnStatus: true,
      script: cmd)
      echo "[Shell] Return Code: $ret"
    }   
    echo "[Shell] Bye!"  
  }
}

def taskRunner(stageName, Closure stageCmd){
  try{ stage(stageName){ stageCmd() }  
  } catch(err){ error stageName + "!! " + "Failed with the ff. error:\n" + err }
} 


