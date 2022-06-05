def call(params){
    if(string){
        input message: '', parameters: [string(name: params.string.name)]
    }
    if(choice){
        input message: '', parameters: [string(name: params.string.choice.name)]
    }
}
    
  
