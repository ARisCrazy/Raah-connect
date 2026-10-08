package com.raahconnect.patient.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.auth.FirebaseAuth
import com.raahconnect.patient.ui.auth.AuthViewModel
import com.raahconnect.patient.ui.auth.LoginScreen
import com.raahconnect.patient.ui.auth.RegisterScreen
import com.raahconnect.patient.ui.emergency.EmergencyConfirmationScreen
import com.raahconnect.patient.ui.home.HomeScreen
import com.raahconnect.patient.ui.home.HomeViewModel
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = viewModel(),
    startDestination: String = if (FirebaseAuth.getInstance().currentUser != null) {
        NavRoutes.Home.route
    } else {
        NavRoutes.Login.route
    }
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(NavRoutes.Login.route) {
            LoginScreen(
                viewModel = authViewModel,
                onNavigateToRegister = {
                    navController.navigate(NavRoutes.Register.route)
                },
                onLoginSuccess = {
                    navController.navigate(NavRoutes.Home.route) {
                        popUpTo(NavRoutes.Login.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(NavRoutes.Register.route) {
            RegisterScreen(
                viewModel = authViewModel,
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onRegisterSuccess = {
                    navController.navigate(NavRoutes.Home.route) {
                        popUpTo(NavRoutes.Login.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(NavRoutes.Home.route) {
            val context = androidx.compose.ui.platform.LocalContext.current.applicationContext as android.app.Application
            val homeViewModel: HomeViewModel = viewModel(
                factory = HomeViewModel.provideFactory(context)
            )
            HomeScreen(
                viewModel = homeViewModel,
                onLogout = {
                    authViewModel.resetLoginState()
                    authViewModel.resetRegisterState()
                    navController.navigate(NavRoutes.Login.route) {
                        popUpTo(NavRoutes.Home.route) {
                            inclusive = true
                        }
                    }
                },
                onEmergencyCreated = { request ->
                    navController.navigate(
                        NavRoutes.Confirmation.createRoute(
                            emergencyId = request.id,
                            emergencyType = request.emergencyType,
                            forWhom = request.forWhom,
                            latitude = request.latitude,
                            longitude = request.longitude
                        )
                    )
                }
            )
        }

        composable(
            route = NavRoutes.Confirmation.route,
            arguments = listOf(
                navArgument("emergencyId") { type = NavType.StringType },
                navArgument("emergencyType") { type = NavType.StringType },
                navArgument("forWhom") { type = NavType.StringType },
                navArgument("lat") { type = NavType.StringType },
                navArgument("lng") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val rawEmergencyId = backStackEntry.arguments?.getString("emergencyId") ?: ""
            val emergencyId = URLDecoder.decode(rawEmergencyId, StandardCharsets.UTF_8.toString())
            val emergencyType = backStackEntry.arguments?.getString("emergencyType") ?: "EMERGENCY"
            val forWhom = backStackEntry.arguments?.getString("forWhom") ?: "SELF"
            val lat = backStackEntry.arguments?.getString("lat")?.toDoubleOrNull() ?: 0.0
            val lng = backStackEntry.arguments?.getString("lng")?.toDoubleOrNull() ?: 0.0

            EmergencyConfirmationScreen(
                emergencyId = emergencyId,
                emergencyType = emergencyType,
                forWhom = forWhom,
                latitude = lat,
                longitude = lng,
                onBackToHome = {
                    navController.popBackStack(NavRoutes.Home.route, inclusive = false)
                }
            )
        }
    }
}
