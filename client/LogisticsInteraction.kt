//Interaction with Leo's block
data class Kettle(val room: String, val id: Int, var boilTime: Long, var ml: Int)

fun getNearKettles(room: String, volume: Int): List<Int> = listOf()
fun updateAllKettles(): List<Kettle> = listOf()
fun addKettle(room: String) = Kettle("", 1, 1L, 1)
fun removeKettle(id: Int) = Unit
fun boilKettle(id: Int, volume: Int) = Unit
fun drink(id: Int, volumeRemaining: Int) = Unit

var room = ""