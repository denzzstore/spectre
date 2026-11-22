package id.spectre.bugs.data

import android.content.Context
import android.content.SharedPreferences
import java.security.MessageDigest

enum class Role { OWNER, ADMIN, USER }

class AuthRepo(ctx: Context) {

    private val sp: SharedPreferences =
        ctx.getSharedPreferences("spectre_auth", Context.MODE_PRIVATE)

    init {
        if (!sp.getBoolean("seeded", false)) {
            sp.edit()
                .putString("u_rico", hash("rico", "rico123"))
                .putString("r_rico", Role.OWNER.name)
                .putBoolean("seeded", true)
                .apply()
        }
    }

    private fun hash(u: String, p: String): String =
        MessageDigest.getInstance("SHA-256")
            .digest("$u::$p::spectre".toByteArray())
            .joinToString("") { "%02x".format(it) }

    fun login(u: String, p: String): Role? {
        val key = u.trim().lowercase()
        val stored = sp.getString("u_$key", null) ?: return null
        if (stored != hash(key, p)) return null
        return runCatching { Role.valueOf(sp.getString("r_$key", "USER")!!) }
            .getOrDefault(Role.USER)
    }

    fun create(u: String, p: String, role: Role): String? {
        val key = u.trim().lowercase()
        if (key.length < 3) return "Username minimal 3 karakter"
        if (p.length < 5) return "Password minimal 5 karakter"
        if (sp.contains("u_$key")) return "Username sudah dipakai"
        sp.edit()
            .putString("u_$key", hash(key, p))
            .putString("r_$key", role.name)
            .apply()
        return null
    }

    fun delete(u: String) {
        val key = u.trim().lowercase()
        if (key == "rico") return
        sp.edit().remove("u_$key").remove("r_$key").apply()
    }

    fun list(): List<Pair<String, Role>> =
        sp.all.keys
            .filter { it.startsWith("u_") }
            .map {
                val u = it.removePrefix("u_")
                u to runCatching { Role.valueOf(sp.getString("r_$u", "USER")!!) }
                    .getOrDefault(Role.USER)
            }
            .sortedBy { it.first }
}
