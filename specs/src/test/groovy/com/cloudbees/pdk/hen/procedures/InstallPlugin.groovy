package com.cloudbees.pdk.hen.procedures

import groovy.transform.AutoClone
import com.cloudbees.pdk.hen.*

@AutoClone
//generated
class InstallPlugin extends Procedure {

    static InstallPlugin create(Plugin plugin) {
        return new InstallPlugin(procedureName: 'Install Plugin', plugin: plugin, )
    }


    InstallPlugin flush() {
        this.flushParams()
        return this
    }

    //Generated
    
    InstallPlugin config(String config) {
        this.addParam('config', config)
        return this
    }
    
    
    InstallPlugin pluginPath(String pluginPath) {
        this.addParam('pluginPath', pluginPath)
        return this
    }
    
    
    
    
}