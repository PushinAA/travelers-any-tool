package com.nri.mycharacter.ui.page

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.nri.mycharacter.entity.Item
import com.nri.mycharacter.ui.component.ItemDetailDialog
import com.nri.mycharacter.ui.component.SelectItem
import com.nri.mycharacter.ui.navigation.MainAppRoutes

@Composable
fun SelectItemPage(
    navController: NavHostController
) {
    Box (
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        val selectedItem = remember { mutableStateOf<Item?>(null) }
        val openItemDetail = remember { mutableStateOf(false) }
        SelectItem {
            selectedItem.value = it
            openItemDetail.value = true
        }
        when {
            openItemDetail.value -> ItemDetailDialog(
                item = selectedItem.value!!,
                onDismissRequest = { openItemDetail.value = false },
                onAccept = {
                    navController.navigate(
                        MainAppRoutes.PrepareToCraft.route + "/${it.id}L"
                    )
                }
            )
        }
    }
}
