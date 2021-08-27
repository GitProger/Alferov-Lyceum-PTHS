import java.awt.GridLayout
import java.lang.NumberFormatException
import javax.swing.JButton
import javax.swing.JOptionPane
import javax.swing.JPanel
import javax.swing.JTextField
import kotlin.system.exitProcess

object UnwrappedButtonPanel : JPanel(GridLayout(10, 1)) {
    init {
        val dontWantTea = JButton("I don't want tea!")
        dontWantTea.addActionListener { exitProcess(0) }
        add(dontWantTea)

        val addKettle = JButton("Add new kettle")
        addKettle.addActionListener { addNewKettle() }
        add(addKettle)

        val removeKettle = JButton("Remove selected kettle")
        removeKettle.addActionListener { removeSelectedKettle() }
        add(removeKettle)

        val boilKettle = JButton("Boil selected kettle")
        boilKettle.addActionListener { boilSelectedKettle() }
        add(boilKettle)

        val drinkSome = JButton("Drink some water from selected kettle")
        drinkSome.addActionListener { drinkSomeWater() }
        add(drinkSome)
    }
}

fun drinkSomeWater() {
    val dialog = MultiEnterDialog(
        "TEA!!!",
        "How much water remains? (In millilitres)"
    )
    dialog.processData {
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
    val dialog = MultiEnterDialog(
        "Boil?",
        "How much did you pour? (In millilitres)"
    )
    dialog.processData {
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

fun addNewKettle() {
    addKettle(room)
}

