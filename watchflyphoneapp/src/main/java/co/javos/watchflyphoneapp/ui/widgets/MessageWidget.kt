package co.javos.watchflyphoneapp.ui.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.javos.watchflyphoneapp.models.AuthorDevice
import co.javos.watchflyphoneapp.models.Message
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MessageWidget {
    @Composable
    fun Message(message: Message) {

        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())

        val textStyle = TextStyle(
            color = Color.Black, fontSize = 12.sp
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
            horizontalArrangement = if (message.author == AuthorDevice.WATCH) Arrangement.Start
            else Arrangement.End,
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                horizontalAlignment = if (message.author == AuthorDevice.WATCH) Alignment.Start
                else Alignment.End
            ) {
                Box(
                    modifier = modifier
                ) {
                    Text(text = message.content, style = textStyle)
                }
                Text(
                    text = dateFormat.format(Date(message.timestamp)), style = TextStyle(
                        color = Color.LightGray, fontSize = 8.sp
                    )
                )
            }
        }
    }
}
