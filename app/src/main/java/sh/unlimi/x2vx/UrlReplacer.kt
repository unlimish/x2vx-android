package sh.unlimi.x2vx

import java.net.URI

class UrlReplacer {
    private val domainMappings = mapOf(
        "x.com" to "vxtwitter.com",
        "tiktok.com" to "tnktok.com",
        "bsky.app" to "boobsky.app",
        "reddit.com" to "rpddit.com"
    )

    fun supports(url: URI): Boolean {
        return url.host in domainMappings
    }

    fun replaceDomainName(url: URI): URI {
        if (!supports(url)) {
            return url
        }

        return URI(
            url.scheme,
            url.userInfo,
            domainMappings[url.host],
            url.port,
            url.path,
            null,
            null
        )
    }
}
