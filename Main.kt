package tasklist

import kotlinx.datetime.*

val taskList = mapOf(
    "C" to mutableListOf<Pair<LocalDateTime, MutableList<String>>>(),
    "H" to mutableListOf<Pair<LocalDateTime, MutableList<String>>>(),
    "N" to mutableListOf<Pair<LocalDateTime, MutableList<String>>>(),
    "L" to mutableListOf<Pair<LocalDateTime, MutableList<String>>>()
)

fun main() {
    val task = Task() // Create an instance of the Task class
    while (true) {
        println("Input an action (add, print, end):")
        when (readln()) {
            "add" -> task.addTask()
            "print" -> task.taskPrint()
            "end" -> {
                println("Tasklist exiting!")
                break
            }
            else -> println("The input action is invalid")
        }
    }
}

class Task {
    private fun checkTime(years: String, months: String, days: String, hours: String, minutes: String): Boolean {
        return try {
            LocalDateTime(years.toInt(), months.toInt(), days.toInt(), hours.toInt(), minutes.toInt())
            true
        } catch (e: Exception) {
            false
        }
    }

    fun addTask() {
        var priority: String
        var years: String
        var months: String
        var days: String
        var hours: String
        var minutes: String

        while (true) {
            println("Input the task priority (C, H, N, L):")
            priority = readln().uppercase()
            when (priority) {
                "C", "H", "N", "L" -> break
                else -> {}
            }
        }

        while (true) {
            println("Input the date (yyyy-mm-dd):")
            val date = readln().split("-")
            if (date.size != 3) {
                println("The input date is invalid")
                continue
            }
            years = date[0]
            months = date[1]
            days = date[2]
            if (checkTime(years, months, days, "0", "0")) break else println("The input date is invalid")
        }

        while (true) {
            println("Input the time (hh:mm):")
            val time = readln().split(":")
            if (time.size != 2) {
                println("The input time is invalid")
                continue
            }
            hours = time[0]
            minutes = time[1]

            if (checkTime(years, months, days, hours, minutes)) break else println("The input time is invalid")
        }
        val dateTime = LocalDateTime(years.toInt(), months.toInt(), days.toInt(), hours.toInt(), minutes.toInt())

        val newTask = mutableListOf<String>()
        println("Input a new task (enter a blank line to end):")
        while (true) {
            val task = readln().trim()
            if (task.isEmpty()) break
            newTask.add(task)
        }
        if (newTask.isEmpty()) {
            println("The task is blank")
            return
        }
        taskList[priority]!!.add(Pair(dateTime, newTask))
    }

        fun taskPrint() {
            if (taskList.all { (_, pairList) -> pairList.isEmpty() }) {
                println("No tasks have been input")
                return
            }
            var count = 0
            for (priority in taskList) {
                if (priority.key.isEmpty()) continue

                for (i in taskList[priority.key]!!) {
                    count++
                    val formatDataTime = i.first.toString().replace("T", " ")
                    println("$count ${if (count <= 9) " " else ""}$formatDataTime ${priority.key} ")
                    val task = i.second
                    for (part in task) {
                        println("   $part")
                    }
                    println()
                }
            }
        }
    }





