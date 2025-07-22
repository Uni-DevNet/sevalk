package com.sevalk.presentation.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sevalk.ui.theme.S_GREEN
import com.sevalk.ui.theme.S_YELLOW
import com.sevalk.ui.theme.SevaLKTheme
import com.sevalk.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch
import com.google.firebase.firestore.FirebaseFirestore

data class ChatItem(
    val name: String,
    val message: String,
    val time: String,
    val unreadCount: Int = 0,
    val isOnline: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    onChatItemClick: (ChatItem) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val chatItems = remember { mutableStateListOf<ChatItem>() }
    val currentUser = FirebaseAuth.getInstance().currentUser
    val database = FirebaseDatabase.getInstance()
    val chatRef = database.getReference("chats")
    val coroutineScope = rememberCoroutineScope()
    val firestore = remember { FirebaseFirestore.getInstance() }

    // Listen for chat conversations for the current user
    LaunchedEffect(currentUser?.uid) {
        if (currentUser?.uid == null) return@LaunchedEffect
        chatRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val newChats = mutableListOf<ChatItem>()
                val chatNames = mutableListOf<String>()
                for (userSnapshot in snapshot.children) {
                    val userName = userSnapshot.key ?: continue
                    chatNames.add(userName)
                    val messagesSnapshot = userSnapshot.child("messages")
                    var lastMessageText = ""
                    var lastMessageTime = ""
                    var unreadCount = 0
                    var lastMessageFromMe = false

                    // Find the last message and count unread
                    var lastMessageKey: String? = null
                    for (msg in messagesSnapshot.children) {
                        lastMessageKey = msg.key
                        val fromMe = msg.child("fromMe").getValue(Boolean::class.java) ?: false
                        val isRead = msg.child("isRead").getValue(Boolean::class.java) ?: false
                        // Count as unread if not from me and not read
                        if (!fromMe && !isRead) {
                            unreadCount++
                        }
                    }
                    if (lastMessageKey != null) {
                        val lastMsg = messagesSnapshot.child(lastMessageKey)
                        lastMessageText = lastMsg.child("text").getValue(String::class.java) ?: ""
                        lastMessageTime = lastMsg.child("time").getValue(String::class.java) ?: ""
                        lastMessageFromMe = lastMsg.child("fromMe").getValue(Boolean::class.java) ?: false
                    }

                    newChats.add(
                        ChatItem(
                            name = userName,
                            message = lastMessageText,
                            time = lastMessageTime,
                            unreadCount = unreadCount,
                            isOnline = false // Will update below
                        )
                    )
                }
                chatItems.clear()
                chatItems.addAll(newChats)

                // Fetch online status for each chat participant (service provider)
                chatNames.forEachIndexed { idx, name ->
                    firestore.collection("service_providers")
                        .whereEqualTo("businessName", name)
                        .limit(1)
                        .get()
                        .addOnSuccessListener { documents ->
                            val isOnline = documents.firstOrNull()?.getBoolean("isAvailable") == true
                            if (idx < chatItems.size) {
                                chatItems[idx] = chatItems[idx].copy(isOnline = isOnline)
                            }
                        }
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Messages",
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )
            
            IconButton(
                onClick = { /* Handle search */ },
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        Color.Gray.copy(alpha = 0.1f),
                        CircleShape
                    )
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.search),
                    contentDescription = "Search",
                    tint = Color.Gray
                )
            }
        }
        
        // Messages List
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(chatItems) { chatItem ->
                ChatItemRow(
                    chatItem = chatItem,
                    onClick = { onChatItemClick(chatItem) }
                )
            }
        }
    }
}

@Composable
fun ChatItemRow(
    chatItem: ChatItem,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar
        Box(
            modifier = Modifier
                .size(50.dp)
                .background(Color.Gray.copy(alpha = 0.3f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            // Online indicator
            if (chatItem.isOnline) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(12.dp)
                        .background(S_GREEN, CircleShape)
                        .padding(2.dp)
                )
            }
        }
        
        // Message content
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = chatItem.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    modifier = Modifier.weight(1f)
                )
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = chatItem.time,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    
                    if (chatItem.unreadCount > 0) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .background(S_YELLOW, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = chatItem.unreadCount.toString(),
                                fontSize = 12.sp,
                                color = Color.Black,
                                fontWeight = FontWeight.Normal,
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = chatItem.message,
                fontSize = 12.sp,
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview
@Composable
fun ChatScreenPreview() {
    SevaLKTheme {
        ChatScreen()
    }
}

