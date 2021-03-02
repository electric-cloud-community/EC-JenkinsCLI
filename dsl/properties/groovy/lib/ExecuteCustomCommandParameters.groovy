
// DO NOT EDIT THIS BLOCK BELOW=== Parameters starts ===
// PLEASE DO NOT EDIT THIS FILE

import com.cloudbees.flowpdf.StepParameters

class ExecuteCustomCommandParameters {
    /**
    * Label: Command, type: entry
    */
    String command
    /**
    * Label: Arguments, type: entry
    */
    String arguments
    /**
    * Label: Uses STDIN, type: checkbox
    */
    boolean usesStdin
    /**
    * Label: STDIN Source Filepath, type: entry
    */
    String inputPath
    /**
    * Label: STDIN Source Content, type: textarea
    */
    String inputText
    /**
    * Label: Result filepath, type: entry
    */
    String filePath
    /**
    * Label: saveToProperty, type: entry
    */
    String resultProperty

    static ExecuteCustomCommandParameters initParameters(StepParameters sp) {
        ExecuteCustomCommandParameters parameters = new ExecuteCustomCommandParameters()

        def command = sp.getRequiredParameter('command').value
        parameters.command = command
        def arguments = sp.getParameter('arguments').value
        parameters.arguments = arguments
        def usesStdin = sp.getParameter('usesStdin').value == "true"
        parameters.usesStdin = usesStdin
        def inputPath = sp.getParameter('inputPath').value
        parameters.inputPath = inputPath
        def inputText = sp.getParameter('inputText').value
        parameters.inputText = inputText
        def filePath = sp.getParameter('filePath').value
        parameters.filePath = filePath
        def resultProperty = sp.getParameter('resultProperty').value
        parameters.resultProperty = resultProperty

        return parameters
    }
}
// DO NOT EDIT THIS BLOCK ABOVE ^^^=== Parameters ends, checksum: 52881d2625758eca5769a023c6722049 ===
