def call(jobName, regexp=false){
    for(item in Hudson.instance.items) {
        if (regexp && item.name ==~ jobName || item.name == jobName) {
            return item
        }
    }
}
