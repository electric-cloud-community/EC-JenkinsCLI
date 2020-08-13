// This procedure.dsl was generated automatically
// DO NOT EDIT THIS BLOCK BELOW=== procedure_autogen starts ===
procedure 'Export Job to XML', description: '''Exports existing job to an XML file''', {

    // Handling binary dependencies
    step 'flowpdk-setup', {
        description = "This step handles binary dependencies delivery"
        subprocedure = 'flowpdk-setup'
        actualParameter = [
            generateClasspathFromFolders: 'deps/libs'
        ]
    }

    step 'Export Job to XML', {
        description = ''
        command = new File(pluginDir, "dsl/procedures/ExportJobtoXML/steps/ExportJobtoXML.groovy").text
        shell = 'ec-groovy'
        shell = 'ec-groovy -cp $[/myJob/flowpdk_classpath]'

        resourceName = '$[/myJobStep/parent/steps/flowpdk-setup/flowpdkResource]'

        postProcessor = '''$[/myProject/perl/postpLoader]'''
    }
// DO NOT EDIT THIS BLOCK ABOVE ^^^=== procedure_autogen ends, checksum: b5a36d862eb27f33e1f38d1bf6b727f0 ===
// Do not update the code above the line
// procedure properties declaration can be placed in here, like
// property 'property name', value: "value"
}