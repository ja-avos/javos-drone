package co.javos.watchflyphoneapp

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date

enum class AuthorDevice {
    PHONE, WATCH
}

class Message(val text: String, val author: AuthorDevice) {

}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(device = Devices.TABLET, uiMode = 0)
@Composable
fun ChatScreen(navController: NavController? = null) {
    val messages = remember { mutableStateListOf<Message>() }
    val newMessage = remember { mutableStateOf("") }

    messages += Message("Hello, Watch!", AuthorDevice.PHONE)
    messages += Message("Hi, Phone!", AuthorDevice.WATCH)

    Scaffold(
        containerColor = Color.Black,
        contentColor = Color.Black,
        topBar = {
            TopAppBar(
                title = { Text(text = "Watch Messages") },
                navigationIcon = {
                    IconButton(onClick = {
                        navController?.popBackStack()
                    }) {
                        Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors().copy(
                    containerColor = Color.Black,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                ),
                expandedHeight = 46.dp,
                modifier = Modifier.drawBehind {
                    drawLine(
                        color = Color.White,
                        start = Offset(0f, size.height),
                        end = Offset(size.width, size.height),
                        strokeWidth = 1.dp.toPx()
                    )
                }

            )
        }, bottomBar = {
            MessageInput()
        }, content = {
            Column(
                modifier = Modifier
                    .displayCutoutPadding()
                    .fillMaxSize()
                    .padding(it)
            ) {
                MessageList(messages)
            }
        })
}

@Composable
fun MessageList(messages: List<Message>) {

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        items(messages.size * 10) { index ->
            MessageItem(messages[index % messages.size])
        }
    }
}

@Composable
fun MessageItem(message: Message) {

    val textStyle = TextStyle(
        color = Color.Black,
        fontSize = 12.sp
    )

    val backgroundColor =
        if (message.author == AuthorDevice.PHONE) Color(0xFF77DDFF) else Color(0xFFBAB9FF)

    val modifier = Modifier
        .clip(RoundedCornerShape(8.dp))
        .background(backgroundColor)
        .padding(8.dp)



    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = if (message.author == AuthorDevice.PHONE) Arrangement.Start
        else Arrangement.End,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp),
            horizontalAlignment = if (message.author == AuthorDevice.PHONE) Alignment.Start
            else Alignment.End
        ) {
            if (message.author == AuthorDevice.PHONE) {
                Box(
                    modifier = modifier
                ) {
                    Text(text = message.text, style = textStyle)
                }
            } else {
                Box(
                    modifier = modifier
                ) {
                    Text(text = message.text, style = textStyle)
                }
            }
            Text(
                text = Date(System.currentTimeMillis()).toString()
                , style = TextStyle(
                    color = Color.LightGray,
                    fontSize = 8.sp
                )
            )
        }

    }
}

@Composable
fun MessageInput() {

    val testMessages = arrayOf(
        "Drone disconnected",
        "Drone connected",
        "Drone flying",
        "Error",
        "Warning"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                drawLine(
                    color = Color.White,
                    start = Offset(0f, 0F),
                    end = Offset(size.width, 0F),
                    strokeWidth = 1.dp.toPx()
                )
            }
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "Send:", color = Color.White, fontWeight = FontWeight.Bold)
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(testMessages.size) { index ->
                TestMessageButton(
                    text = testMessages[index]
                )
            }
        }
    }
}

@Composable
fun TestMessageButton(text: String) {
    Button(onClick = { /*TODO*/ }) {
        Text(text = text)
    }
}
