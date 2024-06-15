package sh.unlimi.x2vx

import org.junit.Test

import org.junit.Assert.*
import java.net.URI

class UrlReplacerTest {
    @Test
    fun testSupports() {
        val replacer = UrlReplacer()

        assertTrue(replacer.supports(URI("https://x.com/unlimish/status/1773686985728962615?s=46")))
        assertTrue(replacer.supports(URI("https://x.com/unlimish/status/1773686985728962615")))
        assertTrue(replacer.supports(URI("https://x.com/unlimish")))
        assertTrue(replacer.supports(URI("https://x.com/")))

        assertFalse(replacer.supports(URI("https://twitter.com/unlimish/status/1773686985728962615")))
        assertFalse(replacer.supports(URI("https://vxtwitter.com/unlimish/status/1773686985728962615")))
        assertFalse(replacer.supports(URI("https://x.com.example.com/unlimish/status/1773686985728962615")))
    }

    @Test
    fun testReplaceDomainName() {
        val replacer = UrlReplacer()

        // general case
        assertEquals(
            URI("https://vxtwitter.com/unlimish/status/1773686985728962615"),
            replacer.replaceDomainName(URI("https://x.com/unlimish/status/1773686985728962615")),
        )

        // removes query string if found
        assertEquals(
            URI("https://vxtwitter.com/unlimish/status/1773686985728962615"),
            replacer.replaceDomainName(URI("https://x.com/unlimish/status/1773686985728962615?s=46")),
        )

        // doesn't replace twitter[.]com domain name
        assertEquals(
            URI("https://twitter.com/unlimish/status/1773686985728962615"),
            replacer.replaceDomainName(URI("https://twitter.com/unlimish/status/1773686985728962615")),
        )

        // ignores domain names starts with x[.]com but it's not x[.]com
        assertEquals(
            URI("https://x.com.example.com/unlimish/status/1773686985728962615"),
            replacer.replaceDomainName(URI("https://x.com.example.com/unlimish/status/1773686985728962615")),
        )
    }
}
