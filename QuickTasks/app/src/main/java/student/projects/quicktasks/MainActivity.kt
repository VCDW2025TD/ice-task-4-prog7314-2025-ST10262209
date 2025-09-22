package student.projects.quicktasks

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import androidx.compose.runtime.saveable.rememberSaveable


data class Task(val id: Int, val text: String, val done: Boolean = false)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QuickTasksApp()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickTasksApp() {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var input by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue("")) }
    var tasks by rememberSaveable { mutableStateOf(listOf<Task>()) }

    val completed = tasks.count { it.done }

    Scaffold(
        topBar = { TopAppBar(title = { Text("QuickTasks") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                val cleared = tasks.count { it.done }
                tasks = tasks.filterNot { it.done }
                scope.launch {
                    val msg = if (cleared > 0) "Cleared $cleared task(s)" else "No completed tasks"
                    snackbarHostState.showSnackbar(msg)
                }
            }) {
                Icon(Icons.Default.Delete, contentDescription = "Clear completed")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Row {
                TextField(
                    value = input,
                    onValueChange = { input = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Enter task") }
                )
                Spacer(Modifier.width(8.dp))
                Button(onClick = {
                    if (input.text.isNotBlank()) {
                        tasks = tasks + Task(id = tasks.size + 1, text = input.text)
                        input = TextFieldValue("")
                    }
                }) { Text("Add") }
            }

            Spacer(Modifier.height(12.dp))

            Text("Completed: $completed / ${tasks.size}")

            Spacer(Modifier.height(12.dp))

            LazyColumn {
                items(tasks, key = { it.id }) { task ->
                    TaskItem(task = task, onCheckedChange = { checked ->
                        tasks = tasks.map { if (it.id == task.id) it.copy(done = checked) else it }
                    })
                }
            }
        }
    }
}

@Composable
fun TaskItem(task: Task, onCheckedChange: (Boolean) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(task.text)
            Checkbox(checked = task.done, onCheckedChange = onCheckedChange)
        }
    }
}
