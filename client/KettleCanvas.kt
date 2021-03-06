import java.awt.*
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.awt.font.FontRenderContext
import java.awt.font.TextLayout
import java.awt.geom.Rectangle2D
import javax.swing.JPanel
import kotlin.math.abs

const val GLASS = 200

fun extended(r: IntRange) = r.first - 1..r.last + 1

private fun <V> Iterable<V>.rangeOf(selector: (V) -> Int) = minOf(selector)..maxOf(selector)

private fun IntRange.split(parts: Int): List<Int> {
    val length = endInclusive - start
    val step = (length + parts - 1) / parts
    if (step <= 0) return listOf()
    return (this step step).toList()
}

object KettleCanvas : JPanel() {
    private var kettleList: List<Pair<Kettle, Point>> = listOf()
    private var optimums: List<Pair<Kettle, Int>> = listOf()
    private var selectedKettle: Kettle? = null
    private var pt: Point? = null
    fun getSelectedKettle() = selectedKettle
    private val mouseListener: MouseListener = object : MouseListener {
        override fun mouseClicked(e: MouseEvent) = e.run {
            selectedKettle = kettleList.minByOrNull { (k, p) -> abs(e.x - p.x) + abs(e.y - p.y) }?.first
            val g = graphics

            if (pt != null) {
                g.color = Color.BLUE
                g.fillOval(pt!!.x - 2, pt!!.y - 2, 4, 4)
            }
            pt = kettleList.minByOrNull { (k, p) -> abs(e.x - p.x) + abs(e.y - p.y) }?.second ?: return
            g.color = Color.RED
            g.fillOval(pt!!.x - 2, pt!!.y - 2, 4, 4)
        }

        override fun mouseReleased(e: MouseEvent) {}
        override fun mousePressed(e: MouseEvent) {}
        override fun mouseExited(e: MouseEvent) {}
        override fun mouseEntered(e: MouseEvent) {}
    }

    init {
        addMouseListener(mouseListener)
    }

    private const val border = 20
    override fun paint(g: Graphics) {
        setSize(1400, 900)
        val currentTime = System.currentTimeMillis()
        val optimums = nearKettles(room, ml, currentTime)
        if (optimums.isEmpty()) return
        val distanceRange = extended(optimums.rangeOf { (_, dist) -> dist })
        val boilRange = extended(optimums.rangeOf { (kettle, _) -> (currentTime - kettle.boilTime).toInt() / 1000 })

        clear(g)
        drawAxes(g)
        val (xRange, yRange) = drawScale(g, distanceRange, boilRange)
        drawPoints(g, optimums, currentTime, distanceRange, boilRange)
    }

    private fun clear(g: Graphics) {
        foreground = Color.WHITE
        g.drawRect(0, 0, width, height)
    }

    private fun drawPoints(
        g: Graphics, optimums: List<Pair<Kettle, Int>>,
        currentTime: Long, xRange: IntRange, yRange: IntRange
    ): List<Pair<Kettle, Point>> {
        g.color = Color.BLUE
        val res = mutableListOf<Pair<Kettle, Point>>()
        for ((kettle, dist) in optimums) {
            val x = getCoordinate(xRange, dist, width)
            val boilTime = (currentTime - kettle.boilTime).toInt() / 1000
            val y = getCoordinate(yRange, yRange.first + yRange.last - boilTime, height)
            g.drawOval(x - 2, y - 2, 4, 4)
            res.add(kettle to Point(x, y))
        }
        return res
    }

    private fun romanFont(size: Int) = Font("Times New Roman", Font.PLAIN, size)

    private fun drawScale(g: Graphics, xRange: IntRange, yRange: IntRange): Pair<List<Int>, List<Int>> {
        g.color = Color.BLACK
        val freq = 2
        val xLabels = xRange.split(parts = (width - freq * border) / freq / border)
        val yLabels = yRange.split(parts = (height - freq * border) / freq / border)
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
