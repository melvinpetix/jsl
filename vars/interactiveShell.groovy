def call() {
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
