$[/myProject/groovy/scripts/preamble.groovy.ignore]

JenkinsCLI plugin = new JenkinsCLI()
plugin.runStep('Wait for Server', 'Wait for Server', 'waitForServer')