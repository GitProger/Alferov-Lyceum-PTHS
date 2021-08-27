import java.awt.GridLayout
import java.lang.NumberFormatException
import javax.swing.JButton
import javax.swing.JOptionPane
import javax.swing.JPanel
import javax.swing.JTextField

object UnwrappedButtonPanel : JPanel(GridLayout(10, 1)) {
    init {
        val roomField = JTextField()
        roomField.addCaretListener{
            room = roomField.text
            KettleCanvas.repaint()
        }

        val wantTea = JButton("I want tea!")
        wantTea.addActionListener { _ -> requestForNearKettles() }
        add(wantTea)

        val addKettle = JButton("Add new kettle")
        addKettle.addActionListener { _ -> addNewKettle() }
        add(addKettle)

        val removeKettle = JButton("Remove selected kettle")
        removeKettle.addActionListener { _ -> removeSelectedKettle() }
        add(removeKettle)

        val boilKettle = JButton("Boil selected kettle")
        boilKettle.addActionListener { _ -> boilSelectedKettle() }
        add(boilKettle)

        val drinkSome = JButton("Drink some water from selected kettle")
        drinkSome.addActionListener { _ -> drinkSomeWater() }
        add(drinkSome)
    }
}

fun drinkSomeWater() {
    val dialog = MultiEnterDialog(
        "TEA!!!",
        "How much water remains? (In millilitres)"
    )
    do {
        val state = dialog.waitForRes()
        if (state == -1) return
        val (volume) = dialog.getResult()
        var ok = false

        try {
            val intVolume = volume.toInt()

            if (intVolume <= 0) JOptionPane.showInternalMessageDialog(
                dialog,
                "Volume is expected to be positive! (Did you drink more than was?)"
            )
            drink(KettleCanvas.getSelectedKettle().id, intVolume)
            ok = true
        } catch (e: NumberFormatException) {
            JOptionPane.showInternalMessageDialog(
                dialog,
                "Volume is expected to be integer (No one can measure so accurately)"
            )
        }
    } while (ok)
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
    do {
        val state = dialog.waitForRes()
        if (state == -1) return
        val (volume) = dialog.getResult()
        var ok = false

        try {
            val intVolume = volume.toInt()

            if (intVolume <= 0) JOptionPane.showInternalMessageDialog(
                dialog,
                "Volume is expected to be positive! (Do you want to donate water?)"
            )
            boilKettle(KettleCanvas.getSelectedKettle().id, intVolume)
            ok = true
        } catch (e: NumberFormatException) {
            JOptionPane.showInternalMessageDialog(
                dialog,
                "Volume is expected to be integer (No one can measure so accurately)"
            )
        }
    } while (ok)
    KettleCanvas.repaint()
}

fun addNewKettle() {
    val dialog = MultiEnterDialog(
        "We need more kettles!",
        "Where are you? (Enter your room number)"
    )
    val state = dialog.waitForRes()
    if (state == -1) return
    val (room) = dialog.getResult()

    val newKettle = addKettle(room)
    allKettles.add(newKettle)
    KettleCanvas.repaint()
}

fun requestForNearKettles() {
    val dialog = MultiEnterDialog(
        "Want tea?",
        "Where are you? (Enter your room number)", "How much do you want? (In millilitres)"
    )
    do {
        val state = dialog.waitForRes()
        if (state == -1) return
        val (room, volume) = dialog.getResult()
        var ok = false

        try {
            val intVolume = volume.toInt()

            if (intVolume <= 0) JOptionPane.showInternalMessageDialog(
                dialog,
                "Volume is expected to be positive! (Do you want to donate water?)"
            )
            nearKettles = getNearKettles(room, intVolume)
            ok = true
        } catch (e: NumberFormatException) {
            JOptionPane.showInternalMessageDialog(
                dialog,
                "Volume is expected to be integer (No one can measure so accurately)"
            )
        }
    } while (ok)
    KettleCanvas.repaint()
}