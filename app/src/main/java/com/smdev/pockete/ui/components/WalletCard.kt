package com.smdev.pockete.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smdev.pockete.R
import com.smdev.pockete.data.fake.dummyWallet
import com.smdev.pockete.data.model.Wallet
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun WalletCard(
    wallet: Wallet,
    onCopy: () -> Unit,
    onShare: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val expiryDate = wallet.expiryDate?.let {
        val dateFormat = SimpleDateFormat("MM/yyyy", Locale.getDefault())
        dateFormat.format(it)
    } ?: "N/A"

    val backgroundColor = Color(wallet.color)
    val isLightColor = backgroundColor.luminance() > 0.5f
    val textColor = if (isLightColor) Color.Black else Color.White

    Card(
        modifier = Modifier.Companion
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onEdit),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(backgroundColor)
        ) {
            Column(
                modifier = Modifier.Companion
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.Companion.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column {
                        Text(
                            text = wallet.name,
                            style = MaterialTheme.typography.titleSmall,
                            color = textColor
                        )
                        Text(
                            text = wallet.number,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge,
                            color = textColor
                        )
                    }
                    IconButton(
                        onClick = onCopy,
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_content_copy_24),
                            contentDescription = "Copy",
                            tint = textColor
                        )
                    }
                }
                Spacer(modifier = Modifier.Companion.height(8.dp))
                Row(
                    modifier = Modifier.Companion
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column {
                        Text(
                            fontSize = 8.sp,
                            text = "Name",
                            color = textColor.copy(alpha = 0.7f)
                        )
                        Text(
                            fontWeight = FontWeight.Bold,
                            text = wallet.cardHolder,
                            color = textColor
                        )
                    }
                    Column {
                        Text(
                            fontSize = 8.sp,
                            text = "Expires",
                            color = textColor.copy(alpha = 0.7f)
                        )
                        Text(
                            fontWeight = FontWeight.Bold,
                            text = expiryDate,
                            color = textColor
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WalletCardPreview() {
    WalletCard(
        wallet = dummyWallet,
        onCopy = {},
        onShare = {},
        onEdit = {},
        onDelete = {}
    )
}
