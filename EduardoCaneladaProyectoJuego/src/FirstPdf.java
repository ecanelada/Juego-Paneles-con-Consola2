import java.awt.Color;
import java.awt.Desktop;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Sides;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.itextpdf.text.Anchor;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chapter;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Jpeg2000.ColorSpecBox;
import com.itextpdf.text.List;
import com.itextpdf.text.ListItem;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Section;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.MailcapCommandMap;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

/**
 * 
 * @author Edu2
 *
 */
public class FirstPdf extends DatosJugador{
  private  String FILE = "InfoYPuntuaciones.pdf";
  private  Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18,
      Font.BOLD);
  private  Font textFont = new Font(Font.FontFamily.TIMES_ROMAN, 12,
      Font.NORMAL/*, BaseColor.RED*/);
  private  Font subFont = new Font(Font.FontFamily.TIMES_ROMAN, 16,
      Font.BOLD);
  private  Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12,
      Font.BOLD);

  JPanel pnl;
  CalculaPuntuaciones cp;
  
  /**
   * constructor sobrecargado
   * @param s objeto de la clase STATS, obtiene la gr�fica
   * @param c objeto de la clase CalculaPuntuaciones
   * @see #getMail()
   */
  public FirstPdf(JPanel s, CalculaPuntuaciones c) {
	  super();
	  pnl = s;
	  cp=c;
    try {
      Document document = new Document(PageSize.A4);
      PdfWriter.getInstance(document, new FileOutputStream(FILE));
      document.open();
      addMetaData(document);
      addTitlePage(document);
      addContent(document);
      addImg(document);
      document.close();
      getMail();
    } catch (Exception e) {
      e.printStackTrace();
    }
    mostrarPDF();
  }
  
  /**
   * Muestra el pdf creado en el buscador
   */
  private void mostrarPDF(){
	  if (Desktop.isDesktopSupported()) {
	        try {
	            File myFile = new File(FILE);
	            Desktop.getDesktop().open(myFile);
	        } catch (IOException ex) {
	            // no application registered for PDFs
	        }
	    }
  	}
  
  	/**
  	 * Pide al usuario su cuenta de correo electr�nico para enviarle los resultados de sus partidas
  	 * @see #enviaMail(String)
  	 */
  	private void getMail(){
  		boolean esMail=false;
  		Pattern pattern;
  		Matcher matcher;
  		//regex para emails
  		String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
  		//compila el regex
  		pattern = Pattern.compile(EMAIL_PATTERN);
  	    Object[] message = {"Introduzca el e-mail al que quiera enviar su informe"};
  	    String option = null;
  	    String mail="";
  	    while (esMail==false){
  	    	option = JOptionPane.showInputDialog(null, message, "Email para enviar pdf de puntuaciones", JOptionPane.OK_CANCEL_OPTION);
  	    	if (option!=null){
  	    		mail = option;
  	    		matcher = pattern.matcher(mail);
  				esMail = matcher.matches();
  	    	} else {
  	    		esMail=true;
  	    	}
  	    }
  	    if (esMail && option!=null) {
  	    	try {
				enviaMail(mail);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
  	    }
  	}
  	
  	/**
  	 * Env�a por email el pdf generado
  	 * @param mail
  	 * @throws IOException
  	 */
  	private void enviaMail(String mail) throws IOException{
  		final String username = "7jfinn@gmail.com"; //cuenta de correo de gmail. debe habilitarse el nivel de seguridad para que permita acceso a aplicaciones menos seguras
  									//al hacer la primera prueba de env�o recibir�s en la cuenta un email que te permite hacerlo
        final String password = "636589791"; //contrase�a

        //evita los errores
        MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap(); 
        mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html"); 
        mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml"); 
        mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain"); 
        mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed"); 
        mc.addMailcap("message/rfc822;; x-java-content- handler=com.sun.mail.handlers.message_rfc822");
        
        //configura la conexi�n
        Properties props = new Properties();
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        //conecta con el correo electr�nico desde el que se va a envair el correo
        Session session = Session.getInstance(props,
          new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
          });

        //crea el email
        try {
            // Create a default MimeMessage object.
            Message message = new MimeMessage(session);

            // Set From: vuelve a introducir el correo para que aparezca qui�n lo envi�
            message.setFrom(new InternetAddress(username));

            // Set To: indica la persona a la que se le env�a
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(mail));

            // Set Subject: asunto del email
            message.setSubject("Puntuaciones del juego");

            // para crear un mensaje en el email
            MimeBodyPart messageBodyPart = new MimeBodyPart();

            // el mensaje
            messageBodyPart.setText("PDF con tus puntaciones");

            // empieza el adjuntar c�digo
            Multipart multipart = new MimeMultipart();

            // establece el nombre del archivo a adjuntar
            multipart.addBodyPart(messageBodyPart);
            String filename = "InfoYPuntuaciones.pdf";
            
            // transforma el documento para que lo adjunte al correo
            messageBodyPart = new MimeBodyPart();
            messageBodyPart.setHeader("Content-Transfer-Encoding", "base64");
            FileDataSource fileDataSource = new FileDataSource(filename);
            messageBodyPart.setDataHandler(new DataHandler(fileDataSource));
            messageBodyPart.setFileName(filename);
            multipart.addBodyPart(messageBodyPart);
            
            

            // a�ade los nuevos contenidos al mensaje
            message.setContent(multipart);

            //necesario para que funcione
            Thread.currentThread().setContextClassLoader( getClass().getClassLoader() );
            
            // env�a el correo
            Transport.send(message);

            //indica si se ha enviado el correo
            JOptionPane.showMessageDialog(null, "E-mail enviado...");
     
         } catch (MessagingException e) {
            throw new RuntimeException(e);
         }
  	}

  /**
   * A�ade los metadatos al pdf (en archivo > propiedades del visor de pdf)
   * @param document
   */
  private  void addMetaData(Document document) {
    document.addTitle("Puntuaciones generales");
    document.addSubject("Formato PDF");
    document.addKeywords("Java, PDF, iText, juego cuadros");
    document.addAuthor("Eduardo Canelada Purcell");
    document.addCreator("Caballinos");
  }

  /**
   * Crea el contenido de la primera p�gina del pdf
   * @param document
   * @throws DocumentException
   * @see #addEmptyLine()
   */
  private  void addTitlePage(Document document)
      throws DocumentException {
    Paragraph preface = new Paragraph();
    // linea vacia
    addEmptyLine(preface, 1);
    // cabecera
    preface.add(new Paragraph("Puntuaciones generales del juego", catFont));

    addEmptyLine(preface, 1);
    // Will create: Report generated by: _name, _date
    preface.add(new Paragraph("Informe generado por: " + getNomId() + ", Fecha de memoria " + new Date(), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        smallBold));
    addEmptyLine(preface, 3);
    preface.add(new Paragraph("Introducci�n",
        smallBold));

    addEmptyLine(preface, 2);

    
    //p�rrafo con alineaci�n justificada
    Paragraph p = new Paragraph("    Recupera el legado hist�rico de los Caballino. El Doctor P. Cab. Belerofonte, Director de la Academia de Ciencia e Historia de la "
    		+ "Capital del Imperio Caballino en el a�o 4659, ha estado a la cabeza de la investigaci�n sobre el efecto de los hilos temporales en las capacidades "
    		+ "cognitivas y sus derivaciones socioculturales, y c�mo afecta el desarrollo del l�bulo atemporal a la toma de decisiones 'por instinto'. \n\n   Es el a�o "
    		+ "4672 y el Dr. C. Cab. Babieca y su equipo de tecnocaballin�logos han puesto en marcha las pruebas con seres caballino org�nicos de tercera generaci�n"
    		+ ". La Recua Imperial ha dictaminado que los primeros sujetos de prueba ser�n soldados. Sin embargo la lista de espera ya supera el millar y tu �nica "
    		+ "manera de acceder es como esp�a de la Guerrilla Salvaje. �Su motivo para ayudarte? Desconocido, su �nica exigencia es que presentes informes actualizados "
    		+ "tras cada una de tus inmersiones.", textFont);
    p.setAlignment(Element.ALIGN_JUSTIFIED_ALL);
    
    preface.add(p);
    
    
    addEmptyLine(preface, 3);
    preface.add(new Paragraph("La misi�n, c�mo jugar",
        smallBold));

    addEmptyLine(preface, 2);

    
    //p�rrafo con alineaci�n justificada. EXPLICACI�N
    Paragraph p1 = new Paragraph("    Tu objetivo ser� navegar por tu memoria en b�squeda de memorias bloqueadas. Con la ayuda de una sustancia oxidante, el DCP-H007, "
    		+ "atacar�s el nodo da�ado para liberarlo. Al final de la sesi�n la m�quina del Dr. Babieca se encargar� de almacenar la informaci�n desbloqueada. �Cuidado!"
    		+ " el DCP-H007 es una sustancia que ataca a los tejidos de forma selectiva, por lo que de aplicarse sobre el nodo de memoria sin la capa de bloqueo echar� "
    		+ "�ste nodo a perder y habr� que reiniciar el proceso de desbloqueo. �Mucha suerte Soldado, confiamos en ti!", textFont);
    p1.setAlignment(Element.ALIGN_JUSTIFIED_ALL);
    
    preface.add(p1);
    

    document.add(preface);
    // empieza nueva p�gina
    document.newPage();
  }

  /**
   * A�ade el contenido de las siguientes p�ginas a la primera
   * @param document
   * @throws DocumentException
   * @see #createTable()
   * @see #addEmptyLine()
   */
  private  void addContent(Document document) throws DocumentException {
    Anchor anchor = new Anchor("Tabla de puntuaciones globales", catFont);
    anchor.setName("First Chapter");

 // el segundo par�metro es el n�mero del cap�tulo
    Chapter catPart = new Chapter(new Paragraph(anchor), 1);

    Paragraph subPara = new Paragraph("Relaci�n de efectividad del proceso en el soldado", subFont);
    Section subCatPart = catPart.addSection(subPara);
    subCatPart.add(new Paragraph("An�lisis del avance del soldado desbloqueando nodos de memoria"));
    
    Paragraph paragraph = new Paragraph();
    addEmptyLine(paragraph, 2);
    subCatPart.add(paragraph);

    // a�ade tabla
    createTable(subCatPart);

    // a�ade lo anterior al documento
    document.add(catPart);

    // siguiente secci�n
    anchor = new Anchor("Gr�fico de puntuaciones", catFont);
    anchor.setName("Second Chapter");

    // el segundo par�metro es el n�mero del cap�tulo
    catPart = new Chapter(new Paragraph(anchor), 2);

    subPara = new Paragraph("Puntuaciones de "+getNomId(), subFont);
    subCatPart = catPart.addSection(subPara);
    subCatPart.add(new Paragraph("Muestra el gr�fico de avance en la misi�n"));

    // a�ade lo anterior al documento
    document.add(catPart);

  }

  /**
   * Crea la tabla de contenidos
   * @param subCatPart
   * @throws BadElementException
   * @see #printTablaJugadores()
   */
  private  void createTable(Section subCatPart)
      throws BadElementException {
    PdfPTable table = new PdfPTable(3);

    PdfPCell c1 = new PdfPCell(new Phrase("Nombre del soldado"));
    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
    table.addCell(c1);

    c1 = new PdfPCell(new Phrase("Puntos logrados"));
    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
    table.addCell(c1);

    c1 = new PdfPCell(new Phrase("Efectividad"));
    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
    table.addCell(c1);
    table.setHeaderRows(1);

    try {
		pintaTablaJugadores(table);
	} catch (NumberFormatException | ClassNotFoundException | SQLException
			| IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    

    subCatPart.add(table);

  }
  
  /**
   * Imprime la tabla con la informaci�n sobre los jugadores
   * @param table
   * @throws SQLException
   * @throws NumberFormatException
   * @throws ClassNotFoundException
   * @throws IOException
   */
  private void pintaTablaJugadores(PdfPTable table) throws SQLException, NumberFormatException, ClassNotFoundException, IOException{
	 String nombresEId = cp.getNombreEIdJugadores();
	 String [][] nombresEIdProcesados = cp.procesarNombreEId(nombresEId);
	 PdfPCell cell1=null, cell2=null, cell3=null;
	 BaseColor miColor = new BaseColor(218,165,32);
	 for (int i=0;i<nombresEIdProcesados.length;i++){
		 cp.calculaPuntosTotalJugador(Integer.parseInt(nombresEIdProcesados[i][1]));
		 cell1 = new PdfPCell(new Paragraph(""+nombresEIdProcesados[i][0]));
		 cell2 = new PdfPCell(new Paragraph(""+cp.getPuntosIndividuales()));
		 cell3 = new PdfPCell(new Paragraph(""+cp.calculaPorcentajeEfectividad(nombresEIdProcesados, i)));
		 cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
		 cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
		 cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
		 if (getIDJugador()==Integer.parseInt(nombresEIdProcesados[i][1])){
			 cell1.setBackgroundColor(miColor);
			 cell2.setBackgroundColor(miColor);
			 cell3.setBackgroundColor(miColor);
		 }
		 table.addCell(cell1);
		 table.addCell(cell2);
		 table.addCell(cell3);
	 }
  }

  	/**
  	 * Crea l�neas vac�as
  	 * @param paragraph
  	 * @param number
  	 */
  	private  void addEmptyLine(Paragraph paragraph, int number) {
  		for (int i = 0; i < number; i++) {
  			paragraph.add(new Paragraph(" "));
  		}
  	}
  
  	/**
  	 * a�ade una imagen al pdf
  	 * @param doc
  	 */
  	private void addImg(Document doc){
	 
  		BufferedImage img = new BufferedImage(pnl.getWidth(), pnl.getHeight(), BufferedImage.TYPE_INT_ARGB); 
  		Graphics2D img2D = img.createGraphics(); 
  		pnl.print(img2D); 
  		float viewWidth = doc.getPageSize().getWidth() - doc.leftMargin() - doc.rightMargin(); 
  		double scaleX = viewWidth / pnl.getWidth(); 
  		img2D.scale(scaleX, scaleX);	// keep aspect ratio 
  		
  		Paragraph p = new Paragraph(); 
  		Image itextImg;
  		try {
  			itextImg = Image.getInstance(img, Color.WHITE, false);
  			p.add( itextImg ); 
  			doc.add( p );
  		} catch (BadElementException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}   
  	}
  
  	/**
   	* devuelve el nombre y el Id del jugador en un string
   	* @return string
   	*/
  	private String getNomId() {
  		return getNomJugador() +" con c�digo de afiliaci�n: "+getIDJugador();
	}
} 