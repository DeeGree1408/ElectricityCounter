package com.dg.electricitycounter

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*

class ReminderScheduler(private val context: Context) {
    
    companion object {
        const val REQUEST_CODE_24TH = 2001
        const val REQUEST_CODE_DAILY = 2002
        private const val ALARM_INTERVAL = 24 * 60 * 60 * 1000L // 24 часа
    }
    
    fun scheduleMonthlyReminder() {
        // 🔧 НОВАЯ СИСТЕМА: только включаем флаг, не планируем AlarmManager
        Toast.makeText(
            context,
            "✅ Напоминания включены!\n" +
            "Проверка будет при каждом открытии приложения",
            Toast.LENGTH_LONG
        ).show()
    }
    
    private fun getNext24thDate(): Calendar {
        val now = Calendar.getInstance()
        val currentDay = now.get(Calendar.DAY_OF_MONTH)
        val currentHour = now.get(Calendar.HOUR_OF_DAY)
        val currentMinute = now.get(Calendar.MINUTE)
        
        val targetDate = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.DAY_OF_MONTH, 24)
            set(Calendar.HOUR_OF_DAY, 12)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        
        // Если сегодня уже 24 число, но еще не 12:00
        if (currentDay == 24 && currentHour < 12) {
            return targetDate // сегодня в 12:00
        }
        
        // Если сегодня 24 число и уже после 12:00, или если сегодня после 24 числа
        if (currentDay >= 24) {
            targetDate.add(Calendar.MONTH, 1) // следующий месяц
        }
        
        return targetDate
    }
    
    fun scheduleNextDayReminder() {
        // 🔧 НОВАЯ СИСТЕМА: ничего не делаем
        // Уведомления будут показываться при открытии приложения
    }
    
    private fun setExactAlarm(alarmManager: AlarmManager, triggerTime: Long, pendingIntent: PendingIntent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        } else {
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        }
    }
    
    fun cancelAllReminders() {
        // 🔧 НОВАЯ СИСТЕМА: просто отключаем
        Toast.makeText(context, "🔕 Напоминания выключены", Toast.LENGTH_SHORT).show()
    }
    
    fun wereReadingsSubmittedThisMonth(): Boolean {
        val historyItems = AppState.historyItems
        if (historyItems.isEmpty()) {
            return false
        }
        
        // Берем последнюю запись
        val latestItem = historyItems.first()
        val latestDateStr = latestItem.date // формат: "dd.MM.yyyy"
        
        try {
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
                return (latestMonth == currentMonth && latestYear == currentYear)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        return false
    }
    
    fun stopRemindersAndRescheduleNextMonth() {
        cancelAllReminders()
    }
}