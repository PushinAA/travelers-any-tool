package com.nri.mycharacter.ui.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.nri.mycharacter.entity.ItemType
import com.nri.mycharacter.ui.component.ButtonWithText
import com.nri.mycharacter.ui.navigation.MainAppRoutes

@Composable
fun SelectItemTypePage(navHostController: NavHostController) {
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
                ButtonWithText(
                    onClick = { navHostController.navigate(MainAppRoutes.SelectItem.route) },
                    text = "Standard",
                    fontSize = 25.sp,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .width(200.dp)
                        .height(60.dp)
                )
                ButtonWithText(
                    onClick = { navHostController.navigate(
                        MainAppRoutes.PrepareToCraftSpellTrigger.route + "/${ItemType.WAND.name}"
                    )},
                    text = "Wand",
                    fontSize = 25.sp,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .width(200.dp)
                        .height(60.dp)
                )
                ButtonWithText(
                    onClick = { navHostController.navigate(
                        MainAppRoutes.PrepareToCraftSpellTrigger.route + "/${ItemType.SCROLL.name}"
                    )},
                    text = "Scroll",
                    fontSize = 25.sp,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .width(200.dp)
                        .height(60.dp)
                )
                ButtonWithText(
                    onClick = { navHostController.navigate(
                        MainAppRoutes.PrepareToCraftSpellTrigger.route + "/${ItemType.POTION.name}"
                    )},
                    text = "Potion",
                    fontSize = 25.sp,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .width(200.dp)
                        .height(60.dp)
                )
            }
        }
    }
}

@Preview
@Composable
fun SelectItemTypePagePreview() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        val navController = rememberNavController()
        SelectItemTypePage(navHostController = navController)
    }
}
