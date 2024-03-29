/*
 * Copyright 2009 Dinko Srkoc, Helix d.o.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import grails.util.Environment

/**
 * Plugin that provides NTLM authentication by using jCIFS NTLM HTTP authentication
 * via jCIFS NtlmHttpFilter. Note that it only works for NTLMv1.
 *
 * @author Dinko Srkoc
 */
class NtlmAuthGrailsPlugin {
    // the plugin version
    def version = "0.6"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "1.1 > *"
    // the other plugins this plugin depends on
    def dependsOn = [:]
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
            "grails-app/views/error.gsp"
    ]

    def author = "Dinko Srkoč"
    def authorEmail = "dinko.srkoc@helix.hr"
    def title = "NTLM HTTP Authentication"
    def description = '''\\
Simple NTLMv1 authentication using jCIFS library/filter.
See http://jcifs.samba.org for more info.
'''

    // URL to the plugin's documentation
    def documentation = "http://grails.org/NtlmAuth+Plugin"

    // This helps with placing this plugin right after char encoding filter
    // in filter-mapping part of web.xml
    def loadBefore = [ 'controllers' ]

    def doWithSpring = { }

    def doWithApplicationContext = { applicationContext -> }

    def doWithWebDescriptor = { xml ->
        def cfg = readConfig(application) // jCIFS NTLM configuration parameters
        if (!cfg || cfg.active == false)  // no configuration or not active -> no authentication filter
            return
            
        // insert filter after last context-param
        // if we do xml.'filter' + { ... }, for some reason we end up with two entries in web.xml
        def ctxParam = xml.'context-param'
        ctxParam[ctxParam.size() - 1] + {
            'filter' {
                'filter-name'('NtlmHttpFilter')
                'filter-class'('jcifs.http.NtlmHttpFilter')
                cfg.flatten().each { key, val ->
                    if (key == 'active') // this does not belong here
                        return
                    'init-param' {
                        'param-name'(key)
                        'param-value'(val)
                    }
                }
            }
        }
        
        // The filter needs to go after the Spring char encoding filter (if present).
        // Some of the logic here is inspired by the Shiro plugin ...
        def filter = xml.'filter-mapping'.find { it.'filter-name'.text() == "charEncodingFilter" }

        if (!filter) {
            def fm = xml.'filter-mapping'
            if (fm.size() == 0) {
                /* If there is no char encoding filter we need to use
                 * the last filter definition as the insertion point. */
                def filters = xml.'filter'
                filter = filters[filters.size() - 1]
            }
            else {
                filter = fm[fm.size() - 1]
            }
        }

        filter + {
            'filter-mapping'{
                'filter-name'('NtlmHttpFilter')
                'url-pattern'("/*")
            }
        }
    }

    def doWithDynamicMethods = { ctx -> }

    def onChange = { event -> }

    def onConfigChange = { event -> }

    // slurps jCIFS configuration, reads ntlmAuth from Config
    // to override the slurped config,
    // returns false if file doesn't exist
    private readConfig(app) {
        GroovyClassLoader classLoader = new GroovyClassLoader(getClass().getClassLoader())
 
        def ntlmCfg = app.config.ntlmAuth ?: [:]       
        def slurper = new ConfigSlurper(Environment.current.name)
        try {
            slurper.parse(classLoader.loadClass('NtlmAuthConfig')) + ntlmCfg
        }
        catch (e) {
            if (ntlmCfg) {
                return ntlmCfg
            }
            else {
                println "--> Unable to load NtlmAuthConfig, jCIFS NTLM disabled."
                println "--> Maybe you haven't run 'grails install-ntlm-auth-config'."
                return false
            }
        }
    }
}
