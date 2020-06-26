
// DO NOT EDIT THIS BLOCK === Parameters starts ===
// PLEASE DO NOT EDIT THIS FILE

import com.cloudbees.flowpdf.StepParameters

class RestartJenkinsParameters {
    /**
    * Label: Perform Safe Restart?, type: checkbox
    */
    boolean safe
    /**
    * Label: Wait For Server?, type: checkbox
    */
    boolean waitForServer

    static RestartJenkinsParameters initParameters(StepParameters sp) {
        RestartJenkinsParameters parameters = new RestartJenkinsParameters()

        def safe = sp.getParameter('safe').value == "true"
        parameters.safe = safe
        def waitForServer = sp.getParameter('waitForServer').value == "true"
        parameters.waitForServer = waitForServer

        return parameters
    }
}
// DO NOT EDIT THIS BLOCK === Parameters ends, checksum: 95a9bbf0e3744c805e2371688275261a ===
