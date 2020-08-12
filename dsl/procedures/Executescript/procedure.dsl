// This procedure.dsl was generated automatically
// DO NOT EDIT THIS BLOCK BELOW=== procedure_autogen starts ===
procedure 'Execute Script', description: '''Executes groovy script on server''', {

    // Handling binary dependencies
    step 'flowpdk-setup', {
        description = "This step handles binary dependencies delivery"
        subprocedure = 'flowpdk-setup'
        actualParameter = [
            generateClasspathFromFolders: 'deps/libs'
        ]
    }

    step 'Execute Script', {
        description = ''
        command = new File(pluginDir, "dsl/procedures/ExecuteScript/steps/ExecuteScript.groovy").text
        shell = 'ec-groovy'
        shell = 'ec-groovy -cp $[/myJob/flowpdk_classpath]'

        resourceName = '$[/myJobStep/parent/steps/flowpdk-setup/flowpdkResource]'

        postProcessor = '''$[/myProject/perl/postpLoader]'''
    }
// DO NOT EDIT THIS BLOCK ABOVE ^^^=== procedure_autogen ends, checksum: c8c794909bbf189d78c1efd8b545a382 ===
// Do not update the code above the line
// procedure properties declaration can be placed in here, like
// property 'property name', value: "value"
}