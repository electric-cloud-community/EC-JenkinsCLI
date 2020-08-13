
// DO NOT EDIT THIS BLOCK BELOW=== Parameters starts ===
// PLEASE DO NOT EDIT THIS FILE

import com.cloudbees.flowpdf.StepParameters

class ImportJobFromXMLParameters {
    /**
    * Label: Job Name, type: entry
    */
    String jenkinsJobName
    /**
    * Label: File Path, type: entry
    */
    String xmlFilePath
    /**
    * Label: File Content, type: textarea
    */
    String xmlFileScriptContent

    static ImportJobFromXMLParameters initParameters(StepParameters sp) {
        ImportJobFromXMLParameters parameters = new ImportJobFromXMLParameters()

        def jenkinsJobName = sp.getRequiredParameter('jenkinsJobName').value
        parameters.jenkinsJobName = jenkinsJobName
        def xmlFilePath = sp.getParameter('xmlFilePath').value
        parameters.xmlFilePath = xmlFilePath
        def xmlFileScriptContent = sp.getParameter('xmlFileScriptContent').value
        parameters.xmlFileScriptContent = xmlFileScriptContent

        return parameters
    }
}
// DO NOT EDIT THIS BLOCK ABOVE ^^^=== Parameters ends, checksum: 4d6b76b48685feee09aad7c2b4df0e38 ===