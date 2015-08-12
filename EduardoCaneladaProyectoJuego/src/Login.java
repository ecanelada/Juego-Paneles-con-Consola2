import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * 
 * @author Edu2
 *
 */
public class Login extends JPanel{

	JPanel panelCentro, panelBajo, identificacion, botonera, partida;
	JTextField Nom, pass;
	JLabel labelPass, labelNom, labelIntro, imagenSoldado, seleccionaNivel;
	JButton aceptar, logout, registrar, jugar;
	private String ok = "ok";
	JComboBox niveles = new JComboBox();
	JComboBox handicap = new JComboBox();
	Statement st;
	private int nivel, handicapGastado;
	private float cantPuntos;
	private boolean hayHandicap=false;
	
	ConsolaInicial ci;
	
	/**
	 * Constructor sobrecargado que genera la ventana de login
	 * @param c objeto de la clase ConsolaInicial
	 * @throws NumberFormatException
	 * @throws SQLException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	Login(ConsolaInicial c) throws NumberFormatException, SQLException, IOException, ClassNotFoundException{
		super();
		this.setLayout(new BorderLayout());
		ci = c;
		st = ci.cp.conectarBD();
		//panel CENTER
		panelCentro = new JPanel();
		panelCentro.setLocation(5, 5);
		panelCentro.setLayout(new FlowLayout(FlowLayout.CENTER));
		labelIntro = new JLabel("<html>Bienvenido a la <br></br> Alianza Caballino <br></br>soldado SIN NOMBRE </html>");
		labelIntro.setVerticalAlignment(JLabel.CENTER);
		panelCentro.add(labelIntro);
		
		this.add(panelCentro, BorderLayout.CENTER);
		
		//crea el avatar que habla
		URL imgURL = Login.class.getResource("/soldado3.png");
		imagenSoldado = new JLabel(new ImageIcon(imgURL));
		panelCentro.add(imagenSoldado);
		
		
		
		//panel partida
		partida = new JPanel();
		partida.setLayout(new GridLayout(3, 1, 3, 3));
		panelCentro.add(partida);
		
		//selección nivel y handicap
		partida.add(niveles);
		niveles.setVisible(false);
		partida.add(handicap);
		handicap.setVisible(false);
		
		//boton jugar
		jugar = new EnterButon("Empezar misi�n");
		partida.add(jugar);
		jugar.setMargin(new Insets(5, 40, 5, 40));
		jugar.addActionListener(new LoginBotones(this));
		jugar.setVisible(false);

		
		
		//panel SOUTH
		panelBajo = new JPanel();
		panelBajo.setLayout(new GridLayout(2, 1));
		this.add(panelBajo, BorderLayout.SOUTH);
		
		//subpanel identificacion
		identificacion = new JPanel();
		identificacion.setLayout(new GridLayout(2,1));
		panelBajo.add(identificacion, BorderLayout.SOUTH);
		
		//nombre
		labelNom = new JLabel("Nombre: ");
		labelNom.setLabelFor(Nom);
		labelNom.setHorizontalAlignment(JLabel.CENTER);
		Nom = new JTextField(10);
		identificacion.add(labelNom);
		identificacion.add(Nom);
		
		//contraseña
		labelPass = new JLabel("Contrase�a: ");
        labelPass.setLabelFor(pass);
        labelPass.setHorizontalAlignment(JLabel.CENTER);
		pass = new JPasswordField(10);
		identificacion.add(labelPass);
		identificacion.add(pass);
		
		
		
		//botonera
		botonera = new JPanel();
		botonera.setLayout(new FlowLayout());
		panelBajo.add(botonera, BorderLayout.SOUTH);
		
		//botones
		aceptar = new EnterButon("Aceptar");
		aceptar.addActionListener(new LoginBotones(this));
		botonera.add(aceptar);
		aceptar.setVisible(true);

		registrar = new EnterButon("Registrar");
		registrar.addActionListener(new LoginBotones(this));
		botonera.add(registrar);
		registrar.setVisible(false);
		
		logout = new EnterButon("Logout");
		logout.addActionListener(new LoginBotones(this));
		botonera.add(logout);
		logout.setEnabled(false);
		logout.setVisible(true);
		this.validate();
		this.repaint();
	}
	

	/**
	 * Busca el nivel, los puntos que tiene el jugador y los gastados como handicap del jugador seg�n su id
	 * @throws NumberFormatException
	 * @throws SQLException
	 * @throws IOException
	 */
	public void cargaNivelYHandicap() throws NumberFormatException, SQLException, IOException{
		ResultSet rs = st.executeQuery("select * from user where Nombre like '"+Nom.getText().toString()+"' and Pass like '"+pass.getText().toString()+"'");
		while(rs.next()){
			nivel = rs.getInt("Nivel");
			cantPuntos = rs.getFloat("Nivel_descompresion");
			handicapGastado = rs.getInt("Puntos_handicap_usados");
		}
		rs.close();
	}
	
	/**
	 * Carga en el combobox de niveles los niveles a los que tiene acceso el jugador
	 */
	public void cargaNiveles(){
		niveles.removeAllItems();
		niveles.addItem("  Seleccione nivel");
		for (int i=nivel;i>=0;i--){
			niveles.addItem(i);
        }
	}
	
	/**
	 * Carga en el combobox handicap los puntos que cuesta saltarse un número de casillas presionadas erroneamente
	 */
	public void cargaHandicap(){
		handicap.removeAllItems();
		//si tiene puntos suficientes para usar handicap se muestra, sino no
		//puntos = (nivel + 4)*(nivel + 4) * 5 * ((nivel+4)+5+nivel*2)
		float p=0;
		int [] puntosNivel = new int [nivel+1];
		//calculo los puntos que debería tener, mínimo, para haber alcanzado el nivel
		for (int i=0;i<=nivel;i++){
			//totales
			p += (i + 4)*(i + 4) * 5 * ((i+4)+5+i*2);
			//de cada nivel
			puntosNivel[i] = (i + 4)*(i + 4) * 5 * ((i+4)+5+i*2);
		}
		//se muestra el máximo de casillas que libra su handicap con el coste en puntos (tiene que actualizar la gráfica al momento)
		//si sus puntos superan la mitad de los requeridos para subir al siguiente nivel puede recibir handicap
		if (cantPuntos - handicapGastado >= p - puntosNivel[nivel]/2){
			handicap.addItem("  Seleccione handicap");
			//calcula el valor de cada unidad de handicap
			float hcap = (p - handicapGastado - puntosNivel[nivel]/2)/4;
			for (int j=0;j<=3;j++){
				handicap.addItem(j+" /Puntos: "+hcap*j);
			}
			hayHandicap=true;
		}else{
			hayHandicap=false;
		}
	}
	
	/**
	 * devuelve si el jugador tiene derecho a handicap o no
	 * @return hayHandicap
	 */
	public boolean getHayHandicap(){
		return hayHandicap;
	}
	
	public void setHayHandicap(boolean hayHandicap){
		this.hayHandicap=hayHandicap;
	}
}
