import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.SwingUtilities

class HelloWorld : Runnable {
    override fun run() {
        JFrame("Hello, World!").also {
            it.defaultCloseOperation = JFrame.DISPOSE_ON_CLOSE
            it.add(JLabel("Hello World"))
            it.pack()
            it.isVisible = true
        }
    }
}

fun main(args: Array<String>) {
    SwingUtilities.invokeLater(HelloWorld())

    println("Hello")
}
