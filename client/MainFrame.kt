import java.awt.BorderLayout
import java.awt.FlowLayout
import java.awt.GridLayout
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.KeyStroke

fun main() = MainFrame.stub()

object MainFrame : JFrame("Kettle map") {
    fun stub() = Unit

    init {
        layout = BorderLayout()
        add(KettleCanvas, BorderLayout.WEST)

        val input = JPanel(GridLayout(2, 1))
        val editWrapper = JPanel(FlowLayout())
        editWrapper.add(UnwrappedEditPanel)
        input.add(editWrapper)
        val buttonWrapper = JPanel(FlowLayout())
        buttonWrapper.add(UnwrappedButtonPanel)
        input.add(buttonWrapper)
        add(input, BorderLayout.EAST)
        pack()
        setSize(800, 800)
        isVisible = true
    }
}
