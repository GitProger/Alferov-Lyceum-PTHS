import java.awt.*
import java.awt.event.KeyEvent
import java.lang.NumberFormatException
import javax.swing.*

fun Container.addButton(label: String, action: () -> Unit) {
    val button = JButton(label)
    button.addActionListener { action() }
    add(button)
}




