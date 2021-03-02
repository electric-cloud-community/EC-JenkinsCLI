package com.cloudbees.plugin.spec

import com.cloudbees.pdk.hen.JenkinsCLI
import com.cloudbees.pdk.hen.JobResponse
import com.cloudbees.pdk.hen.procedures.ExecuteCustomCommand
import spock.lang.Specification

class BasicWrapperTest extends Specification {

    static JenkinsCLI plugin

    def setupSpec() {
        plugin = JenkinsCLI.create()
    }

    def "Get Jenkins version"() {
        when:
        ExecuteCustomCommand procedure = plugin.getExecuteCustomCommand()

        JobResponse resp = procedure
            .command('version')
            .filePath("")
            .arguments("")
            .inputText("")
            .inputPath("")
            .resultProperty('/myJob/version')
            .run()

        then:
        String version = resp.getJobProperties()['version']
        assert version
    }

}
