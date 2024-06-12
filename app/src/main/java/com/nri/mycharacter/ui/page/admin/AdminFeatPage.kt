package com.nri.mycharacter.ui.page.admin

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.nri.mycharacter.entity.Feat
import com.nri.mycharacter.service.FeatService
import com.nri.mycharacter.service.mock.FeatServiceMock
import com.nri.mycharacter.ui.component.ButtonWithText
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
import org.koin.dsl.module

@Composable
fun AdminFeatPage(
    navHostController: NavHostController
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .padding(all = 5.dp)
                .fillMaxWidth()
        ) {
            val name = remember { mutableStateOf("") }
            val description = remember { mutableStateOf("") }
            val featService = koinInject<FeatService>()
            val featList = remember { featService.findAll().map { it.id }.toMutableStateList() }
            Text(text = "Adding feat", fontSize = 20.sp)
            Text(text = "Name")
            Spacer(modifier = Modifier.padding(5.dp))
            OutlinedTextField(
                value = name.value,
                onValueChange = { name.value = it },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()

            )
            Spacer(modifier = Modifier.padding(5.dp))
            Text(text = "Description")
            Spacer(modifier = Modifier.padding(5.dp))
            OutlinedTextField(
                value = description.value,
                onValueChange = { description.value = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
            )
            Spacer(modifier = Modifier.padding(5.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                ButtonWithText(onClick = { navHostController.popBackStack() }, text = "Close")
                ButtonWithText(
                    onClick = {
                        featList.add(
                            featService.save(
                                Feat(name = name.value, description = description.value)
                            )
                        )
                        name.value = ""
                        description.value = ""
                    },
                    enabled = name.value.isNotBlank() && description.value.isNotBlank(),
                    text = "Save")
            }
            Spacer(modifier = Modifier.padding(bottom = 15.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(ScrollState(0))
            ) {
                for (featId in featList) {
                    val feat = featService.find(featId)
                    if (feat != null) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = feat.name!!, fontSize = 20.sp)
                            Spacer(modifier = Modifier.padding(start = 10.dp))
                            Text(
                                text = "X",
                                color = Color.Red,
                                fontSize = 15.sp,
                                modifier = Modifier.clickable {
                                    featList.remove(featId)
                                    featService.delete(feat)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun AddFeatPagePreview() {
    KoinApplication(
        application = {
            modules(
                module { single { FeatServiceMock() as FeatService } }
            )
        }
    ) {
        val navHostController = rememberNavController()
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            AdminFeatPage(navHostController)
        }
    }
}