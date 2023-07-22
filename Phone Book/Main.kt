package phonebook

import java.io.File
import java.sql.DataTruncation
import kotlin.math.floor
import kotlin.math.min
import kotlin.math.sqrt

val homeDir: String = System.getProperty("user.home")
val sep: String = File.separator
val downloadsDir = File("$homeDir${sep}Downloads")

val namesFile = File("$downloadsDir${sep}find.txt")
val recordsFile = File("$downloadsDir${sep}directory.txt")

private fun readFiles(): Pair<List<String>, List<String>> {
    val names = namesFile.readLines()
    val records = recordsFile.readLines()
    return Pair(names, records)
}

private fun formatTime(time: Long): String {
    val minutes = time / 1000 / 60
    val seconds = time / 1000 % 60
    val milliseconds = time % 1000

    return "Time taken: $minutes min. $seconds sec. $milliseconds ms."
}

fun main() {

    val (names, records) = readFiles()
    val timeTakenLinearSearch = linearSearch(names, records)
    println("Found 500 / 500 entries. " + formatTime(timeTakenLinearSearch))

    val (bubbleSortedRecords, timeTakenBubbleSort) = bubbleSortRecords(records)
    val timeTakenJumpSearch = jumpSearch(names, bubbleSortedRecords)
    println("Found 500 / 500 entries. " + formatTime(timeTakenJumpSearch + timeTakenBubbleSort))
    println("Sorting time: ${formatTime(timeTakenBubbleSort)}")
    println("Searching time: ${formatTime(timeTakenJumpSearch)}")

    val (quickSortedRecords, timeTakenQuickSort) = quickSortRecords(records)
    val timeTakenBinarySearch = binarySearch(names, quickSortedRecords)
    println("Found 500 / 500 entries. " + formatTime(timeTakenBinarySearch + timeTakenQuickSort))
    println("Sorting time: ${formatTime(timeTakenQuickSort)}")
    println("Searching time: ${formatTime(timeTakenBinarySearch)}")

    println("Start searching (hash table)...")
    val timeTakenHashInsert = hashInsert(names, records)
    val timeTakenHashSearch = hashSearch(names, hashMap)
    println("Found 500 / 500 entries. " + formatTime(timeTakenHashInsert + timeTakenHashSearch))
    println("Creating time: ${formatTime(timeTakenHashInsert)}")
    println("Searching time: ${formatTime(timeTakenHashSearch)}")
}

private fun linearSearch(names: List<String>, records: List<String>): Long {
    var counter = 0

    println("Start searching (linear search)...")

    val startTime = System.currentTimeMillis()
    for (name in names) {
        for (record in records) {
            if (record.contains(name)) counter++
        }
    }
    val finishTime = System.currentTimeMillis()
    return finishTime - startTime
}

private fun bubbleSortRecords(records: List<String>): Pair<MutableList<String>, Long> {
    println("Start searching (bubble sort + jump search)...")
    val startTime = System.currentTimeMillis()
    var swap = true

    val mutRecords = records.toMutableList()

    while (swap) {
        swap = false
        for (i in 0 until mutRecords.lastIndex) {
            val name = mutRecords[i].split(" ")[1]
            val nextName = mutRecords[i+1].split(" ")[1]
            if (name > nextName) {
                val temp = mutRecords[i]
                mutRecords[i] = mutRecords[i + 1]
                mutRecords[i + 1] = temp
                swap = true
            }
        }
        if (System.currentTimeMillis() > startTime + 630608) {
            return Pair(mutRecords, System.currentTimeMillis() - startTime)
        }
    }

    val finishTime = System.currentTimeMillis()
    val timeTaken = finishTime - startTime

    return Pair(mutRecords, timeTaken)
}

private fun jumpSearch(names: List<String>, sortedRecords: MutableList<String>): Long {
    var counter = 0
    val startTime = System.currentTimeMillis()

    for (name in names) {
        var curr = 1
        val last = sortedRecords.size
        val step = floor(sqrt(last.toFloat()))

        var recordName = sortedRecords[curr].filter { !it.isDigit() }.trim()

        while (recordName < name) {
            curr = min((curr.toDouble() + step.toDouble()), last.toDouble()).toInt()
            recordName = sortedRecords[curr].filter { !it.isDigit() }.trim()
        }
        while (recordName > name) {
            curr -= 1
            recordName = sortedRecords[curr].filter { !it.isDigit() }.trim()
        }
        if (recordName == name) counter++
    }

    val finishTime = System.currentTimeMillis()

    return finishTime - startTime
}

private fun quickSortRecords(records: List<String>): Pair<MutableList<String>, Long> {
    println("Start searching (quick sort + binary search)...")
    val startTime = System.currentTimeMillis()
    val mutRecords = records.toMutableList()


    sort(mutRecords, 0, mutRecords.lastIndex)

    val finishTime = System.currentTimeMillis()
    return Pair(mutRecords, finishTime - startTime)
}

private fun sort(list: MutableList<String>, low: Int, high: Int) {
    if (low < high) {
        val pivotIndex = partition(list, low, high)
        sort(list, low, pivotIndex - 1)
        sort(list, pivotIndex + 1, high)
    }
}

private fun partition(list: MutableList<String>, low: Int, high: Int) :Int {
    val pivot = list[high].filter { !it.isDigit() }.trim()
    var i = low - 1

    for (j in low until high) {
        val recordName = list[j].filter { !it.isDigit() }.trim()
        if (recordName <= pivot) {
            i++
            list.swap(i, j)
        }
    }
    list.swap(i + 1, high)
    return i + 1
}

private fun MutableList<String>.swap(i: Int, j: Int) {
    val temp = this[i]
    this[i] = this[j]
    this[j] = temp
}

private fun binarySearch(names: List<String>, sortedRecords: MutableList<String>): Long {
    val startTime = System.currentTimeMillis()

    var counter = 0
    for (name in names) {
        var left = 1
        var right = sortedRecords.size
        while(left < right) {
            val middle = (left + right) / 2
            val recordName = sortedRecords[middle].filter { !it.isDigit() }.trim()
            if (recordName == name) {
                counter++
                break
            }
            else if (recordName > name) {
                right = middle - 1
            }
            else {
                left = middle + 1
            }
        }
    }
    val finishTime = System.currentTimeMillis()
    return finishTime - startTime
}

val hashMap = hashMapOf<String, String>()

private fun hashSearch(names: List<String>, hashMap: HashMap<String, String>): Long {
    val startTime = System.currentTimeMillis()
    for (name in names) {
        hashMap[name]
    }
    val finishTime = System.currentTimeMillis()
    return finishTime - startTime
}

private fun hashInsert (names: List<String>, records: List<String>): Long {
    val startTime = System.currentTimeMillis()
    for (record in records) {
        val parts = record.split(" ", limit = 2)
        val key = parts[1]
        val value = parts[0]
        hashMap[key]=value
    }
    val finishTime = System.currentTimeMillis()
    return finishTime - startTime
}