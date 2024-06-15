package sh.unlimi.x2vx

class UrlReplacer {
    fun replaceDomainName(url: String): String {
        var modifiedUrl = url
        if (url.startsWith("https://x.com")) {
            modifiedUrl = url.replaceFirst("https://x.com", "https://vxtwitter.com")
        }

        // Remove everything from "?" onwards
        val indexOfQuery = modifiedUrl.indexOf("?")
        if (indexOfQuery != -1) {
            modifiedUrl = modifiedUrl.substring(0, indexOfQuery)
        }
        return modifiedUrl
    }
}
