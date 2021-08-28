import java.awt.*
import java.awt.font.FontRenderContext
import java.awt.font.TextLayout
import java.awt.geom.Rectangle2D
import javax.swing.JPanel

var currentRoom: String = ""
const val GLASS = 200

private fun <V> Iterable<V>.rangeOf(selector: (V) -> Int) = minOf(selector)..maxOf(selector)

private fun IntRange.split(parts: Int): List<Int> {
    val length = endInclusive - start
    return (this step (length + parts - 1) / parts).toList()
}

object KettleCanvas : JPanel() {
    fun getSelectedKettle(): Kettle = Kettle(0, "", 0, 0)

    private const val border = 20
    override fun paint(g: Graphics) {
        val currentTime = System.currentTimeMillis()
        val optimums = nearKettles(currentRoom, GLASS, currentTime)
        if (optimums.isEmpty()) return
        val distanceRange = optimums.rangeOf { (_, dist) -> dist }
        val boilRange = optimums.rangeOf { (kettle, _) -> (kettle.boilTime - currentTime).toInt() / 1000 }

        drawAxes(g)
        val (xRange, yRange) = drawScale(g, distanceRange, boilRange)
        drawPoints(g, optimums, currentTime, distanceRange, boilRange)
    }

    private fun drawPoints(
        g: Graphics, optimums: List<Pair<Kettle, Int>>,
        currentTime: Long, xRange: IntRange, yRange: IntRange
    ): List<Pair<Kettle, Point>> {
        g.color = Color.BLUE
        val res = mutableListOf<Pair<Kettle, Point>>()
        for ((kettle, dist) in optimums) {
            val x = getCoordinate(xRange, dist, width)
            val boilTime = (kettle.boilTime - currentTime).toInt() / 1000
            val y = getCoordinate(yRange, yRange.first + yRange.last - boilTime, height)
            g.drawOval(x - 2, y - 2, 4, 4)
            res.add(kettle to Point(x, y))
        }
        return res
    }

    private fun romanFont(size: Int) = Font("Times New Roman", Font.PLAIN, size)

    private fun drawScale(g: Graphics, xRange: IntRange, yRange: IntRange): Pair<List<Int>, List<Int>> {
        g.color = Color.ORANGE
        val freq = 2
        val xLabels = xRange.split(parts = (width - freq * border) / freq * border)
        val yLabels = yRange.split(parts = (height - freq * border) / freq * border)
        val longestText = (xLabels + yLabels).map { it.toString() }.maxByOrNull { it.length }!!
        g.font = optimalFont(g, longestText)
        for ((index, label) in xLabels.map { it.toString() }.withIndex()) {
            val x = getCoordinate(xLabels, freq, index, width)
            drawTextCentered(g, label, x, height - border / 2)
        }
        for ((index, label) in yLabels.map { it.toString() }.withIndex()) {
            val y = getCoordinate(yLabels, freq, yLabels.lastIndex - index, height)
            drawTextCentered(g, label, border / 2, y)
        }
        return xLabels to yLabels
    }

    private fun getCoordinate(range: IntRange, value: Int, length: Int): Int {
        return if (range.first == range.last) {
            (length - border) / 2
        } else {
            border + (value - range.first) * (length - 2 * border) / (range.last - range.first)
        }
    }

    private fun getCoordinate(coordinates: List<Int>, freq: Int, index: Int, length: Int): Int {
        return if (coordinates.size == 1) {
            (length - border) / 2
        } else {
            (length - freq * border) / (coordinates.size - 1) * index
        }
    }

    private fun drawAxes(g: Graphics) {
        g.color = Color.BLACK
        g.drawLine(border, border, border, height - border)
        g.drawLine(border * 1 / 2, border * 3 / 2, border, border)
        g.drawLine(border * 3 / 2, border * 3 / 2, border, border)
        g.drawLine(width - border, height - border, border, height - border)
        g.drawLine(width - border * 3 / 2, height - border * 3 / 2, width - border, height - border)
        g.drawLine(width - border * 3 / 2, height - border * 1 / 2, width - border, height - border)
    }

    private fun drawTextCentered(g: Graphics, text: String, x: Int, y: Int) {
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