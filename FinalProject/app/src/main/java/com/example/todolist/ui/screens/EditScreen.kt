package com.example.todolist.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.todolist.data.entity.CategoryEntity
import com.example.todolist.model.Priority
import com.example.todolist.ui.components.formatDate
import com.example.todolist.viewmodel.EditUiState
import com.example.todolist.viewmodel.EditViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditScreen(
    todoId: Long,
    viewModel: EditViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(todoId) {
        if (todoId == 0L) {
            viewModel.loadForNew()
        } else {
            viewModel.loadForEdit(todoId)
        }
    }

    LaunchedEffect(uiState) {
        when (uiState) {
            is EditUiState.Saved -> {
                viewModel.resetState()
                onNavigateBack()
            }

            is EditUiState.Error -> {
                snackbarHostState.showSnackbar((uiState as EditUiState.Error).message)
            }

            else -> {}
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (todoId == 0L) "新增待办" else "编辑待办",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        val editingState = uiState as? EditUiState.Editing

        if (editingState != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // 标题输入
                OutlinedTextField(
                    value = editingState.title,
                    onValueChange = { viewModel.updateTitle(it) },
                    label = { Text("标题 *") },
                    placeholder = { Text("输入待办事项标题") },
                    isError = editingState.titleError != null,
                    supportingText = editingState.titleError?.let { { Text(it) } },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 描述输入
                OutlinedTextField(
                    value = editingState.description,
                    onValueChange = { viewModel.updateDescription(it) },
                    label = { Text("描述") },
                    placeholder = { Text("添加详细描述（可选）") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 优先级选择
                Text(
                    text = "优先级",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(modifier = Modifier.fillMaxWidth()) {
                    Priority.entries.forEach { priority ->
                        val isSelected = editingState.priority == priority.value
                        val priorityColor = when (priority) {
                            Priority.HIGH -> com.example.todolist.ui.theme.PriorityHigh
                            Priority.MEDIUM -> com.example.todolist.ui.theme.PriorityMedium
                            Priority.LOW -> com.example.todolist.ui.theme.PriorityLow
                        }
                        androidx.compose.material3.FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.updatePriority(priority.value) },
                            label = { Text(priority.label) },
                            modifier = Modifier.padding(end = 8.dp),
                            colors = if (isSelected) androidx.compose.material3.FilterChipDefaults.filterChipColors(
                                containerColor = priorityColor,
                                labelColor = androidx.compose.ui.graphics.Color.White
                            ) else androidx.compose.material3.FilterChipDefaults.filterChipColors()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 分类选择
                CategoryDropdown(
                    categories = editingState.categories,
                    selectedCategoryId = editingState.categoryId,
                    onCategorySelected = { viewModel.updateCategory(it) }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 截止日期选择
                Text(
                    text = "截止日期",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val calendar = Calendar.getInstance()
                            if (editingState.dueDate > 0) {
                                calendar.timeInMillis = editingState.dueDate
                            }
                            DatePickerDialog(
                                context,
                                { _, year, month, dayOfMonth ->
                                    val selectedCalendar = Calendar.getInstance()
                                    selectedCalendar.set(year, month, dayOfMonth, 23, 59, 59)
                                    viewModel.updateDueDate(selectedCalendar.timeInMillis)
                                },
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH)
                            ).show()
                        }
                        .padding(12.dp)
                ) {
                    Icon(
                        Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = formatDate(editingState.dueDate),
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (editingState.dueDate > 0)
                            MaterialTheme.colorScheme.onSurface
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (editingState.dueDate > 0) {
                        Spacer(modifier = Modifier.weight(1f))
                        IconButton(onClick = { viewModel.updateDueDate(0L) }) {
                            Icon(
                                Icons.Default.Clear,
                                contentDescription = "清除日期",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 保存按钮
                Button(
                    onClick = { viewModel.saveTodo(todoId) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = !editingState.isSaving
                ) {
                    if (editingState.isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.padding(end = 8.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (todoId == 0L) "添加待办" else "保存修改",
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDropdown(
    categories: List<CategoryEntity>,
    selectedCategoryId: Long,
    onCategorySelected: (Long) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedCategory = categories.find { it.id == selectedCategoryId }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedCategory?.name ?: "未分类",
            onValueChange = {},
            readOnly = true,
            label = { Text("分类") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("未分类") },
                onClick = {
                    onCategorySelected(0L)
                    expanded = false
                }
            )
            categories.forEach { category ->
                DropdownMenuItem(
                    text = { Text(category.name) },
                    onClick = {
                        onCategorySelected(category.id)
                        expanded = false
                    }
                )
            }
        }
    }
}
