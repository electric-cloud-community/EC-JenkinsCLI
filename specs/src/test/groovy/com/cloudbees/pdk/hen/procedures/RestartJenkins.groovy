package com.cloudbees.pdk.hen.procedures

import groovy.transform.AutoClone
import com.cloudbees.pdk.hen.*

@AutoClone
//generated
class RestartJenkins extends Procedure {

    static RestartJenkins create(Plugin plugin) {
        return new RestartJenkins(procedureName: 'Restart Jenkins', plugin: plugin, )
    }


    RestartJenkins flush() {
        this.flushParams()
        return this
    }

    //Generated
    
    RestartJenkins config(String config) {
        this.addParam('config', config)
        return this
    }
    
    
    RestartJenkins safe(boolean safe) {
        this.addParam('safe', safe)
        return this
    }
    
    
    RestartJenkins waitForServer(boolean waitForServer) {
        this.addParam('waitForServer', waitForServer)
        return this
    }
    
    
    
    
}