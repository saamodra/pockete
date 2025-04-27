package com.smdev.pockete

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.smdev.pockete.data.TemplateDatabase
import com.smdev.pockete.data.TemplateRepository
import com.smdev.pockete.ui.TemplateViewModel
import com.smdev.pockete.ui.TemplateViewModelFactory
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

@Composable
fun TemplateApp() {
    val navController = rememberNavController()
    val viewModel: TemplateViewModel = viewModel(
        factory = TemplateViewModelFactory(
            TemplateRepository(
                TemplateDatabase.getDatabase(androidx.compose.ui.platform.LocalContext.current)
                    .templateDao()
            )
        )
    )

    NavHost(navController = navController, startDestination = "list") {
        composable("list") {
            TemplateListScreen(
                viewModel = viewModel,
                onAddTemplate = { navController.navigate("edit") },
                onEditTemplate = { template ->
                    navController.navigate("edit/${template.id}")
                }
            )
        }
        composable("edit") {
            TemplateEditScreen(
                onSave = { title, content ->
                    viewModel.addTemplate(title, content)
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("edit/{templateId}") { backStackEntry ->
            val templateId = backStackEntry.arguments?.getString("templateId")?.toLongOrNull()
            val uiState by viewModel.uiState.collectAsState()

            LaunchedEffect(templateId) {
                templateId?.let { viewModel.fetchTemplateById(it) }
            }

            TemplateEditScreen(
                template = uiState.currentTemplate,
                onSave = { title, content ->
                    uiState.currentTemplate?.let {
                        viewModel.updateTemplate(it.copy(title = title, content = content))
                    }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
