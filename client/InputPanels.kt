import java.awt.BorderLayout
import java.awt.GridLayout
import java.lang.NumberFormatException
import javax.swing.*
import kotlin.system.exitProcess

class EditPanel(question: String, default: String, actionOnEditing: (String) -> String) : JPanel(BorderLayout()) {
    init {
        add(JLabel(question), BorderLayout.WEST)

        val messageLabel = JLabel()

        val field = JTextField(default)
        field.addCaretListener { messageLabel.text = actionOnEditing(field.text) }
        add(field, BorderLayout.EAST)
    }
}

object UnwrappedEditPanel : JPanel(GridLayout(2, 1)) {
    init {

        add(EditPanel("Where are you? (Your room)", "") {
            room = it
            ""
        })

        add(EditPanel("How much tea do you want? (ml)", "200") {
            try {
                val i = it.toInt()
                if (i <= 0) "Volume is expected to be positive"
                else {
                    ml = i
                    ""
                }
            } catch (e: NumberFormatException) {
                "Volume is expected to be integer"
            }
        })

    }
}

object UnwrappedButtonPanel : JPanel(GridLayout(5, 1)) {
    fun addButton(label: String, action: () -> Unit) {
        val button = JButton(label)
        button.addActionListener { action() }
        add(button)
    }

    init {
        addButton("I don't want tea!") { exitProcess(0) }
        addButton("Add new kettle") { addKettle(room) }
        addButton("Remove selected kettle", ::removeSelectedKettle)
        addButton("Boil selected kettle", ::boilSelectedKettle)
        addButton("Drink some water from selected kettle", ::drinkSomeWater)
    }
}

fun drinkSomeWater() {
    MultiEnterDialog("TEA!!!", "How much water remains? (In millilitres)")
        .processData {
            try {
                val intVolume = it[0].toInt()

                if (intVolume <= 0) {
                    "Volume is expected to be positive! (Did you drink more than was?)"
                } else {
                    drink(KettleCanvas.getSelectedKettle().id, intVolume)
                    null
                }
            } catch (e: NumberFormatException) {
                "Volume is expected to be integer (No one can measure so accurately)"
            }
        }
    KettleCanvas.repaint()
}

fun removeSelectedKettle() {
    removeKettle(KettleCanvas.getSelectedKettle().id)
    KettleCanvas.repaint()
}

fun boilSelectedKettle() {
    MultiEnterDialog("Boil?", "How much did you pour? (In millilitres)")
        .processData {
            try {
                val intVolume = it[0].toInt()

                if (intVolume <= 0) {
                    "Volume is expected to be positive! (Do you want to donate water?)"
                } else {
                    boilKettle(KettleCanvas.getSelectedKettle().id, intVolume)
                    null
                }
            } catch (e: NumberFormatException) {
                "Volume is expected to be integer (No one can measure so accurately)"
            }
        }
    KettleCanvas.repaint()
}
