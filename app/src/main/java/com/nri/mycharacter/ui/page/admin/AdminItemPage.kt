package com.nri.mycharacter.ui.page.admin

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.nri.mycharacter.service.ItemService
import com.nri.mycharacter.service.mock.ItemServiceMock
import com.nri.mycharacter.viewmodel.CreateItemViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.compose.KoinApplication
import org.koin.dsl.module

@Composable
fun AdminItemPage(
    navHostController: NavHostController
) {
    val viewModel: CreateItemViewModel = koinViewModel()
    val name = viewModel.name.collectAsState()
    val description = viewModel.description.collectAsState()
    val marketCost = viewModel.marketCost.collectAsState()
    val craftCost = viewModel.craftCost.collectAsState()
    val casterLevel = viewModel.casterLevel.collectAsState()
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .padding(all = 5.dp)
                .fillMaxWidth()
        ) {
            Text(text = "Adding item", fontSize = 20.sp)
            Text(text = "Name")
            Spacer(modifier = Modifier.padding(5.dp))
            OutlinedTextField(
                value = name.value,
                onValueChange = { viewModel.updateName(it) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
            )
            Spacer(modifier = Modifier.padding(5.dp))
            Text(text = "Description")
            Spacer(modifier = Modifier.padding(5.dp))
            OutlinedTextField(
                value = description.value,
                onValueChange = { viewModel.updateDescription(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
            )
            Spacer(modifier = Modifier.padding(5.dp))
            Text(text = "Market cost")
            Spacer(modifier = Modifier.padding(5.dp))
            OutlinedTextField(
                value = marketCost.value.toString(),
                onValueChange = {
                    value -> viewModel.updateMarketCost(value.toInt())
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
            )
            Spacer(modifier = Modifier.padding(5.dp))
            Text(text = "Craft cost")
            Spacer(modifier = Modifier.padding(5.dp))
            OutlinedTextField(
                value = craftCost.value.toString(),
                onValueChange = {
                        value -> viewModel.updateCraftCost(value.toInt())
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
            )
            Spacer(modifier = Modifier.padding(5.dp))
            Text(text = "Caster level")
            Spacer(modifier = Modifier.padding(5.dp))
            OutlinedTextField(
                value = casterLevel.value.toString(),
                onValueChange = {
                        value -> viewModel.updateCasterLevel(value.toInt())
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }
}

@Preview
@Composable
fun AdminItemPagePreview() {
    KoinApplication(
        application = {
            modules(
                module { single { ItemServiceMock() as ItemService } },
                module { viewModel { CreateItemViewModel() } }
            )
        }
    ) {
        val navHostController = rememberNavController()
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            AdminItemPage(navHostController = navHostController)
        }
    }
}
