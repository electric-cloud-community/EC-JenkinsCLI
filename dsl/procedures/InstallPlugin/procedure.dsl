// This procedure.dsl was generated automatically
// DO NOT EDIT THIS BLOCK BELOW=== procedure_autogen starts ===
procedure 'Install Plugin', description: '''Installs a plugin (should be available in Updates Center)''', {

    // Handling binary dependencies
    step 'flowpdk-setup', {
        description = "This step handles binary dependencies delivery"
        subprocedure = 'flowpdk-setup'
        actualParameter = [
            generateClasspathFromFolders: 'deps/libs'
        ]
    }

    step 'Install Plugin', {
        description = ''
        command = new File(pluginDir, "dsl/procedures/InstallPlugin/steps/InstallPlugin.groovy").text
        shell = 'ec-groovy'
        shell = 'ec-groovy -cp $[/myJob/flowpdk_classpath]'

        resourceName = '$[/myJobStep/parent/steps/flowpdk-setup/flowpdkResource]'

        postProcessor = '''$[/myProject/perl/postpLoader]'''
    }
// DO NOT EDIT THIS BLOCK ABOVE ^^^=== procedure_autogen ends, checksum: 68a52845d819bf357175f6b2808ddec8 ===
// Do not update the code above the line
// procedure properties declaration can be placed in here, like
// property 'property name', value: "value"
}