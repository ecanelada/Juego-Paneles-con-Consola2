import java.io.IOException;
import java.sql.SQLException;

public class Main {

	/**
	 * M�todo principal que abre la consola
	 * @param args par�metro por defecto
	 * @throws ClassNotFoundException si no encuentra la clase
	 * @throws IOException si encuentra una excepci�n
	 * @throws SQLException si no encuentra los datos en bd
	 */
	public static void main(String[] args) throws ClassNotFoundException, IOException, SQLException {
		// TODO Auto-generated method stub
		ConsolaInicial c = new ConsolaInicial(new CalculaPuntuaciones());
	}

}
