package id.spectre.bugs.wa

import android.content.Context
import com.janeasystems.rn_nodejs_mobile.NodeJS
import kotlinx.coroutines.flow.MutableSharedFlow
import org.json.JSONObject

sealed class WaEvent {
    data class Code(val code: String) : WaEvent()
    data class Status(val state: String) : WaEvent()
    data class Log(val line: String) : WaEvent()
}

object WaBridge {

    val events = MutableSharedFlow<WaEvent>(extraBufferCapacity = 64)
    private var ready = false

    fun start(ctx: Context) {
        if (ready) return
        NodeJS.start("main.js")
        NodeJS.channel.setListener { raw ->
            val o = runCatching { JSONObject(raw) }.getOrNull() ?: return@setListener
            when (o.optString("type")) {
                "code" -> events.tryEmit(WaEvent.Code(o.optString("code")))
                "status" -> events.tryEmit(WaEvent.Status(o.optString("state")))
                "log" -> events.tryEmit(WaEvent.Log(o.optString("line")))
            }
        }
        ready = true
    }

    fun requestCode(phone: String) = NodeJS.channel.post("pair", phone)

    fun send(jid: String, text: String, mentions: List<String> = emptyList()): Boolean {
        val o = JSONObject().put("jid", jid).put("text", text)
        if (mentions.isNotEmpty()) o.put("mentions", mentions.joinToString(","))
        NodeJS.channel.post("send", o.toString())
        return true
    }

    fun groupUpdate(jid: String, field: String, value: String) {
        NodeJS.channel.post(
            "group",
            JSONObject().put("jid", jid).put("field", field).put("value", value).toString()
        )
    }

    fun poll(jid: String, name: String, options: List<String>) {
        NodeJS.channel.post(
            "poll",
            JSONObject().put("jid", jid).put("name", name)
                .put("options", options.joinToString("|")).toString()
        )
    }
}
