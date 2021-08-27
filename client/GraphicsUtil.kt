import java.awt.*
import java.awt.event.KeyEvent
import java.lang.NumberFormatException
import javax.swing.*

class MultiEnterDialog(title: String, vararg datas: String) : JDialog(MainFrame, title, false) {
    val fields = List(datas.size) { JTextField() }
    var state = 0 // 0 means waiting, 1 means OK pressing, -1 means Cancel pressing
    val messageLabel = JLabel("")

    init {
        layout = GridLayout(datas.size + 3, 1)

        messageLabel.font = Font(messageLabel.font.name, messageLabel.font.size, messageLabel.font.style and Font.BOLD)
        messageLabel.foreground = Color(127, 0, 0)

        for (i in datas.indices) {
            val panel = JPanel(BorderLayout())
            panel.add(JLabel(datas[i]), BorderLayout.WEST)
            panel.add(fields[i], BorderLayout.EAST)
        }

        add(JLabel(" "))

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

    fun processData(action: (List<String>) -> String?) {
        while (true) {
            reject()
            val state = waitForRes()
            if (state == -1) return
            val data = getResult()

            val message = action(data) ?: break

            reject()
            messageLabel.text = message
        }
    }
}





