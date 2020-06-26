import com.cloudbees.flowpdf.*
import com.cloudbees.flowpdf.client.REST
import com.cloudbees.flowpdf.components.ComponentManager
import com.cloudbees.flowpdf.components.cli.CLI
import com.cloudbees.flowpdf.components.cli.Command
import com.cloudbees.flowpdf.components.cli.ExecutionResult
import com.cloudbees.flowpdf.exceptions.EntityDoesNotExist
import com.cloudbees.flowpdf.exceptions.UnexpectedEmptyValue
import com.cloudbees.flowpdf.exceptions.WrongFunctionArgumentValue
import com.electriccloud.client.groovy.models.ActualParameter
import groovy.json.JsonOutput

/**
 * JenkinsCLI
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

        String command = (sp.safe) ? 'safe-restart' : 'restart'
        ExecutionResult result = executeCommand([command])

        if (!result.isSuccess()) {
            log.error(result.toString())
            sr.setJobStepOutcome('error')
            sr.setJobStepSummary("Failed to execute command. See log for details")
            return
        }

        sr.setJobStepSummary("Restarted.")
        sr.apply()

        if (sp.waitForServer) {
            if (pollUntilServerAvailable(300, sr)) {
                sr.setJobStepOutcome("success")
                sr.setJobStepSummary("Jenkins running after restart")
            } else {
                sr.setJobStepOutcome("error")
                sr.setJobStepSummary("Reached timeout while waiting for server")
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
        // Use this parameters wrapper for convenient access to your parameters
        InstallPluginParameters sp = InstallPluginParameters.initParameters(p)

        String path = sp.getPluginPath()

        // Local files should be passed to STDIN
        ExecutionResult result
        if (path.startsWith('/')){
            File pluginFile = new File(path)
            if (!pluginFile.exists()){
                throw new WrongFunctionArgumentValue("File $path does not exist")
            }
            result = executeCommand(["install-plugin", '='], pluginFile)
        }
        else{
            result = executeCommand(["install-plugin", path])
        }


        if (!result.isSuccess()) {
            log.error(result.toString())
            sr.setJobStepOutcome('error')
            sr.setJobStepSummary("Failed to execute command. See log for details")
            return
        }

        sr.setJobStepSummary("Success.")
        sr.apply()
        log.info("step Install Plugin has been finished")
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

        ExecutionResult result = executeCommand(["apply-configuration"], scriptFile)

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

        ExecutionResult result = executeCommand(["groovy", "="], scriptFile)

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

        if (pollUntilServerAvailable(timeout, sr)) {
            sr.setJobStepSummary("Jenkins running after restart")
        } else {
            sr.setJobStepOutcome("error")
            sr.setJobStepSummary("Reached timeout while waiting for server")
        }

        sr.apply()
        log.info("step Wait for Server has been finished")
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

        Credential cjocCredential = p.getRequiredCredential('credential')

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
                        new com.electriccloud.client.groovy.models.Credential(
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

    File contentOrFile(String content, String filepath) {
        File scriptFile = null
        if (content) {
            scriptFile = writeToFile(content)
        } else if (filepath) {
            scriptFile = new File(filepath)
        }
        return scriptFile
    }

    private boolean pollUntilServerAvailable(int timeout = 300, StepResult sr) {
        int pollingPeriod = 5

        sr.setJobStepSummary("Waiting for server to start.")

        while (!isServerRunning() && timeout > 0) {
            sr.setJobStepSummary("$timeout seconds left before timeout.")
            sr.applyAndFlush()
            timeout -= pollingPeriod
            sleep(pollingPeriod * 1000)
        }

        if (timeout > 0) {
            sr.flush()
            sr.setJobStepSummary("Jenkins is running.")
        } else if (timeout <= 0) {
            sr.setJobStepSummary("Reached timeout while waiting server to start.")
            sr.setJobStepOutcome('error')
        }

        return timeout >= 0
    }

    boolean isServerRunning() {
        try {
            def res = executeCommand(['help'])
            return res.isSuccess()
        }
        catch (RuntimeException ex) {
            log.debug(ex.getMessage())
        }
        return false
    }

    String getCliPath() {
        Config config = getContext().getConfigValues()
        String filepath = config.getRequiredParameter('cliPath').getValue()
        String downloadCli = config.getRequiredParameter('downloadCli').getValue()

        boolean fileExists = new File(filepath).exists()

        if (!fileExists && downloadCli == "true") {
            log.debug("Downloading CLI tool to $filepath")
            downloadCliTool(filepath)
        } else if (!fileExists) {
            throw new EntityDoesNotExist("CLI is not found at '$filepath'" +
                    " and 'Download CLI' is set to '$downloadCli'." +
                    " Change the configuration or download the CLI jar file.")
        }

        return filepath
    }

    void downloadCliTool(String filepath) {
        String path = '/jnlpJars/jenkins-cli.jar'
        REST rest = getContext().newRESTClient()

        rest.setResponseContentType('BINARY')
        byte[] bytes = rest.request('GET', path) as byte[]

        FileOutputStream fileStream = new FileOutputStream(new File(filepath))
        fileStream.write(bytes)
    }

    File writeToFile(String content) {
        File temp = new File("./command_" + FlowAPI.getFlowProperty('/myJobStep/id'))
        temp.write(content)
        return temp
    }

    ExecutionResult executeCommand(List<String> parameters, File stdinFile = null) {

        Config config = this.getContext().getCurrentStepConfigValues()
        CLI cli

        // Trying to reload component before instantiating new one
        try {
            cli = (CLI) ComponentManager.getComponent(CLI.class)
        } catch (RuntimeException ex) {
            Map componentOptions = [workingDirectory: System.getProperty('user.dir')]
            cli = (CLI) ComponentManager.loadComponent(CLI.class, componentOptions, this)
        }

        String cliPath = getCliPath()
        String endpoint = config.getRequiredParameter('endpoint').getValue()
        endpoint = sanitizeEndpoint(endpoint)

        Credential cred = config.getRequiredCredential('credential')
        String username = cred.getUserName()
        String password = cred.getSecretValue()

        //"java -jar $cliPath -s $endpoint -auth $username:$password"

        String commanderHome = System.getenv("COMMANDER_HOME")
        log.debug("COMMANDER_HOME: $commanderHome")


        Command cmd = cli.newCommand(commanderHome + '/jre/bin/java')
//        cmd.addArguments()
        cmd.addArguments("-jar")
        cmd.addArguments("$cliPath")
        cmd.addArguments("-s")
        cmd.addArguments("$endpoint")
        cmd.addArguments("-auth")
        cmd.addArguments("$username:$password")
        cmd.addArguments((ArrayList<String>) parameters)

        ProcessBuilder pb = cmd.renderCommand()

        if (stdinFile != null) {
            pb.redirectInput(stdinFile)
        }

        return run(pb)
    }

    /**
     * Renders a {@link Command} to a ProcessBuilder and executes it.
     * @param command preconfigured {@link Command} instance
     * @return {@link ExecutionResult} instance.
     * @throws IOException if system returned error for executing the program
     */
    ExecutionResult run(ProcessBuilder pb) throws IOException {
        String commandRepresentation = pb.command().join(' ')
        try {
            Process process = pb.start()

            // Capturing the output
            StringBuffer out = new StringBuffer()
            StringBuffer err = new StringBuffer()
            process.consumeProcessOutput(out, err)

            // Timeout is specified in milliseconds
            process.waitForProcessOutput(out, err)

            def resultMap = [
                    code  : process.exitValue(),
                    stdOut: out.toString(),
                    stdErr: err.toString()
            ]

            log.trace("Result: ", resultMap.toString())

            return new ExecutionResult(resultMap)
        } catch (IOException ioe) {
            // Logging error and throwing again
            log.error("OS returned error while executing the command '$commandRepresentation'.",
                    ioe.getMessage()
            )

            throw ioe
        } catch (RuntimeException ex) {
            log.error("Error happened during execution of the command '$commandRepresentation'.",
                    ex.getMessage()
            )
            throw ex
        }
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