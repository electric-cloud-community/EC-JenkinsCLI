package com.electriccloud.cd.plugins.jenkinscliwrapper.cli

class Utils {
    static String findJava() {
        // Run using commander Java if available
        if (System.getenv("COMMANDER_HOME")) {
            return System.getenv("COMMANDER_HOME") + "/jre/bin/java"
        }

        // Check JAVA_HOME
        if (System.getenv("JAVA_HOME")) {
            return System.getenv("JAVA_HOME") + "/jre/bin/java"
        }

        // Hope that this will work
        return 'java'
    }

    static File writeToTempFile(String content) {
        File temp = File.createTempFile('jenkins-cli', '.tmp')
        temp.write(content)
        return temp
    }

    /**
     * Checks if current OS is Windows.
     * @return true if current OS is Windows
     */
    static boolean isWindows() {
        String osName = System.properties['os.name']
        return osName.toLowerCase().contains('windows')
    }
}
