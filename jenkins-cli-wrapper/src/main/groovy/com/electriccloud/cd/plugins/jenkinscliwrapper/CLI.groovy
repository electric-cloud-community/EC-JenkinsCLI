package com.electriccloud.cd.plugins.jenkinscliwrapper

import com.electriccloud.cd.plugins.jenkinscliwrapper.cli.Command

public class CLI {

    static String workingDirectory

    static Command newCommand(String program, ArrayList<String> parameters = null, String workingDirectory = null) {
        String effectiveWorkingDirectory = workingDirectory ?: this.workingDirectory ?: null

        return new Command(
                program: program,
                args: parameters,
                workingDirectory: effectiveWorkingDirectory
        )
    }


}
