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

    @Test
    fun testProviders() {
        val url = URI("https://x.com/unlimish/status/1773686985728962615?s=46")
        assertEquals(
            URI("https://fixvx.com/unlimish/status/1773686985728962615"),
            UrlReplacer(Provider.FIXVX).replaceDomainName(url),
        )
        assertEquals(
            URI("https://fxtwitter.com/unlimish/status/1773686985728962615"),
            UrlReplacer(Provider.FXTWITTER).replaceDomainName(url),
        )
        assertEquals(
            URI("https://fixupx.com/unlimish/status/1773686985728962615"),
            UrlReplacer(Provider.FIXUPX).replaceDomainName(url),
        )
    }

    @Test
    fun testTranslation() {
        val status = URI("https://x.com/unlimish/status/1773686985728962615?s=46")

        // appends the language code for providers supporting translation
        assertEquals(
            URI("https://fixupx.com/unlimish/status/1773686985728962615/ja"),
            UrlReplacer(Provider.FIXUPX, "ja").replaceDomainName(status),
        )
        assertEquals(
            URI("https://fxtwitter.com/unlimish/status/1773686985728962615/en"),
            UrlReplacer(Provider.FXTWITTER, "en").replaceDomainName(URI("https://x.com/unlimish/status/1773686985728962615/")),
        )

        // ignored for providers without translation support
        assertEquals(
            URI("https://vxtwitter.com/unlimish/status/1773686985728962615"),
            UrlReplacer(Provider.VXTWITTER, "ja").replaceDomainName(status),
        )

        // ignored for non-status URLs
        assertEquals(
            URI("https://fixupx.com/unlimish"),
            UrlReplacer(Provider.FIXUPX, "ja").replaceDomainName(URI("https://x.com/unlimish")),
        )

        // ignored for empty or invalid language codes
        assertEquals(
            URI("https://fixupx.com/unlimish/status/1773686985728962615"),
            UrlReplacer(Provider.FIXUPX, "").replaceDomainName(status),
        )
        assertEquals(
            URI("https://fixupx.com/unlimish/status/1773686985728962615"),
            UrlReplacer(Provider.FIXUPX, "../x").replaceDomainName(status),
        )
    }
}
