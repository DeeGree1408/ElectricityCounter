package com.dg.electricitycounter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // ИНИЦИАЛИЗИРУЕМ ХРАНИЛИЩЕ ДАННЫХ
        DataStorage.init(this)
        
        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                SimpleApp()
            }
        }
    }
    
    override fun onResume() {
        super.onResume()
        
        // 🔧 ПРОВЕРЯЕМ НАПОМИНАНИЯ ПРИ КАЖДОМ ОТКРЫТИИ ПРИЛОЖЕНИЯ
        ReminderChecker.checkAndShowReminder(this)
    }
    
    override fun onStart() {
        super.onStart()
        
        // 🔧 ДОПОЛНИТЕЛЬНАЯ ПРОВЕРКА ПРИ ЗАПУСКЕ
        ReminderChecker.checkAndShowReminder(this)
    }
}