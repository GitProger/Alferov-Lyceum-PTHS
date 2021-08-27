import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.SwingUtilities

class HelloWorld : Runnable {
    override fun run() {
        JFrame("Hello, World!").also {
            it.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE)
            it.add(JLabel("Hello World"))
            it.pack()
            it.setVisible(true)
        }
    }
}

fun main() {
    SwingUtilities.invokeLater(HelloWorld())

    println("Hello")
}
