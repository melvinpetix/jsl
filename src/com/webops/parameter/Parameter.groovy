package com.webops.parameter
import groovy.transform.ToString

@MapConstructor
@ToString(includeNames=true, includeFields=true)

class Parameter {
    
    String name
    
    String description
   
    List<String> choices
}
