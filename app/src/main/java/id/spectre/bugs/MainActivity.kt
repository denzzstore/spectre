package id.spectre.bugs

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import id.spectre.bugs.data.AuthRepo
import id.spectre.bugs.data.Role
import id.spectre.bugs.ui.HomeScreen
import id.spectre.bugs.ui.LoginScreen
import id.spectre.bugs.ui.OwnerScreen
import id.spectre.bugs.ui.PairingScreen
import id.spectre.bugs.ui.theme.SpectreTheme
import id.spectre.bugs.wa.WaBridge

enum class Route { LOGIN, PAIRING, HOME, OWNER }

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val auth = AuthRepo(this)
        WaBridge.start(this)

        setContent {
            SpectreTheme {
                var route by remember { mutableStateOf(Route.LOGIN) }
                var user by remember { mutableStateOf("") }
                var role by remember { mutableStateOf(Role.USER) }

                when (route) {
                    Route.LOGIN -> LoginScreen(auth) { u, r ->
                        user = u
                        role = r
                        route = Route.PAIRING
                    }

                    Route.PAIRING -> PairingScreen(onConnected = { route = Route.HOME })

                    Route.HOME -> HomeScreen(
                        user = user,
                        role = role,
                        onOwner = { route = Route.OWNER },
                        onLogout = {
                            user = ""
                            role = Role.USER
                            route = Route.LOGIN
                        }
                    )

                    Route.OWNER -> OwnerScreen(
                        auth = auth,
                        owner = user,
                        onBack = { route = Route.HOME }
                    )
                }
            }
        }
    }
}
