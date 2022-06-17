@NonCPS
def call(parameters) {
    parameters.collect { params ->
      this.invokeMethod params.type, params.args.collectEntries { name, value ->
        [
          name, 
          value instanceof String ? readMyProps.interp(value) : value
        ]
      }
    }
}

@NonCPS
def interp(value) {
  new groovy.text.GStringTemplateEngine()
    .createTemplate(value)
    .make([env:env])
    .toString()
}
