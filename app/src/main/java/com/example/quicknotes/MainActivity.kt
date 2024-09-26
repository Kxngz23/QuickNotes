@file:JvmName("NoteKt")

package com.example.quicknotes

import android.os.Bundle
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.clickable
import androidx.compose.ui.unit.dp
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.quicknotes.ui.theme.QuickNotesTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QuickNotesTheme {
                QuickNotesApp()
            }
        }
    }
}

@Composable
fun QuickNotesApp() {
    val navController = rememberNavController()
    var notes by remember { mutableStateOf(listOf<Note>()) }

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(navController, notes) { noteId ->
                notes = notes.filterNot { it.id == noteId } // Delete the note
            }
        }
        composable("create") {
            CreateNoteScreen(onSave = { note ->
                notes = notes + note.copy(id = notes.size + 1) // Add the new note to the list with a unique ID
                navController.navigate("home") // Navigate back to home after saving
            })
        }
        composable("edit/{noteId}") { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("noteId")?.toInt()
            EditNoteScreen(noteId, notes) { updatedNote ->
                notes = notes.map { if (it.id == updatedNote.id) updatedNote else it } // Update the note in the list
                navController.navigate("home") // Navigate back after editing
            }
        }
    }
}

@Composable
fun HomeScreen(navController: NavHostController, notes: List<Note>, onDelete: (Int) -> Unit) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            Text(text = "Home Screen", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                navController.navigate("create") // Navigate to Create Note screen
            }) {
                Text("Add Note")
            }
            Spacer(modifier = Modifier.height(16.dp))
            // Display the list of notes
            LazyColumn {
                items(notes) { note ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { navController.navigate("edit/${note.id}") } // Navigate to Edit Note screen
                            .padding(8.dp)
                    ) {
                        Text(text = "${note.title}: ${note.content}", modifier = Modifier.weight(1f))
                        IconButton(onClick = { onDelete(note.id) }) {
                            Icon(imageVector = Icons.Filled.Delete, contentDescription = "Delete Note")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CreateNoteScreen(onSave: (Note) -> Unit) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = content,
            onValueChange = { content = it },
            label = { Text("Content") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                if (title.isNotBlank() && content.isNotBlank()) {
                    onSave(Note(0, title, content)) // Use a proper ID handling method later
                }
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Save")
        }
    }
}

@Composable
fun EditNoteScreen(noteId: Int?, notes: List<Note>, onSave: (Note) -> Unit) {
    val note = notes.find { it.id == noteId } ?: return // Find the note to edit
    var title by remember { mutableStateOf(note.title) }
    var content by remember { mutableStateOf(note.content) }

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = content,
            onValueChange = { content = it },
            label = { Text("Content") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                if (title.isNotBlank() && content.isNotBlank()) {
                    onSave(note.copy(title = title, content = content)) // Save the edited note
                }
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Save")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    QuickNotesTheme {
        Text("Preview")
    }
}
