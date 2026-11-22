package id.spectre.bugs.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.spectre.bugs.ui.theme.Glass
import id.spectre.bugs.ui.theme.Gold
import id.spectre.bugs.ui.theme.GoldDim
import id.spectre.bugs.ui.theme.Ivory
import id.spectre.bugs.ui.theme.Obsidian
import id.spectre.bugs.wa.WaBridge
import id.spectre.bugs.wa.WaEvent

@Composable
fun PairingScreen(onConnected: () -> Unit) {
    var phone by remember { mutableStateOf("62") }
    var code by remember { mutableStateOf<String?>(null) }
    var status by remember { mutableStateOf("Belum terhubung") }

    LaunchedEffect(Unit) {
        WaBridge.events.collect { e ->
            when (e) {
                is WaEvent.Code -> {
                    code = e.code
                    status = "Masukkan kode di WhatsApp"
                }
                is WaEvent.Status -> {
                    status = e.state
                    if (e.state == "connected") onConnected()
                }
                is WaEvent.Log -> status = e.line
            }
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(Obsidian)
            .padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(Modifier.height(40.dp))
        Text("PAIRING", style = MaterialTheme.typography.titleLarge, color = Gold)
        Spacer(Modifier.height(6.dp))
        Text(
            "WhatsApp > Perangkat Tertaut > Tautkan dengan nomor telepon",
            style = MaterialTheme.typography.bodySmall,
            color = GoldDim,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(28.dp))

        OutlinedField("NOMOR (62xxxx)", phone) { phone = it }
        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                status = "Meminta kode..."
                WaBridge.requestCode(phone)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Gold,
                contentColor = Obsidian
            )
        ) { Text("MINTA KODE", style = MaterialTheme.typography.titleMedium) }

        Spacer(Modifier.height(28.dp))

        code?.let {
            Column(
                Modifier
                    .clip(RoundedCornerShape(18.dp))
                    .background(Glass)
                    .border(1.dp, Gold, RoundedCornerShape(18.dp))
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("PAIRING CODE", style = MaterialTheme.typography.labelSmall, color = GoldDim)
                Text(it, fontSize = 38.sp, color = Gold, letterSpacing = 8.sp)
            }
        }

        Spacer(Modifier.height(20.dp))
        Text(
            status,
            style = MaterialTheme.typography.bodySmall,
            color = Ivory.copy(alpha = .6f)
        )
    }
}
