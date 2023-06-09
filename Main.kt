package tasklist

import kotlinx.datetime.*

val taskList = mapOf(
    "C" to mutableListOf<Pair<LocalDateTime, MutableList<String>>>(),
    "H" to mutableListOf<Pair<LocalDateTime, MutableList<String>>>(),
    "N" to mutableListOf<Pair<LocalDateTime, MutableList<String>>>(),
    "L" to mutableListOf<Pair<LocalDateTime, MutableList<String>>>()
) //sorted by priority

val taskListStage = mutableListOf<Pair<String, MutableList<String>>>()

fun main() {
    val task = Task() // Create an instance of the Task class
    while (true) {
        println("Input an action (add, print, edit, delete, end):")
        when (readln().lowercase()) {
            "add" -> task.addTask()
            "print" -> task.taskPrintStage()
            "edit" -> task.edit()
            "delete" -> task.delete()
            "end" -> {
                println("Tasklist exiting!")
                break
            }
            else -> println("The input action is invalid")
        }
    }
}

class Task {

    class Create{
        fun priority():String{
            while (true) {
                println("Input the task priority (C, H, N, L):")
                when (val priority = readln().uppercase()) {
                    "C", "H", "N", "L" -> return priority
                    else -> {}
                }
            }

        }

        fun date():Triple<Int, Int, Int>{
            while (true) {
                println("Input the date (yyyy-mm-dd):")
                val date = readln().split("-")
                if (date.size != 3) {
                    println("The input date is invalid")
                    continue
                }
                if (checkTime(date[0], date[1], date[2], "0", "0")) {
                    return Triple(date[0].toInt(), date[1].toInt(), date[2].toInt())
                }
                else println("The input date is invalid")


            }
        }

        fun time():Pair<Int, Int> {
            while (true) {
                println("Input the time (hh:mm):")
                val time = readln().split(":")
                if (time.size != 2) {
                    println("The input time is invalid")
                    continue
                }
                if (checkTime("2022", "2", "11", time[0], time[1])) {
                    return Pair(time[0].toInt(), time[1].toInt())
                }
                else println("The input time is invalid")
            }


        }

        fun newTask(): MutableList<String>{
            val newTask = mutableListOf<String>()
            println("Input a new task (enter a blank line to end):")
            while (true) {
                val task = readln().trim()
                if (task.isEmpty()) return newTask
                newTask.add(task)
            }

        }



        private fun checkTime(years: String, months: String, days: String, hours: String, minutes: String): Boolean {
            return try {
                LocalDateTime(years.toInt(), months.toInt(), days.toInt(), hours.toInt(), minutes.toInt())
                true
            } catch (e: Exception) {
                false
            }
        }


    }

    fun edit(){
        if (taskListStage.size == 0) {
            println("No tasks have been input")
            return
        }
        taskPrintStage()
        while (true) {
            println("Input the task number (1-${taskListStage.size}):")
            try {
                while (true) {
                    val index = readln().toInt() - 1
                    val editTask = taskListStage[index]
                    var priority = editTask.first.split("|")[1]
                    var (years, months, days) = editTask.first.split("T")[0].split("-").map { it.toInt() }
                    var (hours, minutes) = editTask.first.split("T")[1].split("|")[0].split(":").map { it.toInt() }
                    var newTask = editTask.second
                    while (true) {
                        println("Input a field to edit (priority, date, time, task):")
                        when (readln()) {
                            "priority" -> {
                                priority = Create().priority()
                                break
                            }

                            "date" -> {
                                val (years1, months1, days1) = Create().date()
                                years = years1
                                months = months1
                                days = days1
                                break
                            }

                            "time" -> {
                                val (hours1, minutes1) = Create().time()
                                hours = hours1
                                minutes = minutes1
                                break
                            }

                            "task" -> {
                                newTask = Create().newTask()
                                break
                            }

                            else -> {
                                println("Invalid field")
                            }
                        }
                    }
                    val dateTime = LocalDateTime(years, months, days, hours, minutes)
                    taskListStage[index] = Pair(("$dateTime|$priority"), newTask)
                    println("The task is changed")
                    return
                }
            }catch (e: Exception) {
                println("Invalid task number")
            }
        }
    }


    fun delete(){
        if (taskListStage.size == 0) {
            println("No tasks have been input")
            return
        }
        taskPrintStage()
        while (true) {
            println("Input the task number (1-${taskListStage.size}):")
            try {
                taskListStage.removeAt(readln().toInt() - 1)
                println("The task is deleted")
                return
            } catch (e: Exception) { println("Invalid task number") }
        }

    }

    fun addTask() {
        val priority = Create().priority()
        val (years, months, days)  = Create().date()
        val (hours, minutes) = Create().time()
        val dateTime = LocalDateTime(years, months, days, hours, minutes)
        val newTask = Create().newTask()
        if (newTask.isEmpty()) {
            println("The task is blank")
            return
        }
        taskList[priority]!!.add(Pair(dateTime, newTask))
        taskListStage.add(Pair(("$dateTime|$priority"), newTask))
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

    private fun checkTimeTag(input: String): String{
        val time = input.split("|")[0].toLocalDateTime().date
        val localTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val dayDifference = time.daysUntil(localTime)
        return when {
            dayDifference == 0 -> "T"
            dayDifference >= 1 -> "O"
            else -> "I"

        }
    }

    fun taskPrintStage() {
        if (taskListStage.isNotEmpty()) {
            for ((i, task) in taskListStage.withIndex()) {
                val taskNumber = i + 1
                val taskName = task.first.replace("T", " ").replace("|", " ") //first element
                val timeTag = checkTimeTag(task.first)
                println("$taskNumber ${if (i < 9) " " else ""}$taskName $timeTag")
                for (part in  task.second) {
                    println("   $part")
                }
                println()
            }
        } else {
            println("No tasks have been input")
            println()
        }
    }
}






