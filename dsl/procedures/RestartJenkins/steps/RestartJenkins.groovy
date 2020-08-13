$[/myProject/groovy/scripts/preamble.groovy.ignore]

JenkinsCLI plugin = new JenkinsCLI()
plugin.runStep('Restart Jenkins', 'Restart Jenkins', 'restartJenkins')