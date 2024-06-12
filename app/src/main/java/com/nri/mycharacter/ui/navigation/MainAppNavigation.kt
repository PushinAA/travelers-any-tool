package com.nri.mycharacter.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.nri.mycharacter.entity.ItemType
import com.nri.mycharacter.ui.page.CraftingProcessPage
import com.nri.mycharacter.ui.page.PrepareToCraftItemFromSpellPage
import com.nri.mycharacter.ui.page.PrepareToCraftPage
import com.nri.mycharacter.ui.page.SelectItemPage
import com.nri.mycharacter.ui.page.SelectItemTypePage
import com.nri.mycharacter.ui.page.StartingPage
import com.nri.mycharacter.ui.page.admin.AdminFeatPage
import com.nri.mycharacter.ui.page.admin.AdminPage

sealed class MainAppRoutes(val route: String) {
    object StartingPage: MainAppRoutes("start")
    object SelectItemType: MainAppRoutes("select-item-type")
    object SelectItem: MainAppRoutes("select")
    object PrepareToCraft: MainAppRoutes("prepare")
    object PrepareToCraftSpellTrigger: MainAppRoutes("prepare-spell-trigger")
    object CraftingProcess: MainAppRoutes("processes")
}

sealed class AdminRoutes(val route: String) {
    object Admin: AdminRoutes("admin")
    object AdminMenu: AdminRoutes("admin-menu")
    object AdminFeats: AdminRoutes("admin-feats")
}

@Composable
fun MainAppNav(
    navController: NavHostController = rememberNavController(),
    startDestination: String = MainAppRoutes.StartingPage.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(route = MainAppRoutes.StartingPage.route) {
            StartingPage(navController = navController)
        }
        composable(route = MainAppRoutes.SelectItemType.route) {
            SelectItemTypePage(navHostController = navController)
        }
        composable(route = MainAppRoutes.SelectItem.route) {
            SelectItemPage(navController = navController)
        }
        composable(
            route = MainAppRoutes.PrepareToCraft.route + "/{itemId}",
            arguments = listOf(navArgument("itemId") { type = NavType.LongType })
        ) {
            PrepareToCraftPage(itemId = it.arguments?.getLong("itemId") ?: 0, navController)
        }
        composable(route = MainAppRoutes.PrepareToCraftSpellTrigger.route + "/{itemType}") {
            PrepareToCraftItemFromSpellPage(
                navHostController = navController,
                itemType = ItemType.valueOf(
                    it.arguments?.getString("itemType", "OTHER") ?: ItemType.OTHER.name
                )
            )
        }
        composable(route = MainAppRoutes.CraftingProcess.route) {
            CraftingProcessPage()
        }
        navigation(startDestination = AdminRoutes.AdminMenu.route, route = AdminRoutes.Admin.route) {
            composable(route = AdminRoutes.AdminMenu.route) {
                AdminPage(navController = navController)
            }
            composable(route = AdminRoutes.AdminFeats.route) {
                AdminFeatPage(navHostController = navController)
            }
        }
    }
}