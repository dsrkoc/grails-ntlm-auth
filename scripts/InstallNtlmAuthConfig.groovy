includeTargets << grailsScript("Init")

target(main: "Installs NtlmAuth config in the /grails-app/conf/ directory") {
    def configFile = "${basedir}/grails-app/conf/NtlmAuthConfig.groovy"
    if(!(configFile as File).exists() || confirmInput("NtlmAuth config file already exists in your project. Overwrite it?")) {
        Ant.copy(
            file:"${ntlmAuthPluginDir}/grails-app/conf/SampleNtlmAuthConfig.groovy",
            tofile:configFile,
            overwrite: true
        )
        event("CreatedFile", [configFile])
        event("StatusFinal", ["NtlmAuth configuration file was installed into /grails-app/conf/NtlmAuthConfig.groovy\nPlease fill it with your own configuration data."])
    }
}

confirmInput = {String message ->
    Ant.input(message: message, addproperty: "confirm.message", validargs: "y,n")
    Ant.antProject.properties."confirm.message"
}

setDefaultTarget(main)
