package com.cloudbees.pdk.hen.procedures

import groovy.transform.AutoClone
import com.cloudbees.pdk.hen.*

@AutoClone
//generated
class ImportJobfromXML extends Procedure {

    static ImportJobfromXML create(Plugin plugin) {
        return new ImportJobfromXML(procedureName: 'Import Job from XML', plugin: plugin, )
    }


    ImportJobfromXML flush() {
        this.flushParams()
        return this
    }

    //Generated
    
    ImportJobfromXML config(String config) {
        this.addParam('config', config)
        return this
    }
    
    
    ImportJobfromXML jenkinsJobName(String jenkinsJobName) {
        this.addParam('jenkinsJobName', jenkinsJobName)
        return this
    }
    
    
    ImportJobfromXML xmlFilePath(String xmlFilePath) {
        this.addParam('xmlFilePath', xmlFilePath)
        return this
    }
    
    
    ImportJobfromXML xmlFileScriptContent(String xmlFileScriptContent) {
        this.addParam('xmlFileScriptContent', xmlFileScriptContent)
        return this
    }
    
    
    
    
}