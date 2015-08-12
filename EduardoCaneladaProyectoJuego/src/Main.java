import java.io.IOException;
import java.sql.SQLException;

public class Main {

	/**
	 * Método principal que abre la consola
	 * @param args parámetro por defecto
	 * @throws ClassNotFoundException si no encuentra la clase
	 * @throws IOException si encuentra una excepción
	 * @throws SQLException si no encuentra los datos en bd
	 */
	public static void main(String[] args) throws ClassNotFoundException, IOException, SQLException {
		// TODO Auto-generated method stub
		ConsolaInicial c = new ConsolaInicial(new CalculaPuntuaciones());
	}

}
