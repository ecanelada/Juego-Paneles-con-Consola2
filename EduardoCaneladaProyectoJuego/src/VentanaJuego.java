import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.io.IOException;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * 
 * @author Edu2
 *
 */
public class VentanaJuego extends JPanel {

	JButton iniciar, limpiar;
	JPanel cuadroJuego;
	JPanel [] distribucion = new JPanel[5];
	JLabel l;
	LoginBotones lb;
	
	/**
	 * constructor sobrecargado que pinta el diseño del panel en el que correrá el juego
	 * @param lob
	 * @throws IOException
	 */
	VentanaJuego(LoginBotones lob) throws IOException{
		super();
		lb=lob;
		this.setLayout(new BorderLayout());
		
		cuadroJuego = new PanelJuegoPrueba(this);
		this.add(cuadroJuego, BorderLayout.CENTER);
		
		//bordes
		distribucion[0] = new JPanel();
		distribucion[2] = new JPanel();
		distribucion[3] = new JPanel();
		distribucion[4] = new JPanel();				
		this.add(distribucion[0], BorderLayout.NORTH);
		this.add(distribucion[2], BorderLayout.EAST);
		this.add(distribucion[3], BorderLayout.WEST);
		this.add(distribucion[4], BorderLayout.SOUTH);
		
		
		//Modifica la imagen del cursor
		Toolkit toolkit = Toolkit.getDefaultToolkit();  
		URL imgURL = Login.class.getResource("/target.png");
		Image image = toolkit.getImage(imgURL);
		Point hotSpot = new Point(20,20);
		Cursor cursor = toolkit.createCustomCursor(image, hotSpot, "Mira");
		setCursor(cursor);
	}
}
