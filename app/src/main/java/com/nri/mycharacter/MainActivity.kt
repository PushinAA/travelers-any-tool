package com.nri.mycharacter

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.nri.mycharacter.module.MainModule
import com.nri.mycharacter.store.ObjectBox
import com.nri.mycharacter.ui.navigation.StartNav
import com.nri.mycharacter.ui.theme.MyCharacterTheme
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.compose.KoinContext
import org.koin.core.context.startKoin


class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyCharacterTheme {
                KoinContext {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        StartNav(this.resources)
                    }
                }
            }
        }
        startKoin {
            androidLogger()
            androidContext(this@MainActivity)
            modules(MainModule())
        }
        ObjectBox.init(this)
    }
}
