package com.electriccloud.cd.plugins.jenkinscliwrapper.cli

import java.util.concurrent.TimeUnit
import java.util.logging.Logger

class Command {

    static Logger logger = Logger.getAnonymousLogger()

    int timeout = 300
    String program
    String workingDirectory
    ArrayList<String> args = new ArrayList<>();
    File redirectInput

    ExecutionResult execute() throws IOException {
        ProcessBuilder pb = renderCommand()
        long timeout = getTimeout()

        String commandRepresentation = pb.command().join(' ')
        try {
            Process process = pb.start()

            // Capturing the output
            StringBuffer out = new StringBuffer()
            StringBuffer err = new StringBuffer()

            waitForProcess(process, timeout, out, err)

            Map<String, String> resultMap = [
                    code  : process.exitValue(),
                    stdOut: out.toString(),
                    stdErr: err.toString()
            ] as Map<String, String>

            logger.finest("Result: " + resultMap.toString())

            return new ExecutionResult(resultMap)

        } catch (IOException ioe) {
            // Logging error and throwing again
            logger.severe("OS returned error while execution of the command '$commandRepresentation'." +
                    ioe.getMessage()
            )
            throw ioe
        } catch (RuntimeException ex) {
            logger.severe("Error happened during execution of the command '$commandRepresentation'." +
                    ex.getMessage()
            )
            throw ex
        }
    }

    private static void waitForProcess(Process process, long timeout, Appendable out, Appendable err) {
        Thread tout = StreamReaderThread.runReaderStream(process.getInputStream(), out)
        Thread terr = StreamReaderThread.runReaderStream(process.getErrorStream(), err)
        if (!process.waitFor(timeout, TimeUnit.SECONDS)) {
            finishProcessByTimeout(process)
        } else {
            try {
                tout.join()
            } catch (InterruptedException ignore) {
            }
            try {
                terr.join()
            } catch (InterruptedException ignore) {
            }
            try {
                process.waitFor()
            } catch (InterruptedException ignore) {
            }
            process.closeStreams()
        }
    }

    private static void finishProcessByTimeout(Process process) {
        logger.severe("Process has not finished after the specified timeout."
                + " Sending SIGINT (may depend by platform) and giving 500 ms to finish.")

        process.destroy()
        process.waitFor(500, TimeUnit.MILLISECONDS)
        if (process.isAlive()) {
            logger.severe("Process has not finished in 500 ms after SIGINT." +
                    " Sending SIGKILL (may depend by platform) and waiting 100 ms.")
            process.destroyForcibly()
            process.waitFor(100, TimeUnit.MILLISECONDS)
        }
    }

    ProcessBuilder renderCommand() {

        // Flattening args
        // Flattening args
        ArrayList<String> procArgs = new ArrayList<>()
        procArgs.add(program)
        args.each { it -> procArgs.add((String) it)}

        logger.finest("Command: " + procArgs.join(' '))

        ProcessBuilder pb = new ProcessBuilder(procArgs)

        if (Utils.isWindows()) {
            logger.finer("Windows OS is detected. Environment variable 'NOPAUSE' is set to 1.")
            // Forcing windows to finish the process after execution
            Map<String, String> env = pb.environment()
            env.put('NOPAUSE', "1")
        }

        if (workingDirectory != null){
            pb.directory(new File(workingDirectory))
        }

        if (redirectInput != null) {
            pb.redirectInput(redirectInput)
        }

        return pb
    }

    void addArguments(String... arguments) {
        arguments.collect { args.add(it) }
    }

    void addArguments(ArrayList<String> arguments) {
        args.addAll(arguments)
    }

}
