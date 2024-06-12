package com.nri.mycharacter.ui.page.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.nri.mycharacter.ui.component.ButtonWithText
import com.nri.mycharacter.ui.navigation.AdminRoutes

@Composable
fun AdminPage(
    navController: NavHostController
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier.height(400.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ButtonWithText(onClick = { /*TODO*/ }, text = "Admin item")
                ButtonWithText(
                    onClick = { navController.navigate(AdminRoutes.AdminFeats.route) },
                    text = "Admin feat")
                ButtonWithText(onClick = { /*TODO*/ }, text = "Admin spell")
                ButtonWithText(onClick = { /*TODO*/ }, text = "Admin template")
                ButtonWithText(onClick = { /*TODO*/ }, text = "Admin modificator item")
                ButtonWithText(onClick = { /*TODO*/ }, text = "Admin item modification")
            }
        }
    }
}

@Preview
@Composable
fun AdminPagePreview() {
    val navController = rememberNavController()
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        AdminPage(navController = navController)
    }
}
