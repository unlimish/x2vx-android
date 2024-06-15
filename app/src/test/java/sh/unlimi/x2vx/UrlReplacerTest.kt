package sh.unlimi.x2vx

import org.junit.Test

import org.junit.Assert.*

class UrlReplacerTest {
    @Test
    fun testReplaceDomainName() {
        val replacer = UrlReplacer()

        // general case
        assertEquals(
            "https://vxtwitter.com/unlimish/status/1773686985728962615",
            replacer.replaceDomainName("https://x.com/unlimish/status/1773686985728962615"),
        )

        // removes query string if found
        assertEquals(
            "https://vxtwitter.com/unlimish/status/1773686985728962615",
            replacer.replaceDomainName("https://x.com/unlimish/status/1773686985728962615?s=46"),
        )

        // doesn't replace twitter[.]com domain name
        assertEquals(
            "https://twitter.com/unlimish/status/1773686985728962615",
            replacer.replaceDomainName("https://twitter.com/unlimish/status/1773686985728962615"),
        )

        // ignores domain names starts with x[.]com but it's not x[.]com
        assertEquals(
            "https://x.com.example.com/unlimish/status/1773686985728962615",
            replacer.replaceDomainName("https://x.com.example.com/unlimish/status/1773686985728962615"),
        )
    }
}
