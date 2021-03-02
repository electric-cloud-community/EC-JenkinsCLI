package com.cloudbees.pdk.hen.procedures

import groovy.transform.AutoClone
import com.cloudbees.pdk.hen.*

@AutoClone
//generated
class ApplyConfiguration extends Procedure {

    static ApplyConfiguration create(Plugin plugin) {
        return new ApplyConfiguration(procedureName: 'Apply Configuration', plugin: plugin, )
    }


    ApplyConfiguration flush() {
        this.flushParams()
        return this
    }

    //Generated
    
    ApplyConfiguration config(String config) {
        this.addParam('config', config)
        return this
    }
    
    
    ApplyConfiguration configurationPath(String configurationPath) {
        this.addParam('configurationPath', configurationPath)
        return this
    }
    
    
    ApplyConfiguration configurationYaml(String configurationYaml) {
        this.addParam('configurationYaml', configurationYaml)
        return this
    }
    
    
    
    
}