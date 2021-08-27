import java.awt.BorderLayout
import java.awt.GridLayout
import java.awt.event.ActionListener
import java.awt.event.KeyEvent
import java.lang.NumberFormatException
import javax.swing.*

val allKettles: MutableList<Kettle> = listOf()
val nearKettles: MutableList<Int> = listOf()

//Interaction with Leo's block
data class Kettle(val room: String, val id: Int, var boilTime: Long, var ml: Int)
fun getNearKettles(room: Int, volume: Int): List<Int>
fun getAllKettles()
fun addKettle(room:Int)
fun removeKettle(id:Int)
fun boilKettle(id:Int, volume:Int)
fun drink(id:Int, volumeRemaining:Int)

///Graphics
object KettleCanvas : JPanel() // Stub

object UnwrappedButtonPanel : JPanel(GridLayout(10, 1)) {
    constructor() {
        val wantTea = JButton("I want tea!")
        wantTea.addActionListener(::requestForNearKettles)
        add(wantTea)

        val addKettle = JButton("Add new kettle")
        addKettle.addActionListener(::addNewKettle)
        add(addKettle)

        val removeKettle = JButton("Remove selected kettle")
        removeKettle.addActionListener(::removeSelected)
        add(removeKettle)

        val boilKettle = JButton("Boil selected kettle")
        boilKettle.addActionListener(::boilSelectedKettle)
        add(boilKettle)

        val drinkSome = JButton("Drink some water")
    }
}

fun updateAllKettles() {
    allKettles = getAllKettles()
}

fun removeSelectedKettle(){
    removeKettle(kettleCanvas.getSelectedKettle().id)
}

fun boilSelectedKettle(){
    val dialog = MultiEnterDialog(
        "Boil?",
        listOf("How much did you pour? (In millilitres)")
    )
    do {
        val state = dialog.waitForRes()
        if (state == -1) return
        val (volume) = dialog.getResult()
        val ok = false

        try {
            val intVolume = volume.toInt()

            if (intVolume <= 0) JOptionPane.showInternalMessageDialog(
                dialog,
                "Volume is expected to be positive! (Do you want to donate water?)"
            )
            boilKettle(kettleCanvas.getSelectedKettle().id, intVolume)
            ok = true
        } catch (e: NumberFormatException) {
            JOptionPane.showInternalMessageDialog(dialog, "Volume is expected to be integer (No one can measure so accurately)")
        }
    } while (ok)
    KettleCanvas.repaint()
}

fun addNewKettle() {
    val dialog = MultiEnterDialog(
        "We need more kettles!",
        listOf("Where are you? (Enter your room number)")
    )
    val state = dialog.waitForRes()
    if (state == -1) return
    val (room) = dialog.getResult()

    newKettle = addNewKettle(room)
    allKettles.add(newKettle)
    KettleCanvas.repaint()
}

fun requestForNearKettles(){
    nearKettles = getNearKettles()
    val dialog = MultiEnterDialog(
        "Want tea?",
        listOf("Where are you? (Enter your room number)", "How much do you want? (In millilitres)")
    )
    do {
        val state = dialog.waitForRes()
        if (state == -1) return
        val (room, volume) = dialog.getResult()
        val ok = false

        try {
            val intVolume = volume.toInt()

            if (intVolume <= 0) JOptionPane.showInternalMessageDialog(
                dialog,
                "Volume is expected to be positive! (Do you want to donate water?)"
            )
            nearKettles = getNearKettles(room, intVolume)
            ok = true
        } catch (e: NumberFormatException) {
            JOptionPane.showInternalMessageDialog(dialog, "Volume is expected to be integer (No one can measure so accurately)")
        }
    } while (ok)
    KettleCanvas.repaint()
}

class MultiEnterDialog(title: String, vararg datas: String) : JDialog(MainFrame, false, title) {
    val fields = List(datas.size) { JTextField() }
    var state = 0 // 0 means waiting, 1 means OK pressing, -1 means Cancel pressing

    init {
        layout = GridLayout(datas.size + 1, 1)
        for (i in datas.indices) {
            val panel = JPanel(BorderLayout())
            panel.add(JLabel(datas[i]), BorderLayout.WEST)
            panel.add(fields[i], BorderLayout.EAST)
        }
        val buttonPanel = JPanel(FlowLayout())
        val ok = JButton("OK")
        ok.addActionListener { state = 1 }
        val cancel = JButton("Cancel")
        cancel.addActionListener {
            state = -1
            dispose()
        }
        buttonPanel.add(ok)
        buttonPanel.add(cancel)
        pack()
        isVisible = true
    }

    fun waitForRes(): Int {
        while (state == 0) Thread.yield()
        return state
    }

    fun reject() {
        state = 0
    }

    fun getResult() = fields.map { it.text }
}

object MainFrame : JFrame("Kettle map") {

}


data class Menu constructor(val label: String, val mnemonic: Char, val items: List<JMenuItem>) : JMenu(label) {
    constructor(label: String, mnemonic: Char, vararg items: JMenuItem) : this(label, mnemonic, items.asList())

    init {
        setMnemonic(mnemonic)
        items.forEach { add(it) }
    }
}

val ALT = KeyEvent.ALT_DOWN_MASK
val CTRL = KeyEvent.CTRL_DOWN_MASK
val SHIFT = KeyEvent.SHIFT_DOWN_MASK

class MenuItem(val label: String, val mnemonic: Char, val hotkey: KeyStroke, val action: () -> String?) :
    JMenuItem(label) {
    init {
        this.addActionListener({ event ->
            val res = action()
            if (res != null) JOptionPane.showInternalMessageDialog(null, res)
        })
        setMnemonic(mnemonic)
        accelerator = hotkey
    }
}




