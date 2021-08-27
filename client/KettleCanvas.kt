import java.awt.Font
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.font.FontRenderContext
import java.awt.font.TextLayout
import java.awt.geom.Rectangle2D
import javax.swing.JPanel

var currentRoom: String = ""
const val GLASS = 200

object KettleCanvas : JPanel() {
    fun getSelectedKettle(): Kettle = Kettle(0, "", 0, 0)

    private const val border = 20
    override fun paint(g: Graphics) {
        val optimums = nearKettles(currentRoom, GLASS)
        if (optimums.isEmpty()) return
        val currentTime = System.currentTimeMillis()
        val minBoilTime = optimums.minOf { (kettle, _) -> (kettle.boilTime - currentTime).toInt() / 1000 }
        val maxBoilTime = optimums.maxOf { (kettle, _) -> (kettle.boilTime - currentTime).toInt() / 1000 }
        val minDistance = optimums.minOf { (_, dist) -> dist }
        val maxDistance = optimums.maxOf { (_, dist) -> dist }

        drawAxes(g)
        drawScale(g, minDistance, maxDistance, minBoilTime, maxBoilTime)
    }

    private fun romanFont(size: Int) = Font("Times New Roman", Font.PLAIN, size)

    private fun drawScale(g: Graphics, minX: Int, maxX: Int, minY: Int, maxY: Int) {
        val longestText = listOf(minX, maxX, minY, maxY).map { it.toString() }.maxByOrNull { it.length }!!
        g.font = optimalFont(g, longestText)
        for (x in border until width - border step 2 * border) {
            val label = (minX + (maxX - minX).toDouble() * (x - border) / (width - 2 * border)).toInt().toString()
            g.drawString(label, x, height - border)
        }
    }

    private fun drawAxes(g: Graphics) {
        g.drawLine(border, border, border, height - border)
        g.drawLine(border * 1 / 2, border * 3 / 2, border, border)
        g.drawLine(border * 3 / 2, border * 3 / 2, border, border)
        g.drawLine(width - border, height - border, border, height - border)
        g.drawLine(width - border * 3 / 2, height - border * 3 / 2, width - border, height - border)
        g.drawLine(width - border * 3 / 2, height - border * 1 / 2, width - border, height - border)
    }


    private fun drawTextCentered(g: Graphics, x: Int, y: Int, text: String) {
        val bounds: Rectangle2D = TextLayout(text, g.font, (g as Graphics2D).fontRenderContext).bounds
        g.drawString(text, x - (bounds.width / 2).toInt(), y + (bounds.height / 2).toInt())
    }

    private fun optimalFont(g: Graphics, text: String): Font {
        val context: FontRenderContext = (g as Graphics2D).fontRenderContext

        var lowFontSize = 1
        var highFontSize = 40

        while (highFontSize - lowFontSize > 1) {
            val middle = (lowFontSize + highFontSize) / 2
            val f = romanFont(middle)

            val bounds = TextLayout(text, f, context).bounds
            if (bounds.height > border || bounds.width > border) {
                highFontSize = middle
                continue
            }

            lowFontSize = middle
        }

        return romanFont(lowFontSize)
    }
}