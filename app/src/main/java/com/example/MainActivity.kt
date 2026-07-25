package com.example

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        AppNavigation()
      }
    }
  }
}

@Composable
fun AppNavigation() {
  val context = LocalContext.current
  val prefs = context.getSharedPreferences("vaa_prefs", Context.MODE_PRIVATE)
  val isFirstLaunch = prefs.getBoolean("first_launch_complete", false).not()
  
  val navController = rememberNavController()
  
  NavHost(
    navController = navController, 
    startDestination = if (isFirstLaunch) "welcome" else "main"
  ) {
    composable("welcome") {
      WelcomeScreen(onGetStarted = {
        prefs.edit().putBoolean("first_launch_complete", true).apply()
        navController.navigate("main") {
          popUpTo("welcome") { inclusive = true }
        }
      })
    }
    composable("main") {
      MainShell()
    }
  }
}

@Composable
fun WelcomeScreen(onGetStarted: () -> Unit) {
  Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .padding(32.dp),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = "Vaa",
        style = MaterialTheme.typography.displayLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(bottom = 16.dp)
      )
      Text(
        text = "One place for your AI chats, Google AI Studio, and GitHub.",
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(bottom = 32.dp)
      )
      Button(onClick = onGetStarted) {
        Text("Get Started")
      }
    }
  }
}

@Composable
fun MainShell() {
  val pagerState = rememberPagerState(pageCount = { 4 })
  val coroutineScope = rememberCoroutineScope()
  
  val tabs = listOf("Chats", "Updates", "Loader", "Tools")
  val icons = listOf(Icons.Default.Chat, Icons.Default.RssFeed, Icons.Default.CloudDownload, Icons.Default.Build)
  
  Scaffold(
    modifier = Modifier.fillMaxSize(),
    bottomBar = {
      Column(modifier = Modifier.fillMaxWidth()) {
        NavigationBar {
          tabs.forEachIndexed { index, title ->
            NavigationBarItem(
              selected = pagerState.currentPage == index,
              onClick = {
                coroutineScope.launch {
                  pagerState.animateScrollToPage(index)
                }
              },
              icon = { Icon(icons[index], contentDescription = title) },
              label = { Text(title) }
            )
          }
        }
        // True bottom-most tab strip for open threads
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(Icons.Default.AccountCircle, contentDescription = "Thread 1")
          Spacer(modifier = Modifier.width(8.dp))
          Icon(Icons.Default.Add, contentDescription = "Add")
          Spacer(modifier = Modifier.weight(1f))
          Icon(Icons.Default.MoreVert, contentDescription = "More")
        }
      }
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize()
      ) { page ->
        when (page) {
          0 -> EmptyTabContent("Chats")
          1 -> EmptyTabContent("Updates")
          2 -> EmptyTabContent("Loader")
          3 -> EmptyTabContent("Tools & Skills")
        }
      }
      
      // Floating Action Buttons overlaid on top of content
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(16.dp)
      ) {
        // Log Keeper shortcut - left, alone
        FloatingActionButton(
          onClick = { /* TODO */ },
          modifier = Modifier.align(Alignment.BottomStart)
        ) {
          Icon(Icons.Default.List, contentDescription = "Log Keeper")
        }
        
        // Stacked right FABs
        Column(
          modifier = Modifier.align(Alignment.BottomEnd),
          verticalArrangement = Arrangement.spacedBy(16.dp),
          horizontalAlignment = Alignment.End
        ) {
          // Omega shortcut - top
          FloatingActionButton(onClick = { /* TODO */ }) {
            Icon(Icons.Default.Face, contentDescription = "Omega")
          }
          // Contextual action - below Omega
          FloatingActionButton(onClick = { /* TODO */ }) {
            Icon(Icons.Default.Add, contentDescription = "Action")
          }
        }
      }
    }
  }
}

@Composable
fun EmptyTabContent(title: String) {
  Box(
    modifier = Modifier.fillMaxSize(),
    contentAlignment = Alignment.Center
  ) {
    Text(text = "$title (Static Placeholder)", style = MaterialTheme.typography.headlineMedium)
  }
}
