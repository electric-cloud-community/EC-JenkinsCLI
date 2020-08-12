package com.electriccloud.cd.plugins.jenkinscliwrapper.cli

import groovy.transform.CompileStatic

/**
 * This class represents a command-line execution result with exit code, stdout and stderr.
 * Method toString() is overloaded to simplify logging of the execution result.
 */
@CompileStatic
class ExecutionResult {
    /** Content of the standart output after the command execution */
    public String stdOut

    /** Content of the standart output after the command execution */
    public String stdErr

    /** Exit code returned by the program after the execution */
    public int code

    /**
     * Constructor
     * @param initArgs
     */
    ExecutionResult(Map initArgs) {
        this.stdOut = initArgs['stdOut']
        this.stdErr = initArgs['stdErr']
        this.code = (int) initArgs['code']
    }

    /**
     * This method is used to ensure that there is no error for execution of the command.
     * @return true if exitCode equals 0
     */
    boolean isSuccess() {
        return code == 0
    }

    /**
     * Accessor for the {@link ExecutionResult#stdOut}
     * @return See{@link ExecutionResult#stdOut}
     */
    String getStdOut() {
        return stdOut
    }

    /**
     * Accessor for the {@link ExecutionResult#stdErr}
     * @return See{@link ExecutionResult#stdErr}
     */
    String getStdErr() {
        return stdErr
    }

    /**
     * Accessor for the {@link ExecutionResult#code}
     * @return See{@link ExecutionResult#code}
     */
    int getCode() {
        return code
    }

    @Override
    String toString() {
        return ([
                stdOut: stdOut,
                stdErr: stdErr,
                code  : code
        ]).toString()
    }
}
