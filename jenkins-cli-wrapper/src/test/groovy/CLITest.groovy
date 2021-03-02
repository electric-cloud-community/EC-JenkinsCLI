import com.electriccloud.cd.plugins.jenkinscliwrapper.JenkinsCLIWrapper
import spock.lang.Requires
import spock.lang.Specification

class CLITest extends Specification {

    static String endpoint = System.getenv("JENKINS_URL") ?: 'http://localhost:8080'
    static String username = System.getenv("JENKINS_USER") ?: 'admin'
    static String password = System.getenv("JENKINS_PASSWORD") ?: 'changeme'


    def "initialize"() {
        when:
        JenkinsCLIWrapper wrapper = new JenkinsCLIWrapper(endpoint, username, password)

        then:
        assert wrapper
    }

    @Requires({ System.getenv("JENKINS_URL") != null })
    def "server checks run without CLI jar"(){
        when:
        def jenkinsCliPath = '/dev/null'
        JenkinsCLIWrapper wrapper = new JenkinsCLIWrapper(endpoint, username, password, jenkinsCliPath)
        wrapper.pollUntilServerAvailable(15)

        then:
        assert wrapper.isServerRunning()
    }

    @Requires({ System.getenv("JENKINS_URL") != null })
    def "jar is downloaded"(){
        when:
        def jenkinsCliPath = '/tmp/jenkinsCLI.jar'
        File cli = new File(jenkinsCliPath)
        if (cli.exists()){
            cli.delete()
        }

        JenkinsCLIWrapper wrapper = new JenkinsCLIWrapper(endpoint, username, password, jenkinsCliPath)

        and:
        String version = wrapper.executeCommand(['version']).getStdOut()

        then:
        println("Received Jenkins version: ${version}")
        assert version

        and:
        assert (new File(jenkinsCliPath)).exists()
    }


}
