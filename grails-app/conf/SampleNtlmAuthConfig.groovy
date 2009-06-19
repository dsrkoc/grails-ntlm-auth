/*
 * jCIFS configuration setting for NTLMv1 HTTP authentication filter.
 * Enter data that fits your network.
 *
 * http.domainController:
 *     The IP address of any SMB server that should be used to authenticate HTTP clients
 * smb.client.domain:
 *     The NT domain against which clients should be authenticated
 *
 * Plugin accepts all jCIFS configuration properties.
 * See http://jcifs.samba.org/src/docs/ntlmhttpauth.html for more info
 */
jcifs {
	http.domainController = 'DC ADDRESS' // ... or use jcifs.netbios.wins
	smb.client.domain = 'A DOMAIN'
	smb.client.username = 'USERNAME'
	smb.client.password = 'PASSWORD'
}

/*
 * You can also use per-environment configuration feature.
 * (http://grails.org/doc/1.1.x/guide/3.%20Configuration.html)
 * 
environments {
	development {
		jcifs {
			http.domainController = 'DEV DC ADDRESS'
			smb.client.domain = 'DEV DOMAIN'
    		smb.client.username = 'USERNAME'
			smb.client.password = 'PASSWORD'
		}
	}

	production {
		jcifs {
			http.domainController = 'PROD DC ADDRESS1,DS ADDRESS2'
			smb.client.domain = 'PROD DOMAIN'
			smb.client.username = 'USERNAME'
			smb.client.password = 'PASSWORD'
		}
	}
}
*/
