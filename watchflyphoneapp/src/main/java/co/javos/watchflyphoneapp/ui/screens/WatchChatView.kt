package co.javos.watchflyphoneapp.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import co.javos.watchflyphoneapp.models.AuthorDevice
import co.javos.watchflyphoneapp.models.Message
import co.javos.watchflyphoneapp.ui.widgets.CommandMessageButtonWidget
import co.javos.watchflyphoneapp.ui.widgets.MessageWidget
import co.javos.watchflyphoneapp.viewmodels.WatchChatViewModel
import kotlinx.coroutines.launch

class WatchChatView {

    @OptIn(ExperimentalMaterial3Api::class)
    @Preview(device = "spec:width=1280dp,height=800dp,dpi=240", uiMode = 0)
    @Composable
    fun WatchChat(navController: NavController? = null, viewModel: WatchChatViewModel? = null) {
        val messages = viewModel?.messages ?: remember { mutableStateListOf<Message>() }

        Scaffold(containerColor = Color.Black, contentColor = Color.Black, topBar = {
            TopAppBar(title = { Text(text = "Watch Messages") }, navigationIcon = {
                IconButton(onClick = {
                    navController?.popBackStack()
                }) {
                    Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Back")
                }
            }, colors = TopAppBarDefaults.topAppBarColors().copy(
                containerColor = Color.Black,
                titleContentColor = Color.White,
                navigationIconContentColor = Color.White
            ), expandedHeight = 46.dp, modifier = Modifier.drawBehind {
                drawLine(
                    color = Color.White,
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = 1.dp.toPx()
                )
            }

            )
        }, bottomBar = {
            MessageInput(viewModel)
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

        val listState = rememberLazyListState()

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
            state = listState
        ) {
            items(messages.size) { index ->
                MessageWidget().Message(messages[index])
            }
        }

        LaunchedEffect(messages.size) {
            if (messages.isNotEmpty()) {
                listState.animateScrollToItem(messages.lastIndex)
            }
        }
    }

    @Composable
    fun MessageInput(viewModel: WatchChatViewModel?) {

        val testMessages = arrayOf(
            "Drone disconnected", "Drone connected", "Drone flying", "Error", "Warning"
        )

        Row(modifier = Modifier
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
            verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Send:", color = Color.White, fontWeight = FontWeight.Bold)
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(testMessages.size) { index ->
                    CommandMessageButtonWidget().CommandMessageButton(text = testMessages[index],
                        onClick = {
                            viewModel?.addMessage(
                                Message(
                                    content = testMessages[index],
                                    author = AuthorDevice.PHONE
                                )
                            )
                        })
                }
            }
        }
    }
}
