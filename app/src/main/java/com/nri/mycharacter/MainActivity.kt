package com.nri.mycharacter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.nri.mycharacter.infra.service.ChangesetService
import com.nri.mycharacter.module.MainModule
import com.nri.mycharacter.store.ObjectBox
import com.nri.mycharacter.ui.navigation.MainAppNav
import com.nri.mycharacter.ui.theme.MyCharacterTheme
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.compose.KoinContext
import org.koin.core.context.startKoin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyCharacterTheme {
                KoinContext {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        MainAppNav()
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
        val changesetService: ChangesetService = get()
        changesetService.applyAll(this.resources)
    }
}
