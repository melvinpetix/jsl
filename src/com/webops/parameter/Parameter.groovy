package com.webops.parameter
import groovy.transform.ToString

@ToString(includeNames=true, includeFields=true)

class Parameter {
    
    String name
    
    String description
   
    List<String> choices
}
