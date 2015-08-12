import java.awt.BorderLayout;
import java.awt.Color;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToolBar;

/**
 * 
 * @author Edu2
 * @version 4.13.9
 */
public class ConsolaInicial extends JFrame{
	
	JPanel login, FAQ, STATS, juego;
	JButton displayFAQ, displaySTATS;
	Statement st;
	JToolBar toolbar2;
	FirstPdf f;
	CalculaPuntuaciones cp;
	DatosJugador dJug = new DatosJugador();
	
	
	/**
	 * Constructor de la ventana sobre la que se desarrolla toda la aplicación
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws IOException
	 * @see verticalizarTexto()
	 */
	ConsolaInicial(CalculaPuntuaciones c) throws ClassNotFoundException, SQLException, IOException{
		super();
		this.setLayout(new BorderLayout());
		this.setLocation(500, 50);
		this.setSize(370, 400);
	    f=null;
	    cp=c;
	    
		
		//panel inicio
		login = new Login(this);
		this.getContentPane().add(login, BorderLayout.CENTER);
		login.setVisible(true);
		
		
		
		//panel stats
		STATS = new Stats(this, cp);
		STATS.setEnabled(false);
		toolbar2 = new JToolBar(JToolBar.VERTICAL);
		displaySTATS = new JButton(verticalizarTexto("STATS y PDF "));
		displaySTATS.addActionListener(new AccionToolbar(this));
		displaySTATS.setEnabled(false);
		toolbar2.add(displaySTATS);
		this.getContentPane().add(toolbar2, BorderLayout.EAST);
		
		st = c.conectarBD();
		this.setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	/**
	 * Devuelve el String con cada caracter en una linea
	 * @param str, String del texto a poner en vertical
	 * @return ans, String con el texto en vertical
	 */
	public String verticalizarTexto(String str) {
	    String ans = "<html>";
	    String br = "<br>";
	    String[] lettersArr = str.split("");
	    for (String letter : lettersArr) {
	        ans += letter + br;
	    }
	    ans += "</html>";
	    return ans;
	}
}
