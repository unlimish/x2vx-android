package sh.unlimi.x2vx

import java.net.URI

class UrlReplacer {
    fun replaceDomainName(url: String): String {
        return this.replaceDomainName(URI(url)).toString()
    }

    fun replaceDomainName(url: URI): URI {
        if (url.host != "x.com") {
            return url
        }

        return URI(
            url.scheme,
            url.userInfo,
            "vxtwitter.com",
            url.port,
            url.path,
            // removes query and fragment
            null,
            null,
        )
    }
}
