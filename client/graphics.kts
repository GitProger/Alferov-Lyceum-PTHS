import java.awt.event.ActionListener
import java.awt.event.KeyEvent
import javax.swing.JMenuItem
import javax.swing.JOptionPane
import javax.swing.KeyStroke

val ALT = KeyEvent.ALT_DOWN_MASK
val CTRL = KeyEvent.CTRL_DOWN_MASK
val SHIFT = KeyEvent.SHIFT_DOWN_MASK

class MenuItem(val label: String, val mnemonic: Char, val hotkey: KeyStroke, val action: () -> String?) : JMenuItem(label) {
    fun init() {
        this.addActionListener({ event ->
            val res = action()
            if (res != null) JOptionPane.showInternalMessageDialog(null, res)
        })
        setMnemonic(mnemonic)
        accelerator = hotkey
    }
}


