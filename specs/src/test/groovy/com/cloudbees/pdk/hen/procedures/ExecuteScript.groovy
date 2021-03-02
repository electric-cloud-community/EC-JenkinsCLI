package com.cloudbees.pdk.hen.procedures

import groovy.transform.AutoClone
import com.cloudbees.pdk.hen.*

@AutoClone
//generated
class ExecuteScript extends Procedure {

    static ExecuteScript create(Plugin plugin) {
        return new ExecuteScript(procedureName: 'Execute Script', plugin: plugin, )
    }


    ExecuteScript flush() {
        this.flushParams()
        return this
    }

    //Generated
    
    ExecuteScript config(String config) {
        this.addParam('config', config)
        return this
    }
    
    
    ExecuteScript scriptPath(String scriptPath) {
        this.addParam('scriptPath', scriptPath)
        return this
    }
    
    
    ExecuteScript scriptText(String scriptText) {
        this.addParam('scriptText', scriptText)
        return this
    }
    
    
    
    
}