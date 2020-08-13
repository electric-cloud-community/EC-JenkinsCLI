package com.electriccloud.cd.plugins.jenkinscliwrapper

import com.electriccloud.cd.plugins.jenkinscliwrapper.cli.Command
import com.electriccloud.cd.plugins.jenkinscliwrapper.cli.ExecutionResult
import com.electriccloud.cd.plugins.jenkinscliwrapper.cli.Utils

/**
 * JenkinsCLIWrapper
 */
class JenkinsCLIWrapper {

    private String endpoint
    private String username
    private String password
    private String cliPath = "/tmp/jenkinsCLI.jar"
    private boolean downloadCli = true

    JenkinsCLIWrapper(String endpoint, String username, String password,
                      String cliPath = null, boolean downloadCli = true) {
        this.endpoint = endpoint
        this.username = username
        this.password = password

        if (cliPath != null) {
            this.cliPath = cliPath
        }

        if (!downloadCli && !(new File(this.cliPath).exists())) {
            throw new RuntimeException("File at ${this.cliPath} does not exist and the 'Download CLI' is set to false")
        }
    }

    /**
     * restartJenkins - Restart Jenkins/Restart Jenkins
     * Add your code into this method and it will be called when the step runs
     * @param config (required: true)
     * @param safe (required: false)
     */
    ExecutionResult restartJenkins(boolean safeRestart) {
        String command = safeRestart ? 'safe-restart' : 'restart'
        return executeCommand([command])
    }

    /**
     * installPlugin - Install Plugin/Install Plugin
     * Add your code into this method and it will be called when the step runs
     * @param pluginPath (required: true)

     */
    ExecutionResult installPlugin(String path) {
        // Local files should be passed to STDIN
        if (path.startsWith('/')) {
            File pluginFile = new File(path)
            if (!pluginFile.exists()) {
                throw new RuntimeException("File $path does not exist")
            }
            return executeCommand(["install-plugin", '='], pluginFile)
        } else {
            return executeCommand(["install-plugin", path])
        }
    }

    /**
     * applyConfiguration - Apply Configuration/Apply Configuration
     * Add your code into this method and it will be called when the step runs
     * @param configurationPath (required: false)
     * @param configurationYaml (required: false)
     */
    ExecutionResult applyConfiguration(File configurationFile) {
        return executeCommand(["apply-configuration"], configurationFile)
    }

    /**
     * executeScript - Execute script/Execute script
     * Add your code into this method and it will be called when the step runs
     * @param config (required: true)
     * @param scriptPath (required: false)
     * @param scriptText (required: false)

     */
    ExecutionResult executeScript(File scriptFile) {
        return executeCommand(["groovy", "="], scriptFile)
    }

    /**
     * importJenkinsJob - Imports Jenkins Job XML
     */
    ExecutionResult importJenkinsJob(String jenkinsJobName, File scriptFile) {
        return executeCommand(["create-job", jenkinsJobName], scriptFile)
    }

    /**
     * readJenkinsJob - Exports Jenkins Job XML to a file
     * The result will be
     */
    ExecutionResult exportJenkinsJob(String jenkinsJobName) throws RuntimeException {
        return executeCommand(["get-job", jenkinsJobName])
    }

    void pollUntilServerAvailable(int timeout = 300, Closure waitCallback = null) {
        int pollingPeriod = 5
        int waited = timeout

        boolean running = false

        while (!running && waited > 0) {
            if (waitCallback != null) waitCallback.call(waited, null)

            try {
                running = isServerRunning()
            } catch (RuntimeException ex) {
                if (waitCallback != null) waitCallback.call(waited, ex.getMessage())
            }

            waited -= pollingPeriod

            sleep(pollingPeriod * 1000)
        }

        if (waited <= 0) {
            throw new RuntimeException("Server is not available after $timeout seconds")
        }
    }

    boolean isServerRunning() throws RuntimeException {
        httpClient().isAccessible('/api/json')
    }

    private SimpleHttpClient httpClient() {
        return new SimpleHttpClient(endpoint: endpoint, username: username, password: password)
    }

    private String getCliPath() {
        if (cliPath == null) {
            cliPath = '/tmp/jenkins-cli.jar'
        }

        boolean fileExists = new File(cliPath).exists()
        if (!fileExists && downloadCli) {
            downloadCliTool(cliPath)
        } else if (!fileExists) {
            throw new RuntimeException("CLI is not found at '$cliPath'" +
                    " and 'Download CLI' is set to '$downloadCli'." +
                    " Change the configuration or download the CLI jar file.")
        }

        return cliPath
    }

    private void downloadCliTool(String filepath) {
        if (!isServerRunning()) {
            println("[DEBUG] We are downloading the jenkins-cli.jar" +
                    " but server is not running yet, so we will wait 90 seconds for server to start"
            )
            if (!pollUntilServerAvailable(90)) {
                throw new RuntimeException(
                        "Server was not ready to provide the jenkins-cli.jar in 90 seconds." +
                                " Consider downloading the tool or add sleep in your code." +
                                " This also can be caused by network error. Check log for details."
                )
            }
        }

        String path = '/jnlpJars/jenkins-cli.jar'
        httpClient().download(path, filepath)
    }

    ExecutionResult executeCommand(List<String> parameters, File stdinFile = null) {
        ArrayList<String> args = new ArrayList<>()
        args.addAll(["-jar", getCliPath(), "-s", "$endpoint", "-auth", "$username:$password"])
        args.addAll(parameters)

        Command cmd = CLI.newCommand(Utils.findJava(), args)
        if (stdinFile != null) {
            cmd.setRedirectInput(stdinFile)
        }

        return cmd.execute()
    }

}