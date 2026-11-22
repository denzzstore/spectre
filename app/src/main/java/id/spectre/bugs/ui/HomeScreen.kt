package id.spectre.bugs.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.spectre.bugs.bug.BugEngine
import id.spectre.bugs.bug.Preset
import id.spectre.bugs.bug.Tier
import id.spectre.bugs.data.Role
import id.spectre.bugs.ui.theme.Glass
import id.spectre.bugs.ui.theme.Gold
import id.spectre.bugs.ui.theme.GoldDim
import id.spectre.bugs.ui.theme.Ivory
import id.spectre.bugs.ui.theme.Obsidian
import id.spectre.bugs.wa.WaBridge
import kotlinx.coroutines.launch

private fun tierColor(t: Tier): Color = when (t) {
    Tier.S -> Ivory
    Tier.SS -> Color(0xFFFFB340)
    Tier.SSS -> Color(0xFFFF6B35)
    Tier.VIP -> Gold
}

@Composable
fun HomeScreen(
    user: String,
    role: Role,
    onOwner: () -> Unit,
    onLogout: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val engine = remember { BugEngine(WaBridge) }
    var target by remember { mutableStateOf("") }
    var mention by remember { mutableStateOf("") }
    var delayMode by remember { mutableStateOf(true) }
    var selected by remember { mutableStateOf<Preset?>(null) }
    var busy by remember { mutableStateOf(false) }
    val logs = remember { mutableStateListOf<String>() }

    Column(
        Modifier
            .fillMaxSize()
            .background(Obsidian)
            .padding(16.dp)
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("SPECTRE", style = MaterialTheme.typography.titleLarge, color = Gold)
                Text(
                    "$user | ${role.name}",
                    style = MaterialTheme.typography.labelSmall,
                    color = GoldDim
                )
            }
            if (role == Role.OWNER) {
                TextButton(onClick = onOwner) { Text("AKUN", color = Gold) }
            }
            TextButton(onClick = onLogout) {
                Text("KELUAR", color = Ivory.copy(alpha = .5f))
            }
        }

        Spacer(Modifier.height(8.dp))
        OutlinedField("TARGET JID / NOMOR", target) { target = it }
        Spacer(Modifier.height(8.dp))
        OutlinedField("MENTION JID (pisah koma, opsional)", mention) { mention = it }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Switch(
                checked = delayMode,
                onCheckedChange = { delayMode = it },
                colors = SwitchDefaults.colors(checkedTrackColor = GoldDim)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                "Delay invisible (jitter)",
                style = MaterialTheme.typography.bodySmall,
                color = Ivory.copy(alpha = .6f)
            )
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(Preset.entries.toList()) { p ->
                val on = selected == p
                Column(
                    Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (on) Gold.copy(alpha = .14f) else Glass)
                        .border(
                            1.dp,
                            if (on) Gold else GoldDim.copy(alpha = .3f),
                            RoundedCornerShape(14.dp)
                        )
                        .clickable { selected = p }
                        .padding(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            p.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = Ivory,
                            modifier = Modifier.weight(1f)
                        )
                        Text(p.tier.label, fontSize = 9.sp, color = tierColor(p.tier))
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        p.desc,
                        style = MaterialTheme.typography.bodySmall,
                        color = Ivory.copy(alpha = .55f)
                    )
                }
            }
        }

        Button(
            onClick = {
                val p = selected ?: return@Button
                busy = true
                logs.clear()
                scope.launch {
                    val jids = mention.split(",").map { it.trim() }.filter { it.contains("@") }
                    engine.run(target.trim(), p, delayMode, jids) { logs.add(it) }
                    busy = false
                }
            },
            enabled = !busy && target.isNotBlank() && selected != null,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Gold,
                contentColor = Obsidian
            )
        ) {
            Text(
                if (busy) "MENEMBAK..." else "TEMBAK",
                style = MaterialTheme.typography.titleMedium
            )
        }

        Row(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf("Rename Flood", "Desc Flood", "Poll Bomb").forEachIndexed { i, label ->
                OutlinedButton(
                    onClick = {
                        logs.clear()
                        scope.launch {
                            when (i) {
                                0 -> engine.floodGroupName(target.trim(), 15) { logs.add(it) }
                                1 -> engine.floodGroupDesc(target.trim(), 15) { logs.add(it) }
                                else -> engine.pollBomb(target.trim(), 8) { logs.add(it) }
                            }
                        }
                    },
                    enabled = !busy && target.isNotBlank(),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) { Text(label, fontSize = 10.sp, color = Gold) }
            }
        }

        LazyColumn(
            Modifier
                .height(110.dp)
                .fillMaxWidth()
        ) {
            items(logs) {
                Text(
                    it,
                    style = MaterialTheme.typography.bodySmall,
                    color = Ivory.copy(alpha = .5f)
                )
            }
        }
    }
}
