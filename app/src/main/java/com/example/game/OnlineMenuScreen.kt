import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun OnlineMenuScreen(
    nickname: String,
    onNicknameChange: (String) -> Unit,
    onHost: () -> Unit,
    onJoin: () -> Unit,
    onExit: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        OutlinedTextField(
            value = nickname,
            onValueChange = onNicknameChange,
            label = { Text("Ник") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                if (nickname.isNotBlank()) {
                    onHost()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Создать игру")
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                if (nickname.isNotBlank()) {
                    onJoin()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Подключиться")
        }

        Spacer(Modifier.height(32.dp))

        Button(
            onClick = onExit,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Назад")
        }
    }
}
