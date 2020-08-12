$[/myProject/groovy/scripts/preamble.groovy.ignore]

JenkinsCLIWrapper plugin = new JenkinsCLIWrapper()
plugin.runStep('Restart Jenkins', 'Restart Jenkins', 'restartJenkins')