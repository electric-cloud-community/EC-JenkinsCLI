package com.cloudbees.pdk.hen.procedures

import groovy.transform.AutoClone
import com.cloudbees.pdk.hen.*

@AutoClone
//generated
class WaitforServer extends Procedure {

    static WaitforServer create(Plugin plugin) {
        return new WaitforServer(procedureName: 'Wait for Server', plugin: plugin, )
    }


    WaitforServer flush() {
        this.flushParams()
        return this
    }

    //Generated
    
    WaitforServer config(String config) {
        this.addParam('config', config)
        return this
    }
    
    
    WaitforServer waitTimeout(String waitTimeout) {
        this.addParam('waitTimeout', waitTimeout)
        return this
    }
    
    
    
    
}