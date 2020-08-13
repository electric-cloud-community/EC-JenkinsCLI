// This procedure.dsl was generated automatically
// DO NOT EDIT THIS BLOCK BELOW=== procedure_autogen starts ===
procedure 'Execute Custom Command', description: '''Allows to execute custom command that is not covered by other procedures''', {

    // Handling binary dependencies
    step 'flowpdk-setup', {
        description = "This step handles binary dependencies delivery"
        subprocedure = 'flowpdk-setup'
        actualParameter = [
            generateClasspathFromFolders: 'deps/libs'
        ]
    }

    step 'Execute Custom Command', {
        description = ''
        command = new File(pluginDir, "dsl/procedures/ExecuteCustomCommand/steps/ExecuteCustomCommand.groovy").text
        shell = 'ec-groovy'
        shell = 'ec-groovy -cp $[/myJob/flowpdk_classpath]'

        resourceName = '$[/myJobStep/parent/steps/flowpdk-setup/flowpdkResource]'

        postProcessor = '''$[/myProject/perl/postpLoader]'''
    }
// DO NOT EDIT THIS BLOCK ABOVE ^^^=== procedure_autogen ends, checksum: 44127041de483547baf3a68d42860668 ===
// Do not update the code above the line
// procedure properties declaration can be placed in here, like
// property 'property name', value: "value"
}