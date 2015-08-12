import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.io.IOException;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;


/**
 * 
 * @author Edu2
 *
 */
public class Stats extends JPanel{

	
	private boolean bandera=true;
	CalculaPuntuaciones cp;
	ConsolaInicial ci;
	
	/**
	 * constructor por defecto
	 */
	Stats(){
		super();
        this.setBorder(new EmptyBorder(5, 5, 5, 5));
        this.setPreferredSize(new Dimension(350, 400));
        this.setLayout(new FlowLayout(FlowLayout.CENTER));
	}
	
	/**
	 * constructor sobrecargado
	 * @param ci
	 * @param cp
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws IOException
	 */
    public Stats(ConsolaInicial ci, CalculaPuntuaciones cp) throws ClassNotFoundException, SQLException, IOException {
    	super();
        this.setBorder(new EmptyBorder(5, 5, 5, 5));
        this.setPreferredSize(new Dimension(350, 400));
        this.setLayout(new FlowLayout(FlowLayout.CENTER));
        this.ci = ci;
        this.cp = cp;
    }
    
    /**
     * Método que dibuja la gráfica en función de los puntos totales, los puntos del último mes y los puntos gastados en handicap
     * @see cp#almacenaPuntos()
     * @see cp#calculaPuntosTotalJugador()
     * @see cp#calculaPuntosUltimoMes()
     * @see cp#desbloqueaNivel()
     * @see cp#getPuntosIndividuales()
     * @see cp#getPuntosUltimoMes()
     * @see cp#getHandicapGastado()
     */
    public void paint(Graphics g)
    {
        super.paint(g);
        try {
			cp.calculaPuntosTotalJugador(ci.dJug.getIDJugador());
			cp.calculaPuntosUltimoMes();
			cp.desbloqueaNivel();
		} catch (NumberFormatException | SQLException | IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        int v1= cp.getPuntosIndividuales();
        int v2= cp.getPuntosUltimoMes();
        int v3=0;
		try {
			v3 = cp.getHandicapGastado();
		} catch (NumberFormatException | SQLException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        int suma=v1+v2+v3;
        int grados1=v1*360/suma;
        int grados2=v2*360/suma;
        int grados3=v3*360/suma;

        //sombreado
        g.setColor(new Color(70,70,70));
        g.fillArc(55, 103, 200, 200, 0, 360);
        //circunferencia roja
        g.setColor(new Color(255,0,0));
        g.fillArc(50,100,200,200,0,grados1);
        g.fillRect(370,250,20,20);
        g.drawString("Puntos totales", 230, 40);
        g.drawString(""+v1, 230, 55);          
        
        //circunferencia verde
        g.setColor(new Color(0,128,0));
        g.fillArc(50,100,200,200,grados1,grados2);
        g.fillRect(370,280,20,20);
        g.drawString("Puntos último mes", 230, 75);
        g.drawString(""+v2, 230, 90);            
        
        //circunferencia azul
        g.setColor(new Color(0,0,255));
        g.fillArc(50,100,200,200,grados1+grados2,grados3);
        g.fillRect(370,310,20,20);
        g.drawString("Handicap", 230, 110);
        g.drawString(""+v3, 230, 125);  
        
    }

}
	
