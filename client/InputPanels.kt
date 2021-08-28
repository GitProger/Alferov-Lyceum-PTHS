import java.awt.BorderLayout
import java.awt.GridLayout
import java.lang.NumberFormatException
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import kotlin.system.exitProcess

class EditPanel(question: String, default: String, actionOnEditing: (String) -> Unit) : JPanel(GridLayout(1, 2)) {
    init {
        add(JLabel(question), BorderLayout.WEST)

        val field = JTextField(default)
        field.document.addDocumentListener(object : DocumentListener {
            override fun insertUpdate(e: DocumentEvent?) {
                actionOnEditing(field.text)
            }

            override fun removeUpdate(e: DocumentEvent?) {
                actionOnEditing(field.text)
            }

            override fun changedUpdate(e: DocumentEvent?) {
                actionOnEditing(field.text)
            }
        })
        add(field, BorderLayout.EAST)
    }
}

object UnwrappedEditPanel : JPanel(GridLayout(2, 1)) {
    init {

        add(EditPanel("Where are you? (Your room)", "") {
            room = it
        })

        add(EditPanel("How much tea do you want? (ml)", "200") {
            try {
                ml = it.toInt()
            } catch (e: NumberFormatException) {
            }
        })

    }
}

object UnwrappedButtonPanel : JPanel(GridLayout(5, 1)) {
    init {
        addButton("I don't want tea!") { exitProcess(0) }
        addButton("Add new kettle") { add(room) }
        addButton("Remove selected kettle", ::removeSelectedKettle)
        addButton("Boil selected kettle", ::boilSelectedKettle)
        addButton("Drink some water from selected kettle", ::drinkSomeWater)
        addButton("Manage other kettle...") { TableDialog() }
    }
}

fun drinkSomeWater() {
    val selected = KettleCanvas.getSelectedKettle() ?: return
    val water = JOptionPane.showInputDialog(
        MainFrame,
        "How much water remains? (In millilitres)",
        "TEA!!!",
        JOptionPane.QUESTION_MESSAGE
    )
    try {
        val intVolume = water.toInt()

        if (intVolume <= 0) {
            JOptionPane.showInternalMessageDialog(
                MainFrame,
                "Volume is expected to be positive! (Did you drink more than was?)"
            )
        } else {
            drink(selected.id, intVolume)
        }
    } catch (e: NumberFormatException) {
        JOptionPane.showInternalMessageDialog(
            MainFrame,
            "Volume is expected to be integer (No one can measure so accurately)"
        )
    }
    KettleCanvas.repaint()
}

fun removeSelectedKettle() {
    val selected = KettleCanvas.getSelectedKettle() ?: return

    delete(selected.id)
    KettleCanvas.repaint()
}

fun boilSelectedKettle() {
    val selected = KettleCanvas.getSelectedKettle() ?: return

    val water = JOptionPane.showInputDialog(
        MainFrame,
        "How much did you pour? (In millilitres)",
        "Boil?",
        JOptionPane.QUESTION_MESSAGE
    )
    try {
        val intVolume = water.toInt()

        if (intVolume <= 0) {
            JOptionPane.showInternalMessageDialog(
                MainFrame,
                "Volume is expected to be positive! (Did you drink more than was?)"
            )
        } else {
            boilKettle(selected.id, intVolume)
        }
    } catch (e: NumberFormatException) {
        JOptionPane.showInternalMessageDialog(
            MainFrame,
            "Volume is expected to be integer (No one can measure so accurately)"
        )
    }
    KettleCanvas.repaint()
}
