package com.cloudbees.pdk.hen.procedures

import groovy.transform.AutoClone
import com.cloudbees.pdk.hen.*

@AutoClone
//generated
class ExportJobtoXML extends Procedure {

    static ExportJobtoXML create(Plugin plugin) {
        return new ExportJobtoXML(procedureName: 'Export Job to XML', plugin: plugin, )
    }


    ExportJobtoXML flush() {
        this.flushParams()
        return this
    }

    //Generated
    
    ExportJobtoXML config(String config) {
        this.addParam('config', config)
        return this
    }
    
    
    ExportJobtoXML filePath(String filePath) {
        this.addParam('filePath', filePath)
        return this
    }
    
    
    ExportJobtoXML jenkinsJobName(String jenkinsJobName) {
        this.addParam('jenkinsJobName', jenkinsJobName)
        return this
    }
    
    
    ExportJobtoXML resultProperty(String resultProperty) {
        this.addParam('resultProperty', resultProperty)
        return this
    }
    
    
    
    
}