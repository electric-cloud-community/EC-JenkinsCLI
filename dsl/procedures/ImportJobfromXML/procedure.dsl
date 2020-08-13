// This procedure.dsl was generated automatically
// DO NOT EDIT THIS BLOCK BELOW=== procedure_autogen starts ===
procedure 'Import Job from XML', description: '''Creates new job on the Jenkins server''', {

    // Handling binary dependencies
    step 'flowpdk-setup', {
        description = "This step handles binary dependencies delivery"
        subprocedure = 'flowpdk-setup'
        actualParameter = [
            generateClasspathFromFolders: 'deps/libs'
        ]
    }

    step 'Import Job from XML', {
        description = ''
        command = new File(pluginDir, "dsl/procedures/ImportJobfromXML/steps/ImportJobfromXML.groovy").text
        shell = 'ec-groovy'
        shell = 'ec-groovy -cp $[/myJob/flowpdk_classpath]'

        resourceName = '$[/myJobStep/parent/steps/flowpdk-setup/flowpdkResource]'

        postProcessor = '''$[/myProject/perl/postpLoader]'''
    }
// DO NOT EDIT THIS BLOCK ABOVE ^^^=== procedure_autogen ends, checksum: f7baebec61b60441cfb52811ef77be79 ===
// Do not update the code above the line
// procedure properties declaration can be placed in here, like
// property 'property name', value: "value"
}