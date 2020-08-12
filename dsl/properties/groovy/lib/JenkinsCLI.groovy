import com.cloudbees.flowpdf.*
import com.cloudbees.flowpdf.exceptions.UnexpectedEmptyValue
import com.cloudbees.flowpdf.exceptions.WrongFunctionArgumentValue
import com.electriccloud.cd.plugins.jenkinscliwrapper.JenkinsCLIWrapper
import com.electriccloud.cd.plugins.jenkinscliwrapper.cli.ExecutionResult
import com.electriccloud.client.groovy.models.ActualParameter
import com.electriccloud.client.groovy.models.Credential
import groovy.json.JsonOutput

/**
 * JenkinsCLIWrapper
 */
class JenkinsCLI extends FlowPlugin {

    @Override
    Map<String, Object> pluginInfo() {
        return [
                pluginName         : '@PLUGIN_KEY@',
                pluginVersion      : '@PLUGIN_VERSION@',
                configFields       : ['config'],
                configLocations    : ['ec_plugin_cfgs'],
                defaultConfigValues: [authScheme: 'basic']
        ]
    }

    /**
     * restartJenkins - Restart Jenkins/Restart Jenkins
     * Add your code into this method and it will be called when the step runs
     * @param config (required: true)
     * @param waitForServer (required: true)

     */
    def restartJenkins(StepParameters p, StepResult sr) {
        // Use this parameters wrapper for convenient access to your parameters
        RestartJenkinsParameters sp = RestartJenkinsParameters.initParameters(p)
        JenkinsCLIWrapper wrapper = getWrapper()

        def result = wrapper.restartJenkins(sp.safe)

        if (!result.isSuccess()) {
            log.error(result.toString())
            sr.setJobStepOutcome('error')
            sr.setJobStepSummary("Failed to execute command. See log for details")
            return
        }

        sr.setJobStepSummary("Restarted.")
        sr.apply()

        if (sp.waitForServer) {
            try {
                wrapper.pollUntilServerAvailable(300, { int waited ->
                    sr.setJobStepOutcome("Waiting ${waited}/300 seconds")
                    sr.apply()
                })
                sr.setJobStepOutcome("success")
                sr.setJobStepSummary("Jenkins running after restart")
            } catch (RuntimeException ex) {
                sr.setJobStepOutcome("error")
                sr.setJobStepSummary(ex.getMessage())
            }
        }
    }

    /**
     * installPlugin - Install Plugin/Install Plugin
     * Add your code into this method and it will be called when the step runs
     * @param config (required: true)
     * @param pluginPath (required: true)

     */
    def installPlugin(StepParameters p, StepResult sr) {
        InstallPluginParameters sp = InstallPluginParameters.initParameters(p)
        String path = sp.getPluginPath()

        ExecutionResult result = getWrapper().installPlugin(path)

        if (!result.isSuccess()) {
            log.error(result.toString())
            sr.setJobStepOutcome('error')
            sr.setJobStepSummary("Failed to execute command. See log for details")
            return
        }

        sr.setJobStepSummary("Success")
        sr.apply()
    }

    /**
     * applyConfiguration - Apply Configuration/Apply Configuration
     * Add your code into this method and it will be called when the step runs
     * @param config (required: true)
     * @param configurationPath (required: false)
     * @param configurationYaml (required: false)

     */
    def applyConfiguration(StepParameters p, StepResult sr) {
        // Use this parameters wrapper for convenient access to your parameters
        ApplyConfigurationParameters sp = ApplyConfigurationParameters.initParameters(p)

        File scriptFile = contentOrFile(sp.getConfigurationYaml(), sp.getConfigurationPath())
        if (!scriptFile) {
            throw new UnexpectedEmptyValue(
                    "One of 'Script Path' or 'Script Text' should be specified."
            )
        }

        ExecutionResult result = getWrapper().applyConfiguration(scriptFile)

        if (!result.isSuccess()) {
            log.error(result.toString())
            sr.setJobStepOutcome('error')
            sr.setJobStepSummary("Failed to execute command. See log for details")
            return
        }

        sr.setJobStepSummary("Success")
        sr.apply()
        log.info("step Apply Configuration has been finished")
    }

    /**
     * executeScript - Execute script/Execute script
     * Add your code into this method and it will be called when the step runs
     * @param config (required: true)
     * @param scriptPath (required: false)
     * @param scriptText (required: false)

     */
    def executeScript(StepParameters p, StepResult sr) {
        // Use this parameters wrapper for convenient access to your parameters
        ExecuteScriptParameters sp = ExecuteScriptParameters.initParameters(p)

        File scriptFile = contentOrFile(sp.getScriptText(), sp.getScriptPath())
        if (!scriptFile) {
            throw new UnexpectedEmptyValue(
                    "One of 'Script Path' or 'Script Text' should be specified."
            )
        }

        ExecutionResult result = getWrapper().executeScript(scriptFile)

        if (!result.isSuccess()) {
            log.error(result.toString())
            sr.setJobStepOutcome('error')
            sr.setJobStepSummary("Failed to execute command. See log for details")
            return
        }

        sr.setJobStepSummary("Success.")
        sr.apply()
        log.info("step Execute script has been finished")
    }

    /**
     * waitForServer - Wait for Server/Wait for Server
     * Add your code into this method and it will be called when the step runs
     * @param config (required: true)
     * @param waitTimeout (required: true)

     */
    def waitForServer(StepParameters p, StepResult sr) {
        // Use this parameters wrapper for convenient access to your parameters
        WaitForServerParameters sp = WaitForServerParameters.initParameters(p)

        int timeout = Integer.parseInt(sp.getWaitTimeout())

        try {
            wrapper.pollUntilServerAvailable(timeout, { int waited ->
                sr.setJobStepOutcome("Waiting ${waited}/300 seconds")
                sr.apply()
            })
            sr.setJobStepOutcome("success")
            sr.setJobStepSummary("Jenkins running after restart")
        } catch (RuntimeException ex) {
            sr.setJobStepOutcome("error")
            sr.setJobStepSummary(ex.getMessage())
        }
    }

    /**
     * createMasterConfiguration - Create Master Configuration/Create Master Configuration
     * Add your code into this method and it will be called when the step runs
     * @param config (required: true)
     * @param masterName (required: true)

     */
    def createMasterConfiguration(StepParameters p, StepResult sr) {
        // Use this parameters wrapper for convenient access to your parameters
        CreateMasterConfigurationParameters sp = CreateMasterConfigurationParameters.initParameters(p)

        // Simply calling CreateConfiguration with updated name and url
        String masterName = sp.masterName.toLowerCase()

        if (!(masterName =~ /^[a-zA-Z0-9_-]+$/)) {
            throw new WrongFunctionArgumentValue("Master name can't contain non-alphanumeric symbols");
        }

        String config = p.getRequiredParameter("config").getValue()

        Map<String, String> configMap = context.getConfigValues().getAsMap()
        configMap.description = "Created from configuration '${config}' for master '${masterName}'"
        configMap.endpoint = sanitizeEndpoint(configMap.endpoint) - 'cjoc' + masterName
        configMap.config = config + '_' + masterName

        com.cloudbees.flowpdf.Credential cjocCredential = p.getRequiredCredential('credential')

        log.info("Starting CreateConfiguration")
        log.debug("with parameters: " + configMap.toString())

        def procedureRun = FlowAPI.getEc().runProcedure([
                projectName     : getPluginProjectName(),
                procedureName   : 'CreateConfiguration',
                actualParameters: [
                        new ActualParameter(
                                actualParameterName: 'config',
                                value: configMap.config
                        ),
                        new ActualParameter(
                                actualParameterName: 'endpoint',
                                value: configMap.endpoint
                        ),
                        new ActualParameter(
                                actualParameterName: 'desc',
                                value: configMap.description
                        ),
                        new ActualParameter(
                                actualParameterName: 'downloadCli',
                                value: configMap.downloadCli
                        ),
                        new ActualParameter(
                                actualParameterName: 'debugLevel',
                                value: configMap.debugLevel
                        ),
                        new ActualParameter(
                                actualParameterName: 'cliPath',
                                value: configMap.cliPath
                        ),
                        new ActualParameter(
                                actualParameterName: 'checkConnection',
                                value: configMap.checkConnection
                        ),
                        new ActualParameter(
                                actualParameterName: 'credential',
                                value: 'credential'
                        )

                ],
                credentials     : [
                        new Credential(
                                credentialName: 'credential',
                                userName: cjocCredential.getUserName(),
                                password: cjocCredential.getSecretValue(),
                        )
                ]
        ])

        Map<String, Object> jobDetails = ['status': 'unknown']
        int timeout = 60

        while (jobDetails['status'] != 'completed' && timeout >= 0) {
            jobDetails = FlowAPI.getEc().getJobDetails(jobId: procedureRun.jobId)['job']
            logJobDetails(jobDetails.clone() as Map<String, Object>)

            if (jobDetails['status'] == 'completed') {
                break
            }

            timeout -= 5
            Thread.sleep(5000)
        }

        logJobDetails(jobDetails.clone() as Map<String, Object>)

        if (jobDetails['outcome'] != 'success') {
            sr.setJobStepOutcome('error')
            sr.setJobStepSummary("CreateConfiguration failed")
        } else {
            sr.setOutcomeProperty(sp.resultProperty, configMap.config as String)
        }

    }

// === step ends ===

    JenkinsCLIWrapper getWrapper() {
        Config config = this.getContext().getConfigValues()

        com.cloudbees.flowpdf.Credential cred = config.getRequiredCredential('credential')
        String downloadCli = config.getParameter('downloadCli').getValue()

        JenkinsCLIWrapper wrapper = new JenkinsCLIWrapper(
                endpoint: config.getRequiredParameter('endpoint').getValue(),
                username: cred.getUserName(),
                password: cred.getSecretValue(),
                cliPath: config.getParameter('cliPath').getValue(),
                downloadCli: Boolean.parseBoolean(downloadCli),
        )

        return wrapper
    }

    static File contentOrFile(String content, String filepath) {
        File scriptFile = null
        if (content) {
            scriptFile = writeToFile(content)
        } else if (filepath) {
            scriptFile = new File(filepath)
        }
        return scriptFile
    }

    static File writeToFile(String content) {
        File temp = File.createTempFile('jenkins-cli', '.tmp')
        temp.write(content)
        return temp
    }

    private static String sanitizeEndpoint(String endpoint) {
        if (endpoint.endsWith('/')) {
            endpoint = endpoint.replaceAll(/\/+$/, '')
        }

        return endpoint
    }

    private static void logJobDetails(Map<String, Object> jobDetails) {
        def cleaned = cleanCommandProperty(jobDetails)
        Log.logDebug("JOB DETAILS : " + JsonOutput.toJson(cleaned).toString())
    }

    private static def cleanCommandProperty(Map<String, Object> structure) {
        structure.each { k, v ->
            if (k == "command") {
                structure.remove("command")
            } else if (v instanceof Map) {
                structure[k] = cleanCommandProperty(v)
            }
        }
        return structure
    }

}