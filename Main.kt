package tasklist

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.datetime.*
import java.io.File




var taskListStage = mutableListOf<Pair<String, MutableList<String>>>()

fun main() {
    try {
        taskListStage = Json().loadTaskList()
    } catch (_: Exception) {}
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
                Json().saveTaskListToFile()
                break
            }
            else -> println("The input action is invalid")
        }
    }
}

class Task {

    class Create {
        fun priority(): String {
            while (true) {
                println("Input the task priority (C, H, N, L):")
                when (val priority = readln().uppercase()) {
                    "C", "H", "N", "L" -> return priority
                    else -> {}
                }
            }

        }

        fun date(): Triple<Int, Int, Int> {
            while (true) {
                println("Input the date (yyyy-mm-dd):")
                val date = readln().split("-")
                if (date.size != 3) {
                    println("The input date is invalid")
                    continue
                }
                if (checkTime(date[0], date[1], date[2], "0", "0")) {
                    return Triple(date[0].toInt(), date[1].toInt(), date[2].toInt())
                } else println("The input date is invalid")


            }
        }

        fun time(): Pair<Int, Int> {
            while (true) {
                println("Input the time (hh:mm):")
                val time = readln().split(":")
                if (time.size != 2) {
                    println("The input time is invalid")
                    continue
                }
                if (checkTime("2022", "2", "11", time[0], time[1])) {
                    return Pair(time[0].toInt(), time[1].toInt())
                } else println("The input time is invalid")
            }


        }

        fun newTask(): MutableList<String> {
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

    fun edit() {
        if (taskListStage.isEmpty()) {
            println("No tasks have been input")
            return
        }

        taskPrintStage()

        while (true) {
            println("Input the task number (1-${taskListStage.size}):")
            try {
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
                taskListStage[index] = Pair("$dateTime|$priority", newTask)
                println("The task is changed")
                return

            } catch (e: Exception) {
                println("Invalid task number")
            }
        }
    }


    fun delete() {
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
            } catch (e: Exception) {
                println("Invalid task number")
            }
        }

    }

    fun addTask() {
        val priority = Create().priority()
        val (years, months, days) = Create().date()
        val (hours, minutes) = Create().time()
        val dateTime = LocalDateTime(years, months, days, hours, minutes)
        val newTask = Create().newTask()
        if (newTask.isEmpty()) {
            println("The task is blank")
            return
        }
        taskListStage.add(Pair(("$dateTime|$priority"), newTask))
    }


    private fun checkTimeTag(input: String): String {
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
            val line =
                "+${"-".repeat(4)}+${"-".repeat(12)}+${"-".repeat(7)}+${"-".repeat(3)}+${"-".repeat(3)}+${"-".repeat(44)}+"
            println(line)
            println("| N  |    Date    | Time  | P | D |                   Task                     |")
            println(line)

            for ((i, task) in taskListStage.withIndex()) {
                val taskNum = i + 1
                val data = task.first.split("T")[0]
                val due = checkTimeTag(task.first)
                val dueTag = colorTag(due)
                val time = task.first.split("T")[1].split("|")[0]
                val priority = task.first.split("T")[1].split("|")[1]
                val priorityTag = colorTag(priority)
                val formatTask = formatTask(task.second)

                print("| ")
                if (taskNum < 9) print("$taskNum  |") else print("$taskNum |") //number task
                print(" $data |")
                print(" $time |")
                print(" $priorityTag |")
                print(" $dueTag |")
                println("${formatTask[0]}|")

                if (formatTask.size != 1) {
                    for (t in 1 until formatTask.size) {
                        print("|${" ".repeat(4)}|")
                        print("${" ".repeat(12)}|")
                        print("${" ".repeat(7)}|")
                        print("${" ".repeat(3)}|")
                        print("${" ".repeat(3)}|")
                        println("${formatTask[t]}|")
                    }
                }
                println(line)
            }
            println()
        } else {
            println("No tasks have been input")
            println()
        }
    }

    private fun colorTag(input: String): String {
        return when (input) {
            "C", "O" -> "\u001B[101m \u001B[0m"
            "H", "T" -> "\u001B[103m \u001B[0m"
            "N", "I" -> "\u001B[102m \u001B[0m"
            else -> "\u001B[104m \u001B[0m"
        }
    }

    private fun formatTask(input: MutableList<String>): List<String> {
        return input.flatMap { task ->
            val chunks = task.chunked(44) //split string to list if size is more than 44
            chunks.map { chunk -> chunk.padEnd(44) }//change length string to 44
        }

    }


}

class Json {
    @JsonClass(generateAdapter = true)
    data class TaskEntry(val key: String, val value: MutableList<String>)


    private val fileLocation = "tasklist.json"
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    fun saveTaskListToFile() {
        val adapter: JsonAdapter<List<TaskEntry>> = moshi.adapter(
            Types.newParameterizedType(List::class.java, TaskEntry::class.java)
        )
        val taskEntries = taskListStage.map { TaskEntry(it.first, it.second) }
        val json = adapter.toJson(taskEntries)
        File(fileLocation).writeText(json)
    }

    fun loadTaskList(): MutableList<Pair<String, MutableList<String>>> {
        val adapter: JsonAdapter<List<TaskEntry>> = moshi.adapter(
            Types.newParameterizedType(List::class.java, TaskEntry::class.java)
        )
        val json = File(fileLocation).readText()
        val taskEntries = adapter.fromJson(json) ?: emptyList()
        return taskEntries.map { it.key to it.value }.toMutableList()
    }
}






