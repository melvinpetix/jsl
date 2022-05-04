def call() {
  def j = jobCfg()
  def env
  
  if (!environment) {
     return "";
  }
  env = j.environment.collect { k, v -> "${k}=${v}"};
     return env
}
