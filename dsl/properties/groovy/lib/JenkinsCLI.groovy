import com.cloudbees.flowpdf.*
import com.cloudbees.flowpdf.components.ComponentManager
import com.cloudbees.flowpdf.components.cli.CLI
import com.cloudbees.flowpdf.components.cli.Command
import com.cloudbees.flowpdf.components.cli.ExecutionResult
import com.cloudbees.flowpdf.exceptions.EntityDoesNotExist

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

        /* Log is automatically available from the parent class */
        log.info(
                "restartJenkins was invoked with StepParameters",
                /* runtimeParameters contains both configuration and procedure parameters */
                p.toString()
        )

        ExecutionResult result = executeCommand(["restart"])

        if (!result.isSuccess()) {
            log.error(result.toString())
            sr.setJobStepOutcome('error')
            sr.setJobStepSummary("Failed to execute command. See log for details")
            return
        }

        sr.setJobStepSummary("Restarted.")
        sr.apply()

        if (sp.getWaitForServer()) {
            sr.setJobStepSummary("Waiting for server to start.")
            int timeout = 300
            while (!isServerRunning() && timeout > 0) {
                sr.setJobStepSummary("$timeout seconds left before timeout.")
                sr.apply()
                timeout -= 5
                sleep(5000)
            }

            if (timeout > 0) {
                sr.flush()
                sr.setJobStepSummary("Jenkins is running after restart.")
            } else if (timeout <= 0) {
                sr.setJobStepSummary("Reached timeout while waiting server to start.")
                sr.setJobStepOutcome('error')
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

        /* Log is automatically available from the parent class */
        log.info(
                "installPlugin was invoked with StepParameters",
                /* runtimeParameters contains both configuration and procedure parameters */
                p.toString()
        )

        Context context = getContext()

        // Setting job step summary to the config name
        sr.setJobStepSummary(p.getParameter('config').getValue() ?: 'null')

        sr.setReportUrl("Sample Report", 'https://cloudbees.com')
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

        /* Log is automatically available from the parent class */
        log.info(
                "applyConfiguration was invoked with StepParameters",
                /* runtimeParameters contains both configuration and procedure parameters */
                p.toString()
        )

        Context context = getContext()

        // Setting job step summary to the config name
        sr.setJobStepSummary(p.getParameter('config').getValue() ?: 'null')

        sr.setReportUrl("Sample Report", 'https://cloudbees.com')
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

        /* Log is automatically available from the parent class */
        log.info(
                "executeScript was invoked with StepParameters",
                /* runtimeParameters contains both configuration and procedure parameters */
                p.toString()
        )

        Context context = getContext()

        // Setting job step summary to the config name
        sr.setJobStepSummary(p.getParameter('config').getValue() ?: 'null')

        sr.setReportUrl("Sample Report", 'https://cloudbees.com')
        sr.apply()
        log.info("step Execute script has been finished")
    }

// === step ends ===

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
        String endpoint = getContext()
                .getConfigValues()
                .getRequiredParameter('endpoint')
                .getValue()


        FileOutputStream fileStream = new FileOutputStream(new File(filepath))
        BufferedInputStream input = new BufferedInputStream(
                new URL(endpoint + '/jnlpJars/jenkins-cli.jar').openStream()
        )

        byte[] dataBuffer = new byte[1024]
        int bytesRead;
        while ((bytesRead = input.read(dataBuffer, 0, 1024)) != -1) {
            fileStream.write(dataBuffer, 0, bytesRead);
        }
    }

    File writeToFile(String content) {
        File temp = new File("./command_" + FlowAPI.getFlowProperty('/myJobStep/id'))
        temp.write(content)
        return temp
    }

    ExecutionResult executeCommand(List<String> parameters, File stdinFile = null) {

        Config config = this.getContext().getCurrentStepConfigValues()
        CLI cli = (CLI) ComponentManager.loadComponent(
                CLI.class,
                [workingDirectory: System.getProperty('user.dir')],
                this
        )

        String cliPath = getCliPath()
        String endpoint = config.getRequiredParameter('endpoint').getValue()

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

}