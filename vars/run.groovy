import com.webops.util.ArrayMap

def call(command) {
    long ts = System.currentTimeMillis()
    String outFile = "out_$ts".toString()
    String errFile = "err_$ts".toString()
    int status = sh(script: "${command} 1> $outFile 2> $errFile", returnStatus: true)
    String out = readFile(file: outFile)
    String err = readFile(file: errFile)
    sh script: "set +x; rm $outFile", returnStdout: true
    sh script: "set +x; rm $errFile", returnStdout: true
    def res = new ArrayMap()
    res.put 'out', out
    res.put 'err', err
    res.put 'status', status
    res
}
