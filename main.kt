import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.SwingUtilities

class HelloWorld : Runnable {
    override fun run() {
        val f = JFrame("Hello, World!")
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE)
        f.add(JLabel("Hello World"))
        f.pack()
        f.setVisible(true)
    }
}

fun main() {
    SwingUtilities.invokeLater(HelloWorld())

    println("Hello")
}
