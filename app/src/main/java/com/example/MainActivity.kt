package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.ui.theme.MyApplicationTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.data.FitApplication
import com.example.ui.FitViewModel
import com.example.ui.FitViewModelFactory
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.SignUpScreen
import com.example.ui.screens.ForgotPasswordScreen
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        val app = application as FitApplication
        val viewModel: FitViewModel = viewModel(
          factory = FitViewModelFactory(app.repository)
        )
        FitApp(viewModel)
      }
    }
  }
}

@Composable
fun FitApp(viewModel: FitViewModel) {
    val navController = rememberNavController()
    val authViewModel: com.example.ui.AuthViewModel = viewModel()
    val userProfile by viewModel.userProfile.collectAsState()

    // Assuming we use "login" as start destination for demonstration
    val startDestination = "login"
    
    NavHost(navController = navController, startDestination = startDestination) {
        composable("login") {
            LoginScreen(
                authViewModel = authViewModel,
                onNavigateToSignUp = { navController.navigate("signup") },
                onNavigateToForgotPassword = { navController.navigate("forgot_password") },
                onLoginSuccess = {
                    if (userProfile != null) {
                        navController.navigate("dashboard") { popUpTo(0) }
                    } else {
                        navController.navigate("onboarding") { popUpTo(0) }
                    }
                }
            )
        }
        composable("signup") {
            SignUpScreen(
                authViewModel = authViewModel,
                onNavigateBack = { navController.popBackStack() },
                onSignUpSuccess = {
                    navController.navigate("onboarding") { popUpTo(0) }
                }
            )
        }
        composable("forgot_password") {
            ForgotPasswordScreen(
                authViewModel = authViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("launcher_decision") {
            // Read from flow
            val profile = viewModel.userProfile.collectAsState().value
            // Wait until it loads completely in a real app, but for now:
            androidx.compose.runtime.LaunchedEffect(profile) {
                if (profile != null) {
                    navController.navigate("dashboard") { popUpTo(0) }
                } else {
                    navController.navigate("onboarding") { popUpTo(0) }
                }
            }
        }
        composable("onboarding") {
            OnboardingScreen(
                viewModel = viewModel,
                onComplete = {
                    navController.navigate("dashboard") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                }
            )
        }
        composable("dashboard") {
            DashboardScreen(
                viewModel = viewModel,
                onScannerClicked = {}
            )
        }
    }
}
