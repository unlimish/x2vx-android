package sh.unlimi.x2vx

/**
 * Embed-fixing proxy services that x.com links can be rewritten to.
 *
 * [supportsTranslation] is true for FxEmbed-based services, which translate a post
 * when a language code is appended to the status URL (e.g. `/status/123/ja`).
 */
enum class Provider(
    val host: String,
    val supportsTranslation: Boolean,
) {
    VXTWITTER("vxtwitter.com", false),
    FIXVX("fixvx.com", false),
    FXTWITTER("fxtwitter.com", true),
    FIXUPX("fixupx.com", true);

    companion object {
        val DEFAULT = VXTWITTER

        fun fromName(name: String?): Provider =
            entries.firstOrNull { it.name == name } ?: DEFAULT
    }
}
