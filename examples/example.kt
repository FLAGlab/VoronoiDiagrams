fun mergeSort(list: List<Int>): List<Int> {
    if (list.size <= 1) {
        return list
    }

    val left = mutableListOf<Int>()
    val right = mutableListOf<Int>()

    val middle = list.size / 2
    list.forEachIndexed { index, number ->
        if (index < middle) {
            left.add(number)
        } else {
            right.add(number)
        }
    }

    fun merge(left: List<Int>, right: List<Int>): List<Int> = mutableListOf<Int>().apply {
        var indexLeft = 0
        var indexRight = 0

        while (indexLeft < left.size && indexRight < right.size) {
            if (left[indexLeft] <= right[indexRight]) {
                add(left[indexLeft])
                indexLeft++
            } else {
                add(right[indexRight])
                indexRight++
            }
        }

        while (indexLeft < left.size) {
            add(left[indexLeft])
            indexLeft++
        }

        while (indexRight < right.size) {
            add(right[indexRight])
            indexRight++
        }
    }

    return merge(mergeSort(left), mergeSort(right))
}

fun stoogeSort(a: IntArray, i: Int, j: Int) {
    if (a[j] < a[i]) {
        val temp = a[j]
        a[j] = a[i]
        a[i] = temp
    }
    if (j - i > 1) {
        val t = (j - i + 1) / 3
        stoogeSort(a, i, j - t)
        stoogeSort(a, i + t, j)
        stoogeSort(a, i, j - t)
    }
}

val s = "top1, top2, ip1, ip2, ip3, ip1a, ip2a, ip2b, ip2c, ipcommon, des1, " +
        "des1a, des1b, des1c, des1a1, des1a2, des1c1, extra1"

val deps = mutableListOf(
    0 to 10, 0 to 2, 0 to 3,
    1 to 10, 1 to 3, 1 to 4,
    2 to 17, 2 to 5, 2 to 9,
    3 to 6, 3 to 7, 3 to 8, 3 to 9,
    10 to 11, 10 to 12, 10 to 13,
    11 to 14, 11 to 15,
    13 to 16, 13 to 17
)

val files = listOf("top1", "top2", "ip1")

class Graph(s: String, edges: List<Pair<Int, Int>>) {

    val vertices = s.split(", ")
    val numVertices = vertices.size
    val adjacency = List(numVertices) { BooleanArray(numVertices) }

    init {
        for (edge in edges) adjacency[edge.first][edge.second] = true
    }

    fun topLevels(): List<String> {
        val result = mutableListOf<String>()
        // look for empty columns
        outer@ for (c in 0 until numVertices) {
            for (r in 0 until numVertices) {
                if (adjacency[r][c]) continue@outer
            }
            result.add(vertices[c])
        }
        return result
    }

    fun compileOrder(item: String): List<String> {
        val result = LinkedList<String>()
        val queue  = LinkedList<Int>()
        queue.add(vertices.indexOf(item))
        while (!queue.isEmpty()) {
            val r = queue.poll()
            for (c in 0 until numVertices) {
                if (adjacency[r][c] && !queue.contains(c)) queue.add(c)
            }
            result.addFirst(vertices[r])
        }
        return result.distinct().toList()
    }
}

const val MAX = Int.MAX_VALUE
val rand = java.util.Random()

fun partition(list:IntArray, left: Int, right:Int, pivotIndex: Int): Int {
    val pivotValue = list[pivotIndex]
    list[pivotIndex] = list[right]
    list[right] = pivotValue
    var storeIndex = left
    for (i in left until right) {
        if (list[i] < pivotValue) {
            val tmp = list[storeIndex]
            list[storeIndex] = list[i]
            list[i] = tmp
            storeIndex++
        }
    }
    val temp = list[right]
    list[right] = list[storeIndex]
    list[storeIndex] = temp
    return storeIndex
}

tailrec fun quickSelect(list: IntArray, left: Int, right: Int, k: Int): Int {
    if (left == right) return list[left]
    var pivotIndex = left + Math.floor((rand.nextInt(MAX) % (right - left + 1)).toDouble()).toInt()
    pivotIndex = partition(list, left, right, pivotIndex)
    if (k == pivotIndex)
        return list[k]
    else if (k < pivotIndex)
        return quickSelect(list, left, pivotIndex - 1, k)
    else
        return quickSelect(list, pivotIndex + 1, right, k)
}

fun orderDisjointList(m: String, n: String): String {
    val nList = n.split(' ')
    // first replace the first occurrence of items of 'n' in 'm' with the NULL character 
    // which we can safely assume won't occur in 'm' naturally
    var p = m
    for (item in nList) p = p.replaceFirst(item, NULL)
    // now successively replace the NULLs with items from nList 
    val mList = p.split(NULL)
    val sb = StringBuilder()
    for (i in 0 until nList.size) sb.append(mList[i], nList[i])       
    return sb.append(mList.last()).toString()
}

fun main(args: Array<String>) {
    val numbers = listOf(5, 2, 3, 17, 12, 1, 8, 3, 4, 9, 7)
    println("Unsorted: $numbers")
    println("Merge: ${mergeSort(numbers)}")
    println("Stooge: ${stoogeSort(numbers)}")
    val g = Graph(s, deps)
    println("Top levels:  ${g.topLevels()}")
    for (f in files) println("\nCompile order for $f: ${g.compileOrder(f)}")
    val right = numbers.size - 1
    for (k in 0..9) {
        print(quickSelect(numbers, 0, right, k))
        if (k < 9) print(", ")
    }
    val m = arrayOf(
        "the cat sat on the mat",
        "the cat sat on the mat",
        "A B C A B C A B C",
        "A B C A B D A B E",
        "A B",
        "A B", 
        "A B B A"
    ) 
    val n = arrayOf(
        "mat cat",
        "cat mat",
        "C A C A",
        "E A D A",
        "B",
        "B A",
        "B A"
    )
    for (i in 0 until m.size) 
        println("${m[i].padEnd(22)}  ->  ${n[i].padEnd(7)}  ->  ${orderDisjointList(m[i], n[i])}")
}