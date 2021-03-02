package com.cloudbees.pdk.hen.procedures

import groovy.transform.AutoClone
import com.cloudbees.pdk.hen.*

@AutoClone
//generated
class ExecuteCustomCommand extends Procedure {

    static ExecuteCustomCommand create(Plugin plugin) {
        return new ExecuteCustomCommand(procedureName: 'Execute Custom Command', plugin: plugin, )
    }


    ExecuteCustomCommand flush() {
        this.flushParams()
        return this
    }

    //Generated
    
    ExecuteCustomCommand arguments(String arguments) {
        this.addParam('arguments', arguments)
        return this
    }
    
    
    ExecuteCustomCommand command(String command) {
        this.addParam('command', command)
        return this
    }
    
    
    ExecuteCustomCommand config(String config) {
        this.addParam('config', config)
        return this
    }
    
    
    ExecuteCustomCommand filePath(String filePath) {
        this.addParam('filePath', filePath)
        return this
    }
    
    
    ExecuteCustomCommand inputPath(String inputPath) {
        this.addParam('inputPath', inputPath)
        return this
    }
    
    
    ExecuteCustomCommand inputText(String inputText) {
        this.addParam('inputText', inputText)
        return this
    }
    
    
    ExecuteCustomCommand resultProperty(String resultProperty) {
        this.addParam('resultProperty', resultProperty)
        return this
    }
    
    
    ExecuteCustomCommand usesStdin(boolean usesStdin) {
        this.addParam('usesStdin', usesStdin)
        return this
    }
    
    
    
    
}