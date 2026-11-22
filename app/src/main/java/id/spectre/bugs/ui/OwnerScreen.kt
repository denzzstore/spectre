package id.spectre.bugs.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.spectre.bugs.data.AuthRepo
import id.spectre.bugs.data.Role
import id.spectre.bugs.ui.theme.Blood
import id.spectre.bugs.ui.theme.Gold
import id.spectre.bugs.ui.theme.GoldDim
import id.spectre.bugs.ui.theme.Ivory
import id.spectre.bugs.ui.theme.Obsidian

@Composable
fun OwnerScreen(auth: AuthRepo, owner: String, onBack: () -> Unit) {
    var u by remember { mutableStateOf("") }
    var p by remember { mutableStateOf("") }
    var role by remember { mutableStateOf(Role.USER) }
    var msg by remember { mutableStateOf<String?>(null) }
    var accounts by remember { mutableStateOf(auth.list()) }

    Column(
        Modifier
            .fillMaxSize()
            .background(Obsidian)
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "OWNER PANEL",
                style = MaterialTheme.typography.titleLarge,
                color = Gold,
                modifier = Modifier.weight(1f)
            )
            TextButton(onClick = onBack) { Text("TUTUP", color = Ivory.copy(alpha = .5f)) }
        }
        Text(
            "Login sebagai $owner",
            style = MaterialTheme.typography.labelSmall,
            color = GoldDim
        )
        Spacer(Modifier.height(16.dp))

        OutlinedField("USERNAME BARU", u) { u = it }
        Spacer(Modifier.height(8.dp))
        OutlinedField("PASSWORD", p, true) { p = it }
        Spacer(Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Role.entries.forEach { r ->
                FilterChip(
                    selected = role == r,
                    onClick = { role = r },
                    label = { Text(r.name, fontSize = 10.sp) }
                )
            }
        }
        Spacer(Modifier.height(10.dp))

        Button(
            onClick = {
                val err = auth.create(u, p, role)
                msg = err ?: "Akun $u dibuat"
                if (err == null) {
                    u = ""
                    p = ""
                    role = Role.USER
                    accounts = auth.list()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Gold,
                contentColor = Obsidian
            )
        ) { Text("BUAT AKUN", style = MaterialTheme.typography.titleMedium) }

        msg?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, style = MaterialTheme.typography.bodySmall, color = Gold)
        }

        Spacer(Modifier.height(16.dp))

        LazyColumn {
            items(accounts) { entry ->
                val name = entry.first
                val r = entry.second
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Ivory,
                        modifier = Modifier.weight(1f)
                    )
                    Text(r.name, style = MaterialTheme.typography.labelSmall, color = GoldDim)
                    if (name != "rico") {
                        TextButton(onClick = {
                            auth.delete(name)
                            accounts = auth.list()
                        }) { Text("HAPUS", fontSize = 10.sp, color = Blood) }
                    }
                }
            }
        }
    }
}
