import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ConnectingScreen(
    ip: String,
    nickname: String,
    onConnected: (ClientGameManager) -> Unit,
    onCancel: () -> Unit
) {
    var status by remember { mutableStateOf("Подключение...") }

    LaunchedEffect(Unit) {
        val client = ClientGameManager()
        client.connect(ip, nickname) {
            onConnected(client)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(status, fontSize = 20.sp)
        Spacer(Modifier.height(24.dp))
        Button(onClick = onCancel, modifier = Modifier.fillMaxWidth()) {
            Text("Отмена")
        }
    }
}

