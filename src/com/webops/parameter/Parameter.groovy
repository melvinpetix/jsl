package com.webops.parameter


@MapConstructor
@ToString(includeNames=true, includeFields=true)
class Parameter {
    
    String name
    
    String description
   
    List<String> choices
}
