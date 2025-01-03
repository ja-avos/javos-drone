package co.javos.watchflyphoneapp.ui.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import co.javos.watchflyphoneapp.models.Alert
import co.javos.watchflyphoneapp.models.AlertType
import co.javos.watchflyphoneapp.viewmodels.AlertsViewModel

@Preview
@Composable
fun AlertsPopup(
    viewModel: AlertsViewModel? = null,
    onDismiss: () -> Unit = {}
) {

    val alerts = viewModel?.alerts?.collectAsState()?.value ?: emptyList()

    val backgroundColor = Color.White

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { },
        title = {
            Text(
                "Alerts (${alerts.size})",
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = Color.Black,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            if (alerts.isEmpty())
                Text(
                    "No alerts",
                    textAlign = TextAlign.Center,
                    color = Color.Black,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            else
                LazyColumn(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    items(alerts) {
                        AlertRow(it)
                        HorizontalDivider()
                    }
                }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss, colors = ButtonDefaults.textButtonColors().copy(
                    contentColor = Color.Black,
                ), modifier = Modifier.padding(0.dp)
            ) {
                Text("Close", fontWeight = FontWeight.Bold)
            }
        },
        containerColor = backgroundColor,
    )
}

@Composable
fun AlertRow(alert: Alert) {
    val textColor = when (alert.type) {
        AlertType.WARNING -> Color.Black
        AlertType.ERROR -> Color.Black
        AlertType.INFO -> Color.Black
        else -> Color.Black
    }
    val iconColor = when (alert.type) {
        AlertType.WARNING -> Color(0xFFF7BA37)
        AlertType.ERROR -> Color.Red
        AlertType.INFO -> Color.Blue
        else -> Color.Gray
    }
    val icon = when (alert.type) {
        AlertType.WARNING -> Icons.Default.Warning
        AlertType.ERROR -> Icons.Default.Error
        AlertType.INFO -> Icons.Default.Info
        else -> Icons.Default.QuestionMark
    }

    val collapsed = remember { mutableStateOf(true) }

    val alertText = buildAnnotatedString {
        pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
        append(alert.title)
        pop()
        append(" - ")
        append(alert.message)
        if (!alert.solution.isNullOrEmpty()) {
            append(" - ")
            pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
            append("View ${if (collapsed.value) "more" else "less"}...")
            pop()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .clickable {
                    collapsed.value = !collapsed.value
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                modifier = Modifier.weight(1F),
                contentDescription = alert.type.toString(),
                tint = iconColor
            )
            VerticalDivider(modifier = Modifier.padding(vertical = 8.dp))
            Text(
                alertText,
                modifier = Modifier
                    .weight(5F)
                    .padding(8.dp),
                color = textColor,
                style = TextStyle(fontSize = 4.em)
            )
            Text(
                alert.code ?: "No code",
                modifier = Modifier.weight(1F),
                color = textColor,
                fontWeight = FontWeight.Bold,
                style = TextStyle(fontSize = 4.em)
            )
        }
        if (!collapsed.value && !alert.solution.isNullOrEmpty()) {
                Text(
                    alert.solution
                )
        }
    }
}