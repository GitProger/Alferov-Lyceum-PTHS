import java.io.InputStreamReader
import java.net.URL
import java.util.*
import kotlin.math.absoluteValue

data class Kettle(val id: Int, val room: String, var boilTime: Long, var ml: Int)

var room = ""
var ml = 200

private const val MILLIES_IN_DAY = 86_400_000
private const val start = "http://192.168.43.217:1000/"

private fun query(query: String): List<String> {
    val url = URL(start + query)
    return InputStreamReader(url.openConnection().getInputStream()).readLines()
}

private fun ask() = query("ask.php").map { it.toKettle() }
private fun update(id: Int, boilTime: Long, ml: Int) = query("boil.php?id=$id&boil_time=$boilTime&ml=$ml")
fun add(room: String) = query("add.php?room=$room").first().toInt()
fun delete(id: Int) = query("remove.php?id=$id")
fun byId(id: Int) = query("by_id.php?id=$id").first().toKettle()

var graph: TreeMap<String, TreeMap<String, Int>>? = null
private fun getMap() {
    if (graph == null) {
        graph = TreeMap<String, TreeMap<String, Int>>()
        val query = query("map.php")
        query.forEach {
            val (u, v, dist) = it.split(' ')
            graph!![u] = graph!!.getOrDefault(u, TreeMap<String, Int>())
            graph!![v] = graph!!.getOrDefault(v, TreeMap<String, Int>())
            graph!![u]!![v] = dist.toInt()
            graph!![v]!![u] = dist.toInt()
        }
    }
}

fun String.toKettle(): Kettle {
    val (id, room, boilTime, ml) = split(' ')
    return Kettle(id.toInt(), room, boilTime.toLong(), ml.toInt())
}

fun updateAllKettles() = ask()

fun nearKettles(currentRoom: String, ml: Int, currentTime: Long): List<Pair<Kettle, Int>> {
    getMap()
    val distance = TreeMap<String, Int>() //distance from currentRoom, calculated using Dijkstra algorithm
    distance[currentRoom] = 0
    val dijkstra = TreeSet<String> { s1, s2 ->
        distance.getOrDefault(s1, Int.MAX_VALUE) - distance.getOrDefault(s2, Int.MAX_VALUE)
    }
    dijkstra.add(currentRoom)
    while (dijkstra.isNotEmpty()) {
        val cur = dijkstra.first() //nearest that's not processed yet
        dijkstra.remove(cur)
        for ((next, dist) in (graph!![cur] ?: TreeMap<String, Int>())) {
            if (distance.getOrDefault(next, Int.MAX_VALUE) > distance[cur]!! + dist) {
                dijkstra.remove(next)
                distance[next] = distance[cur]!! + dist
                dijkstra.add(next)
            }
        }
    }
    val candidates = ask().filter { it.ml >= ml }
        .sortedWith(compareByDescending<Kettle> { it.boilTime.coerceAtMost(currentTime) }.thenBy { distance[it.room]!! })
    var currentBestDistance = Int.MAX_VALUE
    val optimums = mutableListOf<Pair<Kettle, Int>>()
    for (kettle in candidates) {
        if (currentBestDistance > distance.getOrDefault(kettle.room, Int.MAX_VALUE)) {
            optimums.add(kettle to distance[kettle.room]!!)
            currentBestDistance = distance[kettle.room]!!
        }
    }
    return optimums.filter { (kettle, _) -> (kettle.boilTime - System.currentTimeMillis()).absoluteValue < MILLIES_IN_DAY }
}

fun boilKettle(id: Int, volume: Int) = update(id, System.currentTimeMillis() + 90000L, volume)
fun drink(id: Int, volumeRemaining: Int) = update(id, byId(id).boilTime, volumeRemaining)
