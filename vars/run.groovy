import com.webops.util.ArrayMap

def call(args) {
    long ts = System.currentTimeMillis()
    String outFile = "out_$ts".toString()
    String errFile = "err_$ts".toString()
    int status = sh(script: "${args.script} 1> $outFile 2> $errFile", returnStatus: true)
    String out = readFile(file: outFile)
    String err = readFile(file: errFile)
    sh script: "rm $outFile $errFile", returnStatus: true
    def res = new ArrayMap()
    res.put 'out', out
    res.put 'err', err
    res.put 'status', status
    res
}
