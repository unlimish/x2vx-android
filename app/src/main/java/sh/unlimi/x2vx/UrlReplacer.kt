package sh.unlimi.x2vx

import java.net.URI

class UrlReplacer {
    fun supports(url: URI): Boolean {
        return url.host == "x.com"
    }

    fun replaceDomainName(url: URI): URI {
        if (!supports(url)) {
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
