package com.webops

class PipelineConfig implements Serializable {
    def project_name
    def parameters
    def environment
    def notification
    def env
    def steps
    def PipelineConfig(steps, notification, parameters, project_name){
    this.steps = steps
    this.notification = notification        
    this.parameters = parameters   
    this.project_name = project_name
    
}

    PipelineConfig build() {
        new PipelineConfig(project_name: this.project_name,
                            parameters: this.parameters,
                            notification: this.notification,
                            steps: this.steps)
    }

}
        
