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
        Toast.makeText(context, "🔍 ReminderChecker: проверка начата", Toast.LENGTH_SHORT).show()
        
        if (shouldShowReminder(context)) {
            Toast.makeText(context, "🔔 ReminderChecker: показываем уведомление", Toast.LENGTH_SHORT).show()
            showReminderNotification(context)
        } else {
            Toast.makeText(context, "❌ ReminderChecker: условия не выполнены", Toast.LENGTH_SHORT).show()
        }
    }
    
    /**
     * Проверяем условия для показа напоминания
     */
    private fun shouldShowReminder(context: Context): Boolean {
        // 1. Проверяем, включены ли напоминания
        Toast.makeText(context, "Проверка 1: напоминания ${if (AppState.isReminderEnabled) "ВКЛ" else "ВЫКЛ"}", Toast.LENGTH_SHORT).show()
        if (!AppState.isReminderEnabled) {
            return false
        }
        
        // 2. Проверяем, сегодня ли 24+ число месяца
        val is24th = is24thOrLater()
        Toast.makeText(context, "Проверка 2: 24+ число? ${if (is24th) "ДА" else "НЕТ"}", Toast.LENGTH_SHORT).show()
        if (!is24th) {
            return false
        }
        
        // 3. Проверяем, переданы ли показания в ЭТОМ месяце (после начала напоминаний 24 числа)
        val submitted = wereReadingsSubmittedAfterReminderStart()
        Toast.makeText(context, "Проверка 3: показания после 24 числа? ${if (submitted) "ДА" else "НЕТ"}", Toast.LENGTH_SHORT).show()
        return !submitted
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
     * Проверяем, переданы ли показания ПОСЛЕ начала напоминаний (24 числа текущего месяца)
     */
    private fun wereReadingsSubmittedAfterReminderStart(): Boolean {
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
                val latestDay = calendar.get(Calendar.DAY_OF_MONTH)
                
                val currentCalendar = Calendar.getInstance()
                val currentMonth = currentCalendar.get(Calendar.MONTH)
                val currentYear = currentCalendar.get(Calendar.YEAR)
                
                // 🔧 ПРАВИЛЬНАЯ ЛОГИКА:
                // Показания считаются "переданными после начала напоминаний" если:
                // 1. Переданы в текущем месяце И текущем году
                // 2. И день передачи >= 24 (после начала напоминаний)
                val isSameMonthYear = latestMonth == currentMonth && latestYear == currentYear
                val isAfter24th = latestDay >= 24
                
                isSameMonthYear && isAfter24th
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
        Toast.makeText(context, "🧪 Тест: запуск testReminder", Toast.LENGTH_SHORT).show()
        showReminderNotification(context)
    }
}