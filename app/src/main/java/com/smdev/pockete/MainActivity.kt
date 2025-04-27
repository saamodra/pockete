package com.smdev.pockete

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.smdev.pockete.data.AppDatabase
import com.smdev.pockete.data.TemplateRepository
import com.smdev.pockete.ui.TemplateViewModel
import com.smdev.pockete.ui.TemplateViewModelFactory
import com.smdev.pockete.ui.category.CategoryEditScreen
import com.smdev.pockete.ui.category.CategoryListScreen
import com.smdev.pockete.ui.category.CategoryViewModel
import com.smdev.pockete.ui.category.CategoryViewModelFactory
import com.smdev.pockete.ui.screens.TemplateEditScreen
import com.smdev.pockete.ui.screens.TemplateListScreen
import com.smdev.pockete.ui.theme.PocketeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PocketeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TemplateApp()
                }
            }
        }
    }
}

sealed class Screen(val route: String, val icon: @Composable () -> Unit, val label: String) {
    object Home : Screen("home", { Icon(Icons.Default.Home, contentDescription = "Home") }, "Home")
    object Categories : Screen("categories", { Icon(painter = painterResource(id = R.drawable.baseline_category_24), contentDescription = "Categories") }, "Categories")
    object More : Screen("more", { Icon(Icons.Default.MoreVert, contentDescription = "More") }, "More")
}

@Composable
fun TemplateApp() {
    val navController = rememberNavController()
    val templateViewModel: TemplateViewModel = viewModel(
        factory = TemplateViewModelFactory(
            TemplateRepository(
                AppDatabase.getDatabase(androidx.compose.ui.platform.LocalContext.current)
                    .templateDao()
            )
        )
    )
    val categoryViewModel: CategoryViewModel = viewModel(
        factory = CategoryViewModelFactory(
            AppDatabase.getDatabase(androidx.compose.ui.platform.LocalContext.current)
                .categoryDao()
        )
    )

    val items = listOf(Screen.Home, Screen.Categories, Screen.More)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        bottomBar = {
            NavigationBar {
                items.forEach { screen ->
                    NavigationBarItem(
                        icon = screen.icon,
                        label = { Text(screen.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Screen.Home.route) {
                TemplateListScreen(
                    viewModel = templateViewModel,
                    onAddTemplate = { navController.navigate("edit") },
                    onEditTemplate = { template ->
                        navController.navigate("edit/${template.id}")
                    }
                )
            }
            composable(Screen.Categories.route) {
                CategoryListScreen(
                    categories = categoryViewModel.categories.collectAsState().value,
                    onAddCategory = { navController.navigate("category/edit") },
                    onEditCategory = { category ->
                        categoryViewModel.selectCategory(category)
                        navController.navigate("category/edit/${category.id}")
                    },
                    onDeleteCategory = { category ->
                        categoryViewModel.deleteCategory(category)
                    }
                )
            }
            composable(Screen.More.route) {
                // TODO: Implement More screen
                Text("More Screen")
            }
            composable("edit") {
                TemplateEditScreen(
                    onSave = { title, content ->
                        templateViewModel.addTemplate(title, content)
                    },
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable("edit/{templateId}") { backStackEntry ->
                val templateId = backStackEntry.arguments?.getString("templateId")?.toLongOrNull()
                val uiState by templateViewModel.uiState.collectAsState()

                LaunchedEffect(templateId) {
                    templateId?.let { templateViewModel.fetchTemplateById(it) }
                }

                TemplateEditScreen(
                    template = uiState.currentTemplate,
                    onSave = { title, content ->
                        uiState.currentTemplate?.let {
                            templateViewModel.updateTemplate(it.copy(title = title, content = content))
                        }
                    },
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable("category/edit") {
                CategoryEditScreen(
                    category = null,
                    onSave = { name, color ->
                        categoryViewModel.addCategory(name, color)
                        navController.popBackStack()
                    },
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable("category/edit/{categoryId}") { backStackEntry ->
                val selectedCategory by categoryViewModel.selectedCategory.collectAsState()

                CategoryEditScreen(
                    category = selectedCategory,
                    onSave = { name, color ->
                        selectedCategory?.let {
                            categoryViewModel.updateCategory(it.copy(name = name, color = color))
                        }
                        navController.popBackStack()
                    },
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
