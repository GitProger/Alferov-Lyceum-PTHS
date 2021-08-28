import java.awt.BorderLayout
import java.awt.FlowLayout
import java.awt.GridLayout
import java.lang.NumberFormatException
import javax.swing.*
import javax.swing.table.DefaultTableModel

class TableDialog : JDialog(MainFrame, "Other...", false) {
    val table = TablePanel()

    init {
        layout = BorderLayout()
        add(table, BorderLayout.NORTH)

        val buttons = JPanel(FlowLayout())
        buttons.addButton("Boil") {
            boilAll(table.getSelected())
            dispose()
        }
        buttons.addButton("Remove") {
            table.getSelected().forEach { delete(it) }
            dispose()
        }
        buttons.addButton("Cancel") {
            dispose()
        }
        add(buttons, BorderLayout.SOUTH)
        pack()
        isVisible = true
    }
}

fun boilAll(ids: List<Int>) {
    val kettles = updateAllKettles().associateBy { it.id }
    val curtime = System.currentTimeMillis()
    for(id in ids) {
        val water = JOptionPane.showInputDialog(
            MainFrame,
            "How much did you pour to ${kettles[id]!!.room} kettle? (${kettles[id]!!.ml} ml boiled ${(curtime - kettles[id]!!.boilTime).asTime()})? (In millilitres)", "Boil?", JOptionPane.QUESTION_MESSAGE
        )
        try {
            val intVolume = water.toInt()

            if (intVolume <= 0) {
                JOptionPane.showInternalMessageDialog(
                    MainFrame, "Volume is expected to be positive! (Did you drink more than was?)"
                )
            } else {
                boilKettle(id, intVolume)
            }
        } catch (e: NumberFormatException) {
            JOptionPane.showInternalMessageDialog(
                MainFrame, "Volume is expected to be integer (No one can measure so accurately)"
            )
        }
    }
    KettleCanvas.repaint()
}

class TablePanel : JPanel(GridLayout(1,1)) {
    // Название столбцов
    private val columns = arrayOf("", "Room", "Boil time", "Water")

    // Данные для таблицы

    private val data: Array<Array<Any>>
    private val ids: List<Int>
    private val table: JTable

    init {
        val allKettles = updateAllKettles()
        ids = allKettles.map { it.id }
        val curtime = System.currentTimeMillis()
        data = allKettles.map {
            arrayOf<Any>(false, it.room, (curtime - it.boilTime).asTime(), "${it.ml} ml")
        }.toTypedArray()

        val model: DefaultTableModel = object : DefaultTableModel(data, columns) {
            override fun getColumnClass(column: Int): Class<*> {
                return data[0][column].javaClass
            }
        }

        table = JTable(model)
        table.columnModel.getColumn(0).cellEditor = DefaultCellEditor(JCheckBox())
        add(JScrollPane(table))
    }

    fun getSelected(): List<Int> {
        val res = mutableListOf<Int>()
        for ((row, id) in ids.withIndex()) {
            val check = table.getValueAt(row, 0) as Boolean
            if (check) res.add(id)
        }
        return res
    }
}

private fun Long.asTime(): String = when {
    this <= -120000 -> "${this / 60000} minutes ago" //Negative time is OK here
    this <= -60000 -> "-1 minute ago"
    this == -1000L || this == 1000L -> "$this second ago"
    this < 60000 -> "$this seconds ago"
    this < 120000 -> "1 minute ago"
    this < 3600000 -> "${this / 60000} minutes ago"
    this < 7200000 -> "1 hour ago"
    else -> "${this / 3600000} hour ago"
}
