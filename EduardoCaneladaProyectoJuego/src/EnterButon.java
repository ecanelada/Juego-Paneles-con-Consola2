import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

/**
 * 
 * @author Edu2
 *
 */
public class EnterButon extends JButton{

	/**
	 * constructor sobrecargado, da la función a los botones de activarse mediante la tecla "enter" y modificar su nombre
	 * @param nom
	 */
	EnterButon(String nom){
		super(nom);
		super.registerKeyboardAction(
                super.getActionForKeyStroke(
                        KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false)),
                        KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false),
                        JComponent.WHEN_FOCUSED);
        super.registerKeyboardAction(
                super.getActionForKeyStroke(
                        KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, true)),
                        KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true),
                        JComponent.WHEN_FOCUSED);
	}
}
