package com.example.todolist.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.todolist.ui.components.EmptyState
import com.example.todolist.ui.components.ErrorState
import com.example.todolist.ui.components.FilterChipRow
import com.example.todolist.ui.components.LoadingState
import com.example.todolist.ui.components.TodoItem
import com.example.todolist.viewmodel.FilterType
import com.example.todolist.viewmodel.HomeUiState
import com.example.todolist.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onAddTodo: () -> Unit,
    onEditTodo: (Long) -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToInspire: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var searchExpanded by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "待办清单",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    IconButton(onClick = { searchExpanded = !searchExpanded }) {
                        Icon(Icons.Default.Search, contentDescription = "搜索")
                    }
                    IconButton(onClick = onNavigateToInspire) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = "每日名言")
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "设置")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddTodo,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Icon(Icons.Default.Add, contentDescription = "添加待办")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 搜索栏
            if (searchExpanded) {
                DockedSearchBar(
                    query = searchQuery,
                    onQueryChange = {
                        searchQuery = it
                        viewModel.setSearchQuery(it)
                    },
                    onSearch = {
                        viewModel.setSearchQuery(searchQuery)
                    },
                    active = false,
                    onActiveChange = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = { Text("搜索待办事项...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "搜索")
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = {
                                searchQuery = ""
                                viewModel.setSearchQuery("")
                            }) {
                                Icon(Icons.Default.DeleteSweep, contentDescription = "清除")
                            }
                        }
                    }
                ) {}
            }

            when (val state = uiState) {
                is HomeUiState.Loading -> {
                    LoadingState()
                }

                is HomeUiState.Empty -> {
                    Column {
                        EmptyState(message = "还没有待办事项\n点击右下角 + 添加一个吧")
                    }
                }

                is HomeUiState.Success -> {
                    // 如果列表为空，显示空状态
                    if (state.todos.isEmpty() && state.searchQuery.isBlank()) {
                        Column {
                            FilterChipRow(
                                selectedFilter = state.selectedFilter,
                                activeCount = state.activeCount,
                                completedCount = state.completedCount,
                                onFilterSelected = { viewModel.setFilter(it) },
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                            EmptyState(message = "还没有待办事项\n点击右下角 + 添加一个吧")
                        }
                    } else {
                    // 筛选栏
                    FilterChipRow(
                        selectedFilter = state.selectedFilter,
                        activeCount = state.activeCount,
                        completedCount = state.completedCount,
                        onFilterSelected = { viewModel.setFilter(it) },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )

                    // 完成进度提示
                    if (state.completedCount > 0 || state.activeCount > 0) {
                        val total = state.activeCount + state.completedCount
                        val progress = if (total > 0) state.completedCount.toFloat() / total else 0f
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "已完成 ${state.completedCount}/$total",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (progress >= 1f) "全部完成！🎉" else "${(progress * 100).toInt()}%",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // 删除已完成的按钮
                    if (state.completedCount > 0) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp)
                        ) {
                            TextButton(onClick = { showDeleteDialog = true }) {
                                Icon(
                                    Icons.Default.DeleteSweep,
                                    contentDescription = null,
                                    modifier = Modifier.padding(end = 4.dp)
                                )
                                Text("清除已完成")
                            }
                        }
                    }

                    // 待办列表
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(
                            items = state.todos,
                            key = { it.todo.id }
                        ) { todoWithCategory ->
                            TodoItem(
                                todoWithCategory = todoWithCategory,
                                onToggleComplete = {
                                    viewModel.toggleTodoCompletion(todoWithCategory.todo)
                                },
                                onDelete = {
                                    viewModel.deleteTodo(todoWithCategory.todo)
                                },
                                onClick = {
                                    onEditTodo(todoWithCategory.todo.id)
                                }
                            )
                        }
                        item { Spacer(modifier = Modifier.height(80.dp)) }
                    }
                    }
                }
            }
        }
    }

    // 清除已完成确认对话框
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("清除已完成") },
            text = { Text("确定要删除所有已完成的待办事项吗？此操作不可撤销。") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteCompletedTodos()
                    showDeleteDialog = false
                }) {
                    Text("确定", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
}
