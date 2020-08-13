import com.electriccloud.cd.plugins.jenkinscliwrapper.JenkinsCLIWrapper
import spock.lang.Specification

class CLITest extends Specification {

    static String endpoint = 'http://docker-host:8080'
    static String username = 'admin'
    static String password = 'changeme'


    def "initialize"(){
        JenkinsCLIWrapper wrapper = new JenkinsCLIWrapper(

        )
    }
}
