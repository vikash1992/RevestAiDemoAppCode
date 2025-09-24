package com.revest.ecommerce.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.revest.ecommerce.presentation.products.ProductListScreen
import com.revest.ecommerce.presentation.productdetail.ProductDetailScreen

sealed class Screen(val route: String) {
    data object ProductList : Screen("products")
    data object ProductDetail : Screen("product/{productId}") {
        fun createRoute(productId: Int) = "product/$productId"
    }
}

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.ProductList.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(
            route = Screen.ProductList.route,
            deepLinks = listOf(
                navDeepLink { 
                    uriPattern = "revest://ecommerce/products"
                }
            )
        ) {
            ProductListScreen(
                onProductClick = { productId ->
                    navController.navigate(Screen.ProductDetail.createRoute(productId))
                }
            )
        }

        composable(
            route = Screen.ProductDetail.route,
            arguments = listOf(
                navArgument("productId") {
                    type = NavType.IntType
                }
            ),
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "revest://ecommerce/product/{productId}"
                }
            )
        ) {
            ProductDetailScreen(
                onNavigateUp = {
                    navController.navigateUp()
                }
            )
        }
    }
}