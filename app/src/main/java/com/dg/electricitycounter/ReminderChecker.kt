package com.dg.electricitycounter

import android.content.Context
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*

object ReminderChecker {
    
    /**
     * Проверяем, нужно ли показать напоминание при открытии приложения
     * Логика: если сегодня 24+ число и показания за этот месяц не переданы
     */
    fun checkAndShowReminder(context: Context) {
        if (shouldShowReminder()) {
            showReminderNotification(context)
        }
    }
    
    /**
     * Проверяем условия для показа напоминания
     */
    private fun shouldShowReminder(): Boolean {
        // 1. Проверяем, включены ли напоминания
        if (!AppState.isReminderEnabled) {
            return false
        }
        
        // 2. Проверяем, сегодня ли 24+ число месяца
        if (!is24thOrLater()) {
            return false
        }
        
        // 3. Проверяем, переданы ли показания за текущий месяц
        return !wereReadingsSubmittedThisMonth()
    }
    
    /**
     * Проверяем, сегодня ли 24+ число месяца
     */
    private fun is24thOrLater(): Boolean {
        val calendar = Calendar.getInstance()
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
        return currentDay >= 24
    }
    
    /**
     * Проверяем, переданы ли показания за текущий месяц
     */
    private fun wereReadingsSubmittedThisMonth(): Boolean {
        val historyItems = AppState.historyItems
        if (historyItems.isEmpty()) {
            return false
        }
        
        val latestItem = historyItems.first()
        val latestDateStr = latestItem.date // формат: "dd.MM.yyyy"
        
        return try {
            val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            val latestDate = sdf.parse(latestDateStr)
            
            if (latestDate != null) {
                val calendar = Calendar.getInstance()
                calendar.time = latestDate
                
                val latestMonth = calendar.get(Calendar.MONTH)
                val latestYear = calendar.get(Calendar.YEAR)
                
                val currentCalendar = Calendar.getInstance()
                val currentMonth = currentCalendar.get(Calendar.MONTH)
                val currentYear = currentCalendar.get(Calendar.YEAR)
                
                // Проверяем, введены ли показания в текущем месяце
                latestMonth == currentMonth && latestYear == currentYear
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Показываем напоминание
     */
    private fun showReminderNotification(context: Context) {
        // Используем существующий NotificationHelper
        val notificationHelper = NotificationHelper(context)
        notificationHelper.showReminderNotification()
        
        // Также показываем Toast для надежности
        Toast.makeText(
            context,
            "🔔 Напоминание: передайте показания за этот месяц!",
            Toast.LENGTH_LONG
        ).show()
    }
    
    /**
     * Для тестирования: принудительно показываем напоминание
     */
    fun testReminder(context: Context) {
        showReminderNotification(context)
    }
}