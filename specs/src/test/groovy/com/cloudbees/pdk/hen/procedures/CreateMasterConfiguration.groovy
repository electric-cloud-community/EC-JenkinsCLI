package com.cloudbees.pdk.hen.procedures

import groovy.transform.AutoClone
import com.cloudbees.pdk.hen.*

@AutoClone
//generated
class CreateMasterConfiguration extends Procedure {

    static CreateMasterConfiguration create(Plugin plugin) {
        return new CreateMasterConfiguration(procedureName: 'Create Master Configuration', plugin: plugin, )
    }


    CreateMasterConfiguration flush() {
        this.flushParams()
        return this
    }

    //Generated
    
    CreateMasterConfiguration config(String config) {
        this.addParam('config', config)
        return this
    }
    
    
    CreateMasterConfiguration masterName(String masterName) {
        this.addParam('masterName', masterName)
        return this
    }
    
    
    CreateMasterConfiguration resultProperty(String resultProperty) {
        this.addParam('resultProperty', resultProperty)
        return this
    }
    
    
    
    
}