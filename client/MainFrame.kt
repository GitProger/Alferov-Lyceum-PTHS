import java.awt.BorderLayout
import java.awt.FlowLayout
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.KeyStroke

fun main() = MainFrame.stub()

object MainFrame : JFrame("Kettle map") {
    fun stub() = Unit

    init {
        layout = BorderLayout()
        add(KettleCanvas, BorderLayout.WEST)

        val wrapper = JPanel(FlowLayout())
        wrapper.add(UnwrappedButtonPanel)
        add(wrapper, BorderLayout.EAST)

        val menu = Menu(
            "Menu", 'M',
            MenuItem("I want tea", 'W', KeyStroke.getKeyStroke('W', ALT)) { requestForNearKettles() },
            MenuItem("Add kettle", 'A', KeyStroke.getKeyStroke('A', ALT)) { addNewKettle() },
            MenuItem("Remove selected kettle", 'R', KeyStroke.getKeyStroke('R', ALT)) { removeSelectedKettle() },
            MenuItem("Boil kettle", 'B', KeyStroke.getKeyStroke('B', ALT)) { boilSelectedKettle() },
            MenuItem("Drink", 'D', KeyStroke.getKeyStroke('D', ALT)) { drinkSomeWater() },
        )
    }
}
