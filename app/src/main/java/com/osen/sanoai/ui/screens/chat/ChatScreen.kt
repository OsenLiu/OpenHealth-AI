package com.osen.sanoai.ui.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.osen.sanoai.data.api.AiProvider
import androidx.compose.ui.res.stringResource
import com.osen.sanoai.R
import com.osen.sanoai.data.api.model.ChatMessage
import com.osen.sanoai.ui.components.OrganicBlobShape
import com.osen.sanoai.ui.theme.*
import com.osen.sanoai.ui.viewmodel.ChatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(viewModel: ChatViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.chat_title), color = VitaMindDarkBrown) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = VitaMindBackground,
                    titleContentColor = VitaMindDarkBrown
                )
            )
        },
        bottomBar = {
            Surface(
                tonalElevation = 3.dp,
                color = VitaMindBackground
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .fillMaxWidth()
                        .imePadding(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text(stringResource(R.string.chat_placeholder), color = VitaMindBrown.copy(alpha = 0.6f)) },
                        maxLines = 4,
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = VitaMindBrown,
                            unfocusedBorderColor = VitaMindBrown.copy(alpha = 0.3f),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (inputText.isNotBlank()) {
                                viewModel.sendMessage(inputText, viewModel.getSelectedAiProvider())
                                inputText = ""
                            }
                        },
                        enabled = inputText.isNotBlank() && !uiState.isLoading,
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = VitaMindCoral,
                            contentColor = VitaMindDarkBrown,
                            disabledContainerColor = VitaMindCoral.copy(alpha = 0.5f)
                        )
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = VitaMindDarkBrown,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(Icons.AutoMirrored.Rounded.Send, contentDescription = "Send")
                        }
                    }
                }
            }
        },
        containerColor = VitaMindBackground
    ) { padding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (uiState.messages.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                        Card(
                            shape = OrganicBlobShape(),
                            colors = CardDefaults.cardColors(containerColor = VitaMindMint.copy(alpha = 0.5f)),
                            modifier = Modifier.padding(24.dp)
                        ) {
                            Text(
                                stringResource(R.string.chat_welcome),
                                style = MaterialTheme.typography.bodyLarge,
                                color = VitaMindDarkBrown,
                                modifier = Modifier.padding(32.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            items(uiState.messages) { message ->
                ChatBubble(message)
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val isUser = message.role == "user"
    val alignment = if (isUser) Alignment.End else Alignment.Start
    val containerColor = if (isUser) VitaMindSkyBlue else VitaMindMint
    val shape = if (isUser) {
        RoundedCornerShape(20.dp, 20.dp, 4.dp, 20.dp)
    } else {
        // Use RoundedCornerShape for AI to prevent clipping of long text
        RoundedCornerShape(24.dp)
    }

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = alignment) {
        Box(
            modifier = Modifier
                .clip(shape)
                .background(containerColor)
                .padding(16.dp)
                .widthIn(max = 300.dp)
        ) {
            Text(
                text = message.content.toString(),
                color = VitaMindDarkBrown,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
