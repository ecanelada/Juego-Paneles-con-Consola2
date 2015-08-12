import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.SQLException;

/**
 * 
 * @author Edu2
 *
 */
public class AccionToolbar implements ActionListener {

	ConsolaInicial ci;
	
	//constructor sobrecargado
	AccionToolbar(ConsolaInicial c){
		ci=c;
	}
	
	/**
	 * Recoge las acciones de las toolbar de la consola inicial. Para el botón STATS desplega el panel STATS y para FAQ su homónimo, modificando el tamaño de la consola
	 * @see #repinta()
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource()==ci.displaySTATS){
			if (ci.STATS.isEnabled()==false){
				ci.setPreferredSize(new Dimension(700, 400));
				ci.getContentPane().add(ci.STATS, BorderLayout.EAST);
				ci.STATS.setVisible(true);
				ci.STATS.setEnabled(true);
				ci.pack();
				ci.f = new FirstPdf(ci.STATS, ci.cp);
			} else {
				ci.setPreferredSize(new Dimension(370, 400));
				ci.STATS.setEnabled(false);
				ci.STATS.setVisible(false);
			}
			repinta();
		}
	}
	
	/**
	 * actualiza el aspecto de la consola
	 */
	private void repinta(){
		ci.pack();
		ci.validate();
		ci.repaint();
	}

}
