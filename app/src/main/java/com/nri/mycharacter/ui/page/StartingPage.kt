package com.nri.mycharacter.ui.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.nri.mycharacter.ui.navigation.AdminRoutes
import com.nri.mycharacter.ui.navigation.MainAppRoutes

@Composable
fun StartingPage(
    navController: NavHostController
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier.height(300.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = { navController.navigate(MainAppRoutes.SelectItemType.route) },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .width(200.dp)
                        .height(60.dp)
                ) {
                    Text(text = "Create new", fontSize = 25.sp)
                }
                Button(
                    onClick = { navController.navigate(MainAppRoutes.CraftingProcess.route) },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .width(150.dp)
                        .height(40.dp)
                ) {
                    Text(text = "Craft list", fontSize = 17.sp)
                }
                Button(
                    onClick = { navController.navigate(AdminRoutes.Admin.route) },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .width(150.dp)
                        .height(40.dp)
                ) {
                    Text(text = "Admin menu", fontSize = 17.sp)
                }
            }
        }
    }
}

@Preview
@Composable
fun StartingPagePreview() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        val navController = rememberNavController()
        StartingPage(navController = navController)
    }
}
