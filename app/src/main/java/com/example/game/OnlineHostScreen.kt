import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.net.Inet4Address
import java.net.NetworkInterface

@Composable
fun OnlineHostScreen(
    nickname: String,
    onExit: () -> Unit,
    onStartGame: () -> Unit,
    onHostReady: (HostGameManager) -> Unit,
    setOnlineGameStarted: (Boolean) -> Unit
) {
    val hostIp = remember { getLocalIpAddress() }
    val hostManager = remember { HostGameManager() }
    var connected by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        onHostReady(hostManager)
        hostManager.start {
            connected = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text("Ваш IP:")
        Text(hostIp, fontSize = 20.sp)

        Spacer(Modifier.height(16.dp))

        if (!connected) {
            Text("Ожидание игрока...")
        } else {
            Text("Игрок подключился!")
            Button(
                onClick = {
                    hostManager.sendStartGame()
                    setOnlineGameStarted(true)
                    onStartGame() },
                modifier = Modifier.fillMaxWidth()

            ) {
                Text("Начать игру")
            }
        }

        Spacer(Modifier.height(32.dp))

        Button(
            onClick = {
                hostManager.close()
                onExit()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Отмена")
        }
    }
}

fun getLocalIpAddress(): String {
    val interfaces = NetworkInterface.getNetworkInterfaces()
    for (iface in interfaces) {
        for (addr in iface.inetAddresses) {
            if (!addr.isLoopbackAddress && addr is Inet4Address) {
                return addr.hostAddress ?: "0.0.0.0"
            }
        }
    }
    return "0.0.0.0"
}
