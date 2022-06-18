def call(){    
  def folders = sh(returnStdout: true, 
  script: "ls $WORKSPACE/runbook").replaceAll(".yml", "")  
  String jp = """\n${folders}\n""" 
  properties([parameters([choice(choices: "${jp}", name: 'runbook')])])
}  
