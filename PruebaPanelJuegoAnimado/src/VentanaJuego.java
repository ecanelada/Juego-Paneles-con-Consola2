import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class VentanaJuego extends JFrame {

	JButton iniciar, limpiar;
	JPanel cuadroJuego;
	JPanel [] distribucion = new JPanel[5];
	JLabel l;
	
	VentanaJuego() throws IOException{
		super();
		this.setLayout(new BorderLayout());
		this.setBounds(500, 300, 270, 350);
		this.setResizable(false);
		distribucion[0] = new JPanel();
		l = new JLabel("JUEGO");
		distribucion[0].setLayout(new FlowLayout());
		distribucion[0].add(l);
		iniciar = new JButton("Iniciar");
		distribucion[0].add(iniciar);
		limpiar = new JButton("Limpiar");
		distribucion[0].add(limpiar);
		this.getContentPane().add(distribucion[0], BorderLayout.NORTH);
		
		cuadroJuego = new PanelJuegoPrueba();
		this.getContentPane().add(cuadroJuego, BorderLayout.CENTER);
		
		//bordes
		distribucion[2] = new JPanel();
		distribucion[3] = new JPanel();
		distribucion[4] = new JPanel();
				
		this.getContentPane().add(distribucion[2], BorderLayout.EAST);
		this.getContentPane().add(distribucion[3], BorderLayout.WEST);

				
		this.getContentPane().add(distribucion[4], BorderLayout.SOUTH);
				
		//funciones
		/*Timer timer = new Timer(1000, new motorDeJuego(this));
        timer.start();*/
		//iniciar.addActionListener(new motorDeJuego(this));
		//limpiar.addActionListener(new motorDeJuego(this));
				
		this.setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
}
