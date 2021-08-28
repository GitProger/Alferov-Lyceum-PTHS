import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.GridLayout
import javax.swing.JFrame
import javax.swing.JPanel

fun main() {
    MainFrame.stub()
    KettleCanvas.repaint()
}

/*{
    update(2, System.currentTimeMillis(), 1000)//
}*/
fun wrapped(panel: JPanel) = JPanel(BorderLayout()).apply { add(panel, BorderLayout.CENTER) }

object MainFrame : JFrame("Kettle map") {
    fun stub() = Unit

    init {
/*        setBounds(0, 0, 1000, 800)
        add(KettleCanvas)
        KettleCanvas.setBounds(0, 0, 1000, 1000)
        isVisible = true
        defaultCloseOperation = EXIT_ON_CLOSE*/
        defaultCloseOperation = EXIT_ON_CLOSE
        layout = BorderLayout()
        add(KettleCanvas, BorderLayout.WEST)

        val input = JPanel(GridLayout(2, 1))
        input.add(wrapped(UnwrappedEditPanel))
        input.add(wrapped(UnwrappedButtonPanel))
        add(input, BorderLayout.EAST)

        val (iw, ih) = input.size
        pack()
        setSize(1600,900)

        isVisible = true
        extendedState = extendedState or MAXIMIZED_BOTH
    }
}

private operator fun Dimension.component1(): Int = width
private operator fun Dimension.component2(): Int = height
