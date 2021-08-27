import java.awt.Graphics
import javax.swing.JPanel

var currentRoom: String = ""
const val GLASS = 200

object KettleCanvas : JPanel() {
    fun getSelectedKettle(): Kettle = Kettle(0, "", 0, 0)

    val border = 20
    override fun paint(g: Graphics) {
        val optimums = nearKettles(currentRoom, GLASS)
        if (optimums.isEmpty()) return
        val minBoilTime = optimums.minOf { (kettle, _) -> kettle.boilTime }
        val maxBoilTime = optimums.maxOf { (kettle, _) -> kettle.boilTime }
        val minDistance = optimums.minOf { (_, dist) -> dist }
        val maxDistance = optimums.maxOf { (_, dist) -> dist }

    }
}