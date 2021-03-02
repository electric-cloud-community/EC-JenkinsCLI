package com.cloudbees.pdk.hen

import com.cloudbees.pdk.hen.Plugin
import com.cloudbees.pdk.hen.procedures.*

import static com.cloudbees.pdk.hen.Utils.env

class JenkinsCLI extends Plugin {

    static JenkinsCLI create() {
        JenkinsCLI plugin = new JenkinsCLI(name: 'EC-JenkinsCLI')
        plugin.configure(plugin.config)
        return plugin
    }

    static JenkinsCLI createWithoutConfig() {
        JenkinsCLI plugin = new JenkinsCLI(name: 'EC-JenkinsCLI')
        return plugin
    }

    //user-defined after boilerplate was generated, default parameters setup
    JenkinsCLIConfig config = JenkinsCLIConfig
        .create(this)
        .checkConnection(true)
        .downloadCli(true)
        .cliPath('/tmp/jenkins-cli.jar')
        .endpoint(env("JENKINS_URL", "http://jenkins:8080"))
        .credential(env("JENKINS_USER"), env("JENKINS_PASSWORD"))
    //.parameter(value) add parameters here


    ApplyConfiguration applyConfiguration = ApplyConfiguration.create(this)

    CreateMasterConfiguration createMasterConfiguration = CreateMasterConfiguration.create(this)

    EditConfiguration editConfiguration = EditConfiguration.create(this)

    ExecuteCustomCommand executeCustomCommand = ExecuteCustomCommand.create(this)

    ExecuteScript executeScript = ExecuteScript.create(this)

    ExportJobtoXML exportJobtoXML = ExportJobtoXML.create(this)

    ImportJobfromXML importJobfromXML = ImportJobfromXML.create(this)

    InstallPlugin installPlugin = InstallPlugin.create(this)

    RestartJenkins restartJenkins = RestartJenkins.create(this)

    WaitforServer waitforServer = WaitforServer.create(this)

}