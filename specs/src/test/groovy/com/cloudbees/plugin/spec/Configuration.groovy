package com.cloudbees.plugin.spec

import com.cloudbees.pdk.hen.JenkinsCLI
import com.cloudbees.pdk.hen.JobResponse
import com.cloudbees.pdk.hen.Utils
import com.cloudbees.pdk.hen.procedures.JenkinsCLIConfig
import spock.lang.Specification

class Configuration extends Specification {

    static String host = Utils.env("JENKINS_URL")
    static String user = Utils.env("JENKINS_USER")
    static String pass = Utils.env("JENKINS_PASSWORD")

    def "Create configuration"() {
        when:
        JenkinsCLI pl = JenkinsCLI.createWithoutConfig()

        and:
        JenkinsCLIConfig config = JenkinsCLIConfig.create(pl)

        config
            .config("specs-config")
            .endpoint(host)
            .downloadCli(true)
            .checkConnection(true)
            .cliPath('/tmp/jenkinsCLI.jar')
            .credential(user, pass)

        then:
        JobResponse resp = pl.configure(config)
        assert resp.isSuccessful()
    }

}