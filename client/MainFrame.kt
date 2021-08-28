import java.awt.BorderLayout
import java.awt.FlowLayout
import java.awt.GridLayout
import javax.swing.JFrame
import javax.swing.JPanel

fun main() = MainFrame.stub()

fun wrapped(panel:JPanel) = JPanel(BorderLayout()).apply { add(panel, BorderLayout.CENTER) }

object MainFrame : JFrame("Kettle map") {
    fun stub() = Unit

    init {
        defaultCloseOperation = EXIT_ON_CLOSE
        layout = BorderLayout()
        add(KettleCanvas, BorderLayout.WEST)

        val input = JPanel(GridLayout(2, 1))
        input.add(wrapped(UnwrappedEditPanel))
        input.add(wrapped(UnwrappedButtonPanel))
        add(input, BorderLayout.EAST)
        pack()
        setSize(800, 800)
        isVisible = true
    }
}
