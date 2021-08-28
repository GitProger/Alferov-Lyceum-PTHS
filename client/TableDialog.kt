import java.awt.BorderLayout
import java.awt.FlowLayout
import java.util.*
import javax.swing.*
import javax.swing.table.DefaultTableModel

class TableDialog : JDialog(MainFrame, "Other...", false) {
    val table = TablePanel()

    init{
        layout = BorderLayout()
        add(table, BorderLayout.NORTH)

        val buttons = JPanel(FlowLayout())
        buttons.addButton("Boil") {boil(table.getSelected())}
        buttons.addButton("Remove") {}
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
            arrayOf<Any>(false, it.room, (it.boilTime - curtime).asTime(), "${it.ml} ml")
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
        for((row, id) in ids.withIndex()){
            val check = table.getValueAt(row,0) as Boolean
            if(check) res.add(id)
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
