package com.webops.parameter

import groovy.transform.MapConstructor

@MapConstructor

class Parameter {
    
    String name
    
    String description
   
    List<String> choices
}
