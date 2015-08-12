import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

/**
 * 
 * @author Edu2
 *
 */
public class LoginBotones extends DatosJugador implements ActionListener{

	Login l;
	
	/**
	 * constructor sobrecargado
	 * @param lo
	 */
	LoginBotones (Login lo){
		l=lo;
	}
	
	/**
	 * método que controla las acciones de los botones de la clase Login
	 * @see #actualizarPanel()
	 * @see #datosJugador()
	 * @see #registraUser()
	 * @see #cambiarDialogoInicio()
	 * @see #guardaHandicapUsado()
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource()==l.aceptar){
			try {
				datosJugador();
				actualizarPanel();
				l.cargaNivelYHandicap();
			} catch (SQLException | IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			l.cargaHandicap();
			l.cargaNiveles();
			l.ci.setPreferredSize(new Dimension(370, 400));
			l.ci.pack();
			
		} else if (e.getSource()==l.registrar){
			try {
				registraUser();
				datosJugador();
				actualizarPanel();
				l.cargaNivelYHandicap();
			} catch (SQLException | IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			l.cargaHandicap();
			l.cargaNiveles();
			l.registrar.setEnabled(false);
			l.ci.displaySTATS.setEnabled(false);
			
		} else if (e.getSource()==l.logout){
			logout();
			
		} else if (e.getSource()==l.jugar){
			if (l.niveles.getSelectedIndex()!=0 && l.handicap.getSelectedIndex()!=0){
				int anadeTamano = Integer.parseInt(l.niveles.getSelectedItem().toString())*50;
				try {
					if (l.getHayHandicap())
						guardaHandicapUsado();
					l.ci.juego = new VentanaJuego(this);
				} catch (IOException | SQLException | ClassNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				l.ci.login.setVisible(false);
				l.ci.getContentPane().add(l.ci.juego, BorderLayout.CENTER);
				l.ci.juego.setVisible(true);
				l.ci.displaySTATS.setEnabled(false);
				l.ci.displaySTATS.setVisible(false);
				l.ci.toolbar2.setVisible(false);
				l.ci.STATS.setVisible(false);
				l.ci.setPreferredSize(new Dimension(370+anadeTamano ,400+anadeTamano));
				l.ci.pack();
			} else if (l.niveles.getSelectedItem().equals("  Seleccione nivel")){
				l.niveles.requestFocusInWindow();
				l.ci.setPreferredSize(new Dimension(370,400));
				l.ci.pack();
			}
		}
	}

	/**
	 * Comprueba que los contenidos de los campos de login han sido rellenados, devuelve false si no es as�
	 * @return rellena
	 */
	private boolean comprobarContenido (){
		boolean rellena=false;
		if (l.Nom.getText().toString().equals("")){
			JOptionPane.showMessageDialog( null, "Debe de introducir el nombre");
			l.Nom.requestFocus();
		}
		if (l.pass.getText().toString().equals("")){
			JOptionPane.showMessageDialog( null, "Debe de introducir la contraseña");
			l.pass.requestFocus();
		}
		if (!l.Nom.getText().toString().equals("") && !l.pass.getText().toString().equals("")){
			rellena = true;
		}
		
		return rellena;
	}
	
	/**
	 * Comprueba si el usuario existe o si puede registrarse
	 * @return i, valdrá 1 si el usuario existe y -1 si puede registrarse
	 * @throws SQLException
	 * @see #comprobarContenido()
	 */
	private int comprobarUsuario () throws SQLException{
		int i=0,j=0;
		if (comprobarContenido()){
			ResultSet rs = l.ci.st.executeQuery("select * from user where Nombre like '"+l.Nom.getText().toString()+"' and Pass like '"+l.pass.getText().toString()+"' and Alta=1");
			while(rs.next()){
				i++;
			}
			rs.close();
			if (i==0){
				rs = l.ci.st.executeQuery("select * from user where Nombre like '"+l.Nom.getText().toString()+"' and Alta=1");
				while(rs.next()){
					j++;
				}
				rs.close();
				if (j==0){
					JOptionPane.showMessageDialog( null, "El usuario no existe");
					i=-1;
					l.Nom.requestFocusInWindow();
				}
				else{
					JOptionPane.showMessageDialog( null, "Contrase�a incorrecta");
					l.pass.requestFocusInWindow();
				}
			}
		} else {
			
		}
		return i;
	}
	
	/**
	 * Actualiza el panel de login
	 * @see #cambiarDialogoInicial()
	 * @see #comprobarUsuario()
	 * @see #mostrarCBHandicap()
	 * @throws SQLException
	 * @throws IOException 
	 */
	private void actualizarPanel() throws SQLException, IOException{
		switch(comprobarUsuario()){
		case -1 : //abilitar boton registro
			l.registrar.setVisible(true);
			l.aceptar.setVisible(false);
			//mostrarCBHandicap();
			l.Nom.setEnabled(false);
			l.pass.setEnabled(false);
			l.registrar.setFocusable(true);
			l.panelCentro.validate();
			l.panelCentro.repaint();
			break;
		case 1 : //hacer login y cargar stats
			l.aceptar.setEnabled(false);
			l.ci.displaySTATS.setEnabled(true);
			cambiarDialogoInicio();
			l.partida.setVisible(true);
			l.niveles.requestFocusInWindow();
			l.logout.setEnabled(true);
			break;
		case 0 : 
			break;
		}
	}
	
	/**
	 * Actualiza el diálogo inicial para que aparezca el nombre del usuario y muestra los combobox de nivel y handicap
	 */
	private void cambiarDialogoInicio(){
		l.labelIntro.setText("<html>Bienvenido a la <br></br> Alianza Caballino <br></br>soldado <i>"+getNomJugador().toUpperCase()+"</i></html>");
		l.jugar.setVisible(true);
		l.niveles.setPreferredSize(new Dimension(130, 20));
		l.niveles.setVisible(true);
		//muestra el combobox de handicap solo si tiene acceso a handicap
		mostrarCBHandicap();
		l.panelCentro.validate();
		l.panelCentro.repaint();
	}
	
	/**
	 * Recupera el estado inicial de la consola
	 * @see #cierraStats()
	 */
	private void logout(){
		l.labelIntro.setText("<html>Bienvenido a la <br></br> Alianza Caballino <br></br>soldado SIN NOMBRE </html>");
		l.Nom.setText("");
		l.Nom.setEnabled(true);
		l.pass.setText("");
		l.pass.setEnabled(true);
		l.registrar.setVisible(false);
		l.registrar.setEnabled(false);
		l.aceptar.setVisible(true);
		l.aceptar.setEnabled(true);
		l.ci.displaySTATS.setEnabled(false);
		l.ci.toolbar2.setEnabled(false);
		l.partida.setVisible(false);
		l.Nom.requestFocusInWindow();
		cierraStats();
	}
	
	/**
	 * Muestra el combobox handicap si el jugador tiene acceso a handicap
	 * @see #l.getHayHandicap()
	 */
	private void mostrarCBHandicap(){
		if (l.getHayHandicap())
			l.handicap.setVisible(true);
		else {
			l.handicap.setVisible(false);
		}
	}
	
	/**
	 * Crea un fichero de texto en el que almacena los datos principales del jugador
	 * @throws IOException
	 * @throws SQLException
	 */
	private void datosJugador() throws IOException, SQLException{
		ResultSet rs = l.ci.st.executeQuery("select * from user where Nombre like '"+l.Nom.getText().toString()+"' and Pass like '"+l.pass.getText().toString()+"' and Alta=1");
		while(rs.next()){
			setIDJugador(rs.getInt("ID"));
			setDificultadMax(Integer.parseInt(rs.getString("Nivel")));
			setNomJugador(rs.getString("Nombre"));
		}
		rs.close();
	}
	
	/**
	 * Registra al usuario
	 * @throws SQLException
	 * @throws IOException 
	 * @see #generaId()
	 */
	private void registraUser() throws SQLException, IOException{
		GregorianCalendar gcal = new GregorianCalendar();
		int dia, mes, anio;
		dia=gcal.get(Calendar.DAY_OF_MONTH);
		mes=gcal.get(Calendar.MONTH)+1;
		anio=gcal.get(Calendar.YEAR);
		String fecha=anio+"-"+mes+"-"+dia;
		int id = generaId()+1;
		l.ci.st.executeUpdate("insert into User (ID, Nombre, Pass, Nivel, Nivel_descompresion, Puntos_handicap_usados, Fecha_registro, Alta) VALUES ('"+id+"', '"+l.Nom.getText().toString()+"', '"+l.pass.getText().toString()+"', '0', '0', '0', '"+fecha+"', '1')");
	}
	
	/**
	 * Calcula la Id para el nuevo jugador
	 * @return id
	 * @throws SQLException
	 */
	private int generaId() throws SQLException{
		int id=0;
		ResultSet rs = (ResultSet) l.ci.st.executeQuery("select ID from User order by ID asc");
		while(rs.next()){
			id=rs.getInt("ID");
		}
		rs.close();
		return id;
	}
	
	/**
	 * Actualiza en la base de datos el handicap que usa el jugador en cada partida
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	private void guardaHandicapUsado () throws SQLException, ClassNotFoundException{
		float puntos=0;
		puntos = Float.parseFloat(l.handicap.getSelectedItem().toString().substring(11));
		Statement stm = l.ci.cp.conectarBD();
		ResultSet rs = stm.executeQuery("select Puntos_handicap_usados from user where ID like '"+getIDJugador()+"'");
		while (rs.next()){
			puntos += rs.getFloat("Puntos_handicap_usados");
		}
		rs.close();
		stm.executeUpdate("update user set Puntos_handicap_usados = '"+puntos+"' where ID like '"+getIDJugador()+"'");
		stm.close();
	}
	
	/**
	 * devuelve la ventana principal a su estado inicial una vez se cierra el despliegue del panel STATS
	 */
	private void cierraStats(){
		if (l.ci.STATS.isVisible()){
			l.ci.STATS.setVisible(false);
			l.ci.setPreferredSize(new Dimension(370, 400));
			l.ci.pack();
		}
	}
}
