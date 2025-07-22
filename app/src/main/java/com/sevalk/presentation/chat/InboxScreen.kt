package com.sevalk.presentation.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sevalk.ui.theme.S_YELLOW
import com.sevalk.ui.theme.SevaLKTheme
import com.sevalk.R
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class Message(
    val text: String,
    val time: String,
    val isFromMe: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InboxScreen(
    contactName: String = "Mike's Plumbing",
    isOnline: Boolean = true,
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var messageText by remember { mutableStateOf("") }
    val messages = remember { mutableStateListOf<Message>() }
    val database = FirebaseDatabase.getInstance()
    val chatId = remember(contactName) { contactName.replace(" ", "_").lowercase() } // Simple chat id, improve as needed
    val messagesRef = database.getReference("chats/$chatId/messages")
    val coroutineScope = rememberCoroutineScope()

    // Listen for messages from Firebase
    LaunchedEffect(chatId) {
        messagesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val newMessages = mutableListOf<Message>()
                for (msgSnapshot in snapshot.children) {
                    val text = msgSnapshot.child("text").getValue(String::class.java) ?: ""
                    val time = msgSnapshot.child("time").getValue(String::class.java) ?: ""
                    val isFromMe = when (val value = msgSnapshot.child("fromMe").value) {
                        is Boolean -> value
                        is String -> value.toBoolean()
                        else -> false
                    }
                    newMessages.add(Message(text, time, isFromMe))
                }
                messages.clear()
                messages.addAll(newMessages)
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(top = 28.dp),
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.Black
                    )
                }
                
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.Gray.copy(alpha = 0.3f), CircleShape)
                )
                
                Column {
                    Text(
                        text = contactName,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                    if (isOnline) {
                        Text(
                            text = "Online",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = { /* Handle call */ },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        Icons.Default.Call,
                        contentDescription = "Call",
                        tint = Color.Black
                    )
                }
                
                IconButton(
                    onClick = { /* Handle more options */ },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = "More",
                        tint = Color.Black
                    )
                }
            }
        }
        
        // Messages
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages) { message ->
                MessageBubble(message = message)
            }
        }
        
        // Message Input
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Gray.copy(alpha = 0.05f))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = messageText,
                onValueChange = { messageText = it },
                placeholder = { 
                    Text(
                        "Message",
                        color = Color.Gray,
                        fontSize = 16.sp
                    ) 
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(28.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Gray.copy(alpha = 0.2f),
                    unfocusedBorderColor = Color.Gray.copy(alpha = 0.2f),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                trailingIcon = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        IconButton(
                            onClick = { /* Handle attachment */ },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.attachment),
                                contentDescription = "Attach",
                                tint = Color.Gray,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        
                        IconButton(
                            onClick = { /* Handle camera */ },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.camera),
                                contentDescription = "Camera",
                                tint = Color.Gray,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            )
            
            IconButton(
                onClick = { 
                    if (messageText.isNotBlank()) {
                        val newMessage = Message(
                            text = messageText,
                            time = java.text.SimpleDateFormat("hh:mm a").format(java.util.Date()),
                            isFromMe = true
                        )
                        // Add to local list immediately
                        messages.add(newMessage)
                        // Save to Firebase
                        coroutineScope.launch(Dispatchers.IO) {
                            val key = messagesRef.push().key ?: System.currentTimeMillis().toString()
                            messagesRef.child(key).setValue(newMessage)
                        }
                        messageText = ""
                    }
                },
                modifier = Modifier
                    .size(48.dp)
                    .background(S_YELLOW, CircleShape)
            ) {
                Icon(
                    Icons.Default.Send,
                    contentDescription = "Send",
                    tint = Color.Black,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun MessageBubble(
    message: Message,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isFromMe) Arrangement.End else Arrangement.Start
    ) {
        Column(
            horizontalAlignment = if (message.isFromMe) Alignment.End else Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .background(
                        color = if (message.isFromMe) S_YELLOW else Color.Gray.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (message.isFromMe) 16.dp else 4.dp,
                            bottomEnd = if (message.isFromMe) 4.dp else 16.dp
                        )
                    )
                    .padding(12.dp)
                    .widthIn(max = 280.dp)
            ) {
                Text(
                    text = message.text,
                    fontSize = 14.sp,
                    color = if (message.isFromMe) Color.White else Color.Black
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = message.time,
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}

@Preview
@Composable
fun InboxScreenPreview() {
    SevaLKTheme {
        InboxScreen()
    }
}
