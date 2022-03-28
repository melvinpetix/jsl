#!groovy

def call(String snapshot){
  node("${env.jenkins_agent}"){  
    def now = "${new Date().format('yyyyMMdd')}"  
    if(!params.SNAPSHOT || params.SNAPSHOT == 'none' ){
      currentBuild.description = "update"
      interactiveShell() 
      return
    } 
    
    println "UNIFIED SNAPSHOT[ ${now} ] : ${params.SNAPSHOT}"

    sendTeamsNotif( 
      job: "🚀${params.SNAPSHOT}",
      msg: "started", 
      webhookUrl: "${webops}"
    )
  
    deleteDir()

    stage("${params.SNAPSHOT}"){
      try {
        gitCheckout()

        switch(params.SNAPSHOT) {
          case 'MASTER TEMPLATE':
          mastertemplateBuild()
          break
          case 'Flexclone Destroy':
          flexClone(task: "destroy")
          break
          case 'Flexclone Create':
          flexClone(task: "create")
          break
          case 'Post Snapshot Report':
          postSnapshotReport()
          break
          default:
          currentBuild.result = "SUCCESS"
        }
      }catch(err){
        sendTeamsNotif(
          job: "❌${params.SNAPSHOT}",
          msg: "FAILED❗ [ERROR]${err}",
          webhookUrl: "${webops}" 
        )
        error "${params.SNAPSHOT} FAILED [${err}]"
        currentBuild.result = 'FAILURE'
        deleteDir()
      } 
    } 
  }
}

def mastertemplateBuild(body){
  def config = body
  def d = params.snapshot_date
  
  node{ 
    if(env.hotcopy == 'true'){
      stage 'hotcopy' 
      env.COPY_TO.tokenize(",").each{ s-> sh(returnStdout: false,
      script: "ssh -F + csendrepo01 'bash -s' -- < hotcopy.sh -S \"${d}\" -H \"${s}\"") } 
    }
  
    if(env.extract == 'true'){  
      stage 'extract'
      env.EXTRACT_TO.tokenize(",").each{ e-> sh "ssh -F + ${e} 'bash -s' < umt.sh" }
    }
  
    if(env.generate == 'true'){  
    stage 'generate'
    env.GENERATE_TO.tokenize(",").each{ j->      
      run "${j}", "cd ${FEEDS_DIR}; script /dev/null | sh feeds.sh"                 
      shout 'screen', "${j}", "mastertemplate_\$(date +%Y-%m-%d)"  
    }
  }
  
  if(env.elasticfeedgen == 'true'){
    stage 'elasticfeedsgeneration'
    run "${snap_host}", 'script /dev/null | sh .elastic.sh'
    shout 'logs', 'cscatbe09', 'tail /tmp/elastic_feeds.log'
    shout 'screen', 'cscatbe09', 'screen -ls | grep elastic*'
    currentBuild.description = "${currentBuild.description} | ELASTIC_FEEDS"   
    sendTeamsNotif(
      job: "🚀MasterTemplate & ElasticFeedsGeneration",
      msg: "in-progress ⚡",
      webhookUrl: "${webops}" 
    )

  }
}

def flexClone(body){
  def config = body
  def task = "${config.task}" 

  run "${snap_host}", "runFlex ${task}"

  taskStatus = sh(returnStdout: true,
    script: "ssh -F + -t ${snap_host} 'taskStatus ${task}'"
  ).trim()

  sendTeamsNotif(msg: "${taskStatus}",
    job: "🚀${params.SNAPSHOT}",
    webhookUrl: "${webops}"
  )
}

def postSnapshotReport(){
  env.WORKSPACE = pwd()
  def USERNAME = input message: 'Please enter the username',
  parameters: [string(defaultValue: '', description: '' ,name: 'Username')]
  def PASSWORD = input message: 'Please enter the password',
  parameters: [password(defaultValue: '', description: '', name: 'Password')]
  def i = readFile("${env.WORKSPACE}/postsnaper.sh").replaceAll("US3R", "$USERNAME")
  
  filenew = i.replaceAll('P4SS', "$PASSWORD")
  writeFile file:'postsnapnew.sh', text: filenew
  sh "/bin/bash +x postsnapnew.sh"
  deleteDir()
}

def run(String server, String cmd) {
  def out = sh script: "ssh -F + ${server} '${cmd}'",
  returnStdout: false
}

def shout(String name, String server, String cmd) {
  try{
    if(!server || server == ''){
      def out = sh(script: "${cmd}", returnStdout: true).trim()
      println "[${name}] --> ${out}"
    } else {
      def out = sh(returnStdout: true,
        script: "ssh -F + ${server} '${cmd}'"
      ).trim()
      println "[${name}] --> ${out}"
    }
  } catch(err){
    println "[SHELL] ${err}"
  }
}

def startInteractiveShell

def sendTeamsNotif(body) {
  def now = "${new Date().format('yyyyMMdd')}"  
  def config = body

  msg = "${config.msg}"
  webhookUrl = "${config.webhookUrl}"
  job = "${config.job}"

  def message = "${job}: "+''+" ${msg}"

  sh "curl -X POST -H \'Content-Type: application/json\'\
  -d \'{\"text\": \"${message}\"}\' ${webhookUrl}" 
}

def gitCheckout(){
  git branch: 'script', credentialsId: 'prd-private-gitlab', 
    url: 'git@gitlab.usautoparts.io:mbiscarra/legacy-task.git'   
    sh 'chmod 600 snp.key; chmod 775 *.sh; chmod +x *.sh; chmod 600 +'     
}

def interactiveShell() {
  try{
    timeout(time: 10, unit: 'MINUTES') {
      println "[Starting shell session]"
      String cmd = ''
      while (true) {
        cmd = input(id: 'cmd', message: 'Command:', parameters: [
        [$class: 'TextParameterDefinition',
        description: 'shell session of the fly',
        defaultValue: cmd, name: 'cmd']])
          if (cmd == 'exit') {
              break
          }
          def ret = sh(script: cmd, returnStatus: true)
          echo "[Shell] Return Code: $ret"
      }   
      echo "[Shell] Bye!"
     }
    } catch(err){
      currentBuild.result == 'SUCCESS'
  }

}




