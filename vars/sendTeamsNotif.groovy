def call(String buildStatus, String webhookUrl) {
  def now = "${new Date().format('yyyyMMdd')}"  
  
  def emoji

  if(currentBuild.result == ('FAILURE')){
    emoji = "❌"
  } else {
    emoji = "🚀"
  }

   sh "curl -X POST -H \'Content-Type: application/json\'\
  -d \'{\"title\": \"${emoji} Unified-Notifier :  ${params.SNAPSHOT}\", \"text\": \"${buildStatus}\"}\' ${webhookUrl}" 
}
