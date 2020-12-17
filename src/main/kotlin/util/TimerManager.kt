package com.yumi.kotlin.util

import com.yumi.kotlin.actions.RecallAction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

object TimerManager {

    private const val PERIOD_DAY = 24 * 60 * 60 * 1000L

    suspend fun run() {
        withContext(Dispatchers.IO) {
            val calendar = Calendar.getInstance()
            calendar[Calendar.HOUR_OF_DAY] = 2 // 凌晨 2 点
            calendar[Calendar.MINUTE] = 0
            calendar[Calendar.SECOND] = 0
            var date: Date = calendar.time //第一次执行定时任务的时间
            if (date.before(Date())) {
                date = addDay(date)
            }
            val timer = Timer()
            val task = CleanTask()
            timer.schedule(task, date, PERIOD_DAY)
        }
    }

    // 增加或减少天数
    private fun addDay(date: Date?, num: Int = 1): Date {
        val startDT = Calendar.getInstance()
        startDT.time = date
        startDT.add(Calendar.DAY_OF_MONTH, num)
        return startDT.time
    }
}

private class CleanTask : TimerTask() {
    override fun run() {
        RecallAction.resetMembersFile()
    }
}