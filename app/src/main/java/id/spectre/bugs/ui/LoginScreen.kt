package id.spectre.bugs.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import id.spectre.bugs.data.AuthRepo
import id.spectre.bugs.data.Role
import id.spectre.bugs.ui.theme.Blood
import id.spectre.bugs.ui.theme.Glass
import id.spectre.bugs.ui.theme.Gold
import id.spectre.bugs.ui.theme.GoldDim
import id.spectre.bugs.ui.theme.Ivory
import id.spectre.bugs.ui.theme.Obsidian
import id.spectre.bugs.ui.theme.Onyx

@Composable
fun LoginScreen(auth: AuthRepo, onLogin: (String, Role) -> Unit) {
    var u by remember { mutableStateOf("") }
    var p by remember { mutableStateOf("") }
    var err by remember { mutableStateOf<String?>(null) }

    Box(
        Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Obsidian, Onyx, Obsidian))),
        contentAlignment = Alignment.Center
    ) {
        Column(
            Modifier
                .padding(28.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("SPECTRE", style = MaterialTheme.typography.displayLarge, color = Gold)
            Text(
                "BUG ENGINE - PRIVATE BUILD",
                style = MaterialTheme.typography.labelSmall,
                color = GoldDim
            )
            Spacer(Modifier.height(44.dp))

            Column(
                Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Glass)
                    .border(1.dp, GoldDim.copy(alpha = .45f), RoundedCornerShape(20.dp))
                    .padding(20.dp)
            ) {
                OutlinedField("USERNAME", u) { u = it }
                Spacer(Modifier.height(12.dp))
                OutlinedField("PASSWORD", p, true) { p = it }
                err?.let {
                    Spacer(Modifier.height(10.dp))
                    Text(it, color = Blood, style = MaterialTheme.typography.bodySmall)
                }
                Spacer(Modifier.height(20.dp))
                Button(
                    onClick = {
                        val r = auth.login(u, p)
                        if (r == null) err = "Kredensial salah"
                        else {
                            err = null
                            onLogin(u.trim().lowercase(), r)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Gold,
                        contentColor = Obsidian
                    )
                ) { Text("MASUK", style = MaterialTheme.typography.titleMedium) }
            }
        }
    }
}

@Composable
fun OutlinedField(
    label: String,
    value: String,
    secret: Boolean = false,
    onChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label, style = MaterialTheme.typography.labelSmall) },
        singleLine = true,
        visualTransformation =
            if (secret) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(
            keyboardType = if (secret) KeyboardType.Password else KeyboardType.Text
        ),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Gold,
            unfocusedBorderColor = GoldDim.copy(alpha = .4f),
            focusedTextColor = Ivory,
            unfocusedTextColor = Ivory,
            cursorColor = Gold,
            focusedLabelColor = Gold,
            unfocusedLabelColor = GoldDim
        )
    )
}
