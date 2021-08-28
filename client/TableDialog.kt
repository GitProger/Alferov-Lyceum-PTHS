import java.awt.BorderLayout
import java.awt.FlowLayout
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
    MultiEnterDialog(
        "Boil?",
        ids.map { "How much water is in ${kettles[it]!!.room} kettle? (${kettles[it]!!.ml} ml boiled ${(curtime - kettles[it]!!.boilTime).asTime()})" }
    ).processData {
        try {
            val volumes = it.map { it.toInt() }
            if (volumes.any { it <= 0 }) "Volume is expected to be positive! (Did you invent negative-mass water?)"
            else {
                for (i in ids.indices) {
                    boilKettle(ids[i], volumes[i])
                }
                null
            }
        } catch (e: NumberFormatException) {
            "Volume is expected to be integer! (Sure you can measure so accurately?)"
        }
    }
}

class TablePanel : JPanel(FlowLayout()) {
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
    this <= -120 -> "${this / 60} minutes ago" //Negative time is OK here
    this <= -60 -> "-1 minute ago"
    this == -1L || this == 1L -> "$this second ago"
    this < 60 -> "$this seconds ago"
    this < 120 -> "1 minute ago"
    this < 3600 -> "${this / 60} minutes ago"
    this < 7200 -> "1 hour ago"
    else -> "${this / 3600} hour ago"
}
