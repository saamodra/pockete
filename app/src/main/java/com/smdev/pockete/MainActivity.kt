package com.smdev.pockete

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.smdev.pockete.data.AppDatabase
import com.smdev.pockete.data.repository.CategoryRepository
import com.smdev.pockete.data.repository.WalletRepository
import com.smdev.pockete.ui.screens.category.CategoryEditScreen
import com.smdev.pockete.ui.screens.category.CategoryListScreen
import com.smdev.pockete.ui.screens.category.CategoryViewModel
import com.smdev.pockete.ui.screens.category.CategoryViewModelFactory
import com.smdev.pockete.ui.screens.wallet.WalletEditScreen
import com.smdev.pockete.ui.screens.wallet.WalletListScreen
import com.smdev.pockete.ui.screens.wallet.WalletViewModel
import com.smdev.pockete.ui.screens.wallet.WalletViewModelFactory
import com.smdev.pockete.ui.theme.PocketeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PocketeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    WalletApp()
                }
            }
        }
    }
}

sealed class Screen(val route: String, val icon: @Composable (Boolean) -> Unit, val label: String) {
    object Home : Screen("home", { isSelected ->
        Icon(
            painter = painterResource(id = if (isSelected) R.drawable.baseline_home_24 else R.drawable.outline_home_24),
            contentDescription = "Home"
        )
    }, "Home")

    object Categories : Screen(
        "categories",
        { isSelected ->
            Icon(
                painter = painterResource(id = if (isSelected) R.drawable.baseline_category_24 else R.drawable.outline_category_24),
                contentDescription = "Categories"
            )
        },
        "Categories"
    )

    object More :
        Screen("more", { isSelected ->
            Icon(
                painter = painterResource(id = if (isSelected) R.drawable.baseline_settings_24 else R.drawable.outline_settings_24),
                contentDescription = "More"
            )
        }, "More")
}

@Composable
fun WalletApp() {
    val navController = rememberNavController()
    val database = AppDatabase.getDatabase(LocalContext.current)
    val walletViewModel: WalletViewModel = viewModel(
        factory = WalletViewModelFactory(
            WalletRepository(
                walletDao = database.walletDao(),
                walletCategoryDao = database.walletCategoryDao()
            )
        )
    )
    val categoryViewModel: CategoryViewModel = viewModel(
        factory = CategoryViewModelFactory(
            CategoryRepository(categoryDao = database.categoryDao())
        )
    )

    val items = listOf(Screen.Home, Screen.Categories, Screen.More)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 4.dp)
                    .border(width = 1.dp, color = Color.LightGray.copy(alpha = 0.5f)),
                color = Color.White
            ) {
                NavigationBar(
                    containerColor = Color.White,
                    contentColor = Color.Black
                ) {
                    items.forEach { screen ->
                        val isSelected =
                            currentDestination?.hierarchy?.any { it.route == screen.route } == true
                        NavigationBarItem(
                            icon = { screen.icon(isSelected) },
                            label = { Text(screen.label) },
                            selected = isSelected,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color.Black,
                                selectedTextColor = Color.Black,
                                indicatorColor = Color.Black.copy(alpha = 0.1f)
                            )
                        )
                    }
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
                WalletListScreen(
                    viewModel = walletViewModel,
                    categoryViewModel = categoryViewModel,
                    onAddWallet = { navController.navigate("edit") },
                    onEditWallet = { wallet ->
                        navController.navigate("edit/${wallet.id}")
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
                val allCategories by categoryViewModel.categories.collectAsState()
                WalletEditScreen(
                    categories = allCategories,
                    onSave = { title, content, cardHolder, expiryDate, color, selectedCategories ->
                        walletViewModel.addWallet(
                            title,
                            content,
                            cardHolder,
                            expiryDate,
                            color,
                            selectedCategories
                        )
                    },
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable("edit/{walletId}") { backStackEntry ->
                val walletId = backStackEntry.arguments?.getString("walletId")?.toLongOrNull()
                val uiState by walletViewModel.uiState.collectAsState()
                val allCategories by categoryViewModel.categories.collectAsState()

                LaunchedEffect(walletId) {
                    walletId?.let { walletViewModel.fetchWalletById(it) }
                }

                WalletEditScreen(
                    walletWithCategories = uiState.currentWallet,
                    categories = allCategories,
                    onSave = { title, content, cardHolder, expiryDate, color, selectedCategories ->
                        uiState.currentWallet?.let {
                            walletViewModel.updateWallet(
                                it.wallet.copy(
                                    name = title,
                                    number = content,
                                    cardHolder = cardHolder,
                                    expiryDate = expiryDate,
                                    color = color
                                ),
                                selectedCategories
                            )
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
