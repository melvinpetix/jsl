def call(String buildStatus, String jobName, String webhookUrl) {
  def cause = currentBuild.getBuildCauses('hudson.model.Cause$UserIdCause')
  echo "userName: ${cause.userName}"
  def now = "${new Date().format('yyyyMMdd')}"  
 
  if(currentBuild.result == ('FAILURE')){
    emoji = "❌" } else { emoji = "🚀" }

   sh """
      curl --request POST \
      --url ${webhookUrl} \
      --header 'content-type: application/json' \
      --data '{
        "@type": "MessageCard",
        "@context": "http://schema.org/extensions",
        "themeColor": "0076D7",
        "summary": "Unified Notifier",
        "sections": [{"activityTitle": "Unified Notifier",
        "facts": [{"name": "task",
        "value": "${emoji} ${jobName}"},
        {"name": "status",
        "value": "${buildStatus}",
        {"name": "started_by",
        "value": "${cause.userName}"},
        {"name": "date",
        "value": "${now}"}],
        "markdown": true}]}'
      """
}

