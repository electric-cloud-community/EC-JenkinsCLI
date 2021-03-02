package com.cloudbees.pdk.hen.procedures

import groovy.transform.AutoClone
import com.cloudbees.pdk.hen.*

@AutoClone
//generated
class JenkinsCLIConfig extends Procedure {

    static JenkinsCLIConfig create(Plugin plugin) {
        return new JenkinsCLIConfig(procedureName: 'CreateConfiguration', plugin: plugin, credentials: [
            
            'credential': null,
            
        ])
    }


    JenkinsCLIConfig flush() {
        this.flushParams()
        return this
    }

    //Generated
    
    JenkinsCLIConfig checkConnection(boolean checkConnection) {
        this.addParam('checkConnection', checkConnection)
        return this
    }
    
    
    JenkinsCLIConfig cliPath(String cliPath) {
        this.addParam('cliPath', cliPath)
        return this
    }
    
    
    JenkinsCLIConfig config(String config) {
        this.addParam('config', config)
        return this
    }
    
    
    JenkinsCLIConfig debugLevel(String debugLevel) {
        this.addParam('debugLevel', debugLevel)
        return this
    }
    
    JenkinsCLIConfig debugLevel(DebugLevelOptions debugLevel) {
        this.addParam('debugLevel', debugLevel.toString())
        return this
    }
    
    
    JenkinsCLIConfig desc(String desc) {
        this.addParam('desc', desc)
        return this
    }
    
    
    JenkinsCLIConfig downloadCli(boolean downloadCli) {
        this.addParam('downloadCli', downloadCli)
        return this
    }
    
    
    JenkinsCLIConfig endpoint(String endpoint) {
        this.addParam('endpoint', endpoint)
        return this
    }
    
    
    
    JenkinsCLIConfig credential(String user, String password) {
        this.addCredential('credential', user, password)
        return this
    }

    JenkinsCLIConfig credentialReference(String path) {
        this.addCredentialReference('credential', path)
        return this
    }
    
    
    enum DebugLevelOptions {
    
    INFO("0"),
    
    DEBUG("1"),
    
    TRACE("2")
    
    private String value
    DebugLevelOptions(String value) {
        this.value = value
    }

    String toString() {
        return this.value
    }
}
    
}