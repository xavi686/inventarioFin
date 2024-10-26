package inventario.org.inventario.com;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

class BuscarExistencias{

	private String referencia;
	private String itemcode;
    private String lote;
    private String fecha;
    private int total;

    public BuscarExistencias(String referencia, String itemcode, String lote, String fecha, int total) {
        this.referencia = referencia;
        this.itemcode = itemcode;
        this.lote = lote;
        this.fecha = fecha;
        this.total = total;
    }

    public String getReferencia() {
        return referencia;
    }

    public String getItemcode() {
        return itemcode;
    }

    public String getLote() {
        return lote;
    }

    public String getFecha() {
        return fecha;
    }

    public int getTotal() {
        return total;
    }
}

class VerificarExistencias{
	private Connection connection;
	private String grupo;
    private int caja;
    private int num_conteo;

    public VerificarExistencias(Connection connection, String grupo, int caja, int num_conteo) {
        this.connection = connection;
        this.grupo = grupo;
        this.caja = caja;
        this.num_conteo = num_conteo;
    }

    public VerificarExistencias(Connection connection, String grupo, int num_conteo) {
        this.connection = connection;
        this.grupo = grupo;
        this.num_conteo = num_conteo;
    }

    public Connection getConnection() {
        return connection;
    }

    public String getGrupo() {
        return grupo;
    }

    public int getCaja() {
        return caja;
    }

    public int getNum_conteo() {
        return num_conteo;
    }
}

//public class Interfaz extends javax.swing.JFrame {
public class InterfazConteo extends JFrame {

	private static final String URL = "jdbc:mysql://localhost:3314/inventario";
    private static final String USER = "root";  // Cambia esto a tu usuario de MySQL
    private static final String PASSWORD = "root";  // Cambia esto a tu contraseña de MySQL
    

	private static final String origen = "C:\\Users\\javie\\OneDrive\\Documents\\VJ Cardiosistemas\\Inventario\\inv.xls";

    public static BuscarExistencias buscarInfo(Connection connection, String codigobarra, String grupo, int caja, int num_conteo, int total) {

    	if (codigobarra.length() < 22) {
            System.out.println("El código de barras es demasiado corto.");
        }

        // Extraer los 16 primeros caracteres
        String codigo = codigobarra.substring(0, 16);

        // Verificar los caracteres en las posiciones 16 y 17 (índice 15 y 16)
        if (!codigobarra.substring(16, 18).equals("00")) {
            System.out.println("Error: Los caracteres en las posiciones 17 y 18 no son '00'.");
        }

        // Extraer la fecha (6 caracteres después de "00")
        String fecha = codigobarra.substring(18, 24);

        // Verificar los caracteres en las posiciones 24 y 25 (índice 23 y 24)
        if (!codigobarra.substring(24, 26).equals("10")) {
            System.out.println("Error: Los caracteres en las posiciones 25 y 26 no son '10'.");
        }

        // Extraer el lote (resto de los caracteres después de "10")
        String lote = codigobarra.substring(26);

        //Muestra los datos extraidos del código de barras
        System.out.println("Código: " + codigo);
        System.out.println("Fecha: " + fecha);
        System.out.println("Lote: " + lote);
        
        String itemcode = "";
        String referencia = "";
 //       int total = 0;

        // Buscar en el archivo de Excel
        try {
            Workbook workbook = Workbook.getWorkbook(new File("C:\\Users\\javie\\OneDrive\\Documents\\VJ Cardiosistemas\\Inventario\\inv.xls"));
            Sheet sheet = workbook.getSheet(0);
            boolean encontrado = false;
            for (int i = 0; i < sheet.getRows(); i++) {
                Cell cellCodigo = sheet.getCell(0, i);
                Cell cellLote = sheet.getCell(1, i);
                if (cellCodigo != null && cellLote != null && cellCodigo.getContents().trim().equals(codigo) && cellLote.getContents().trim().equals(lote)) {
                    System.out.println("Datos encontrados:");
                    for (int j = 0; j < 5; j++) {
                        Cell cell = sheet.getCell(j, i);
                        System.out.print(cell.getContents() + "\t");
                    }
	                System.out.println("\n");
	                Cell celda = sheet.getCell(2, i);
	                itemcode = celda.getContents().trim();
	                //System.out.println(itemcode + "\n");
	                celda = sheet.getCell(3, i);
	                referencia = celda.getContents().trim();
	                //System.out.println(referencia + "\n");
	                celda = sheet.getCell(5, i);
	                total = Integer.parseInt(celda.getContents().trim()); //las existentes en sistema
	                //System.out.println(cantidad + "\n");
	   			    //System.out.println();
                    encontrado = true;
                }
            }
            if (encontrado==true) {
//Con este sólo guardo los primeros 16 caracteres del código de barras
            	return new BuscarExistencias(referencia, itemcode, lote, fecha, total);
            }

            if (!encontrado) {
                System.out.println("No se encontraron coincidencias para el código " + codigo + " y lote " + lote + " en el archivo de Excel.");
            }
            workbook.close();
        } catch (IOException | BiffException e) {
            e.printStackTrace();
        }
    	return new BuscarExistencias(referencia, itemcode, lote, fecha, total);
    }

    public static VerificarExistencias obtenerInfo(Connection connection, String grupo, int caja) {
    	int num_conteo = 1;
        String conteo_query = "SELECT * FROM cajas WHERE caja = " + caja; //revisa del listado de las cajas y no desde conteo.
        try (Statement statement = connection.createStatement();
            	ResultSet resultSet = statement.executeQuery(conteo_query)) {
            while (resultSet.next()) {
                    num_conteo = resultSet.getInt("num_conteo");
                    if(!grupo.equals(resultSet.getString("grupo"))){
                        System.out.println("La caja fue revisada por el grupo "+
                        		resultSet.getString("grupo"));
                        grupo = resultSet.getString("grupo");
                    }
            	}
        }catch (SQLException e){
            e.printStackTrace();
        }
        System.out.println("Este es el conteo que lleva: " + num_conteo);
    	return new VerificarExistencias(connection, grupo, num_conteo);
    }
	
    public InterfazConteo() {
        initComponents();
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        String[] grupo = {"","A", "B", "C", "D"};
        JComboBox<String> jComboBox1 = new javax.swing.JComboBox<>(grupo);
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jTextCaja = new javax.swing.JTextField(); //
        jLabel3 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jTextConteo = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();

        JLabel jLabelProducto = new JLabel("Producto:");
        JTextField jTextFieldProducto = new JTextField();
        JLabel jLabelCantidad = new JLabel("Cantidad:");
        JTextField jTextFieldCantidad = new JTextField();
        JLabel jLabelReferencia = new JLabel("Referencia:");
        JTextField jTextFieldReferencia = new JTextField();
        JLabel jLabelItemcode = new JLabel("Item Code:");
        JTextField jTextFieldItemcode = new JTextField();
        JLabel jLabelLote = new JLabel("Lote:");
        JTextField jTextFieldLote = new JTextField();
        JLabel jLabelFechaVencimiento = new JLabel("Fecha de Vencimiento:");
        JTextField jTextFieldFechaVencimiento = new JTextField();
        JTextField jTextFieldTotal = new JTextField();
        JButton jButtonBuscar = new JButton("Buscar");
        jButtonBuscar.setEnabled(false);
        JButton jButtonGuardar = new JButton("Guardar");
        JButton jButtonInforme = new JButton("Informe");
        JButton jButtonBorrar = new JButton("Borrar");
        JButton jButtonSalir = new JButton("Salir");
        //JCheckBox checkRecontar = new JCheckBox(); 
        //JLabel jRecontar = new JLabel("Recontar:");

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setText("Conteo General");
        jLabel2.setText("Grupo:");
        jLabel3.setText("Caja:");
        jLabel4.setText("Conteo:");
        jButton1.setText("Agregar");
        
        // Set default sizes for JTextFields
        Dimension textFieldSize = new Dimension(200, 24);
        jTextCaja.setPreferredSize(textFieldSize); //número de caja
//        jTextConteo.setEditable(false);
        jTextConteo.setPreferredSize(textFieldSize);
        jTextFieldProducto.setPreferredSize(textFieldSize);
        jTextFieldCantidad.setPreferredSize(textFieldSize);
        jTextFieldReferencia.setPreferredSize(textFieldSize);
        jTextFieldItemcode.setPreferredSize(textFieldSize);
        jTextFieldProducto.setPreferredSize(textFieldSize);
        jTextFieldLote.setPreferredSize(textFieldSize);
        jTextFieldFechaVencimiento.setPreferredSize(textFieldSize);
        jTextFieldProducto.setEditable(true);
        jTextFieldCantidad.setEditable(true);
        jTextFieldReferencia.setEditable(false);
        jTextFieldItemcode.setEditable(false);
        jTextFieldLote.setEditable(false);
        jTextFieldFechaVencimiento.setEditable(false);
        jTextFieldTotal.setPreferredSize(textFieldSize);
        jTextFieldTotal.setEditable(false);
//        jTextFieldTotal.setVisible(false);
        
        getContentPane().setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);  

        gbc.gridx = 0;
        gbc.gridy = 0;
        getContentPane().add(jLabel1, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        getContentPane().add(jLabel2, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        getContentPane().add(jComboBox1, gbc);

        gbc.gridx = 2;
        gbc.gridy = 1;
        getContentPane().add(jLabel3, gbc);

        gbc.gridx = 3;
        gbc.gridy = 1;
        getContentPane().add(jTextCaja, gbc);

        gbc.gridx = 4;
        gbc.gridy = 1;
        getContentPane().add(jLabel4, gbc);

        gbc.gridx = 5;
        gbc.gridy = 1;
        getContentPane().add(jTextConteo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        getContentPane().add(jLabelProducto, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        getContentPane().add(jTextFieldProducto, gbc);
        
        gbc.gridx = 2;
        gbc.gridy = 3;
        getContentPane().add(jButtonBuscar, gbc);
        
        gbc.gridx = 3;
        gbc.gridy = 3;
        getContentPane().add(jTextFieldTotal, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        getContentPane().add(jLabelCantidad, gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        getContentPane().add(jTextFieldCantidad, gbc);

        gbc.gridx = 2;
        gbc.gridy = 4;
        getContentPane().add(jButtonGuardar, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 5;
        getContentPane().add(jLabelReferencia, gbc);

        gbc.gridx = 1;
        gbc.gridy = 5;
        getContentPane().add(jTextFieldReferencia, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        getContentPane().add(jLabelItemcode, gbc);

        gbc.gridx = 1;
        gbc.gridy = 6;
        getContentPane().add(jTextFieldItemcode, gbc);

        gbc.gridx = 0;
        gbc.gridy = 7;
        getContentPane().add(jLabelLote, gbc);

        gbc.gridx = 1;
        
        gbc.gridy = 7;
        getContentPane().add(jTextFieldLote, gbc);

        gbc.gridx = 0;
        gbc.gridy = 8;
        getContentPane().add(jLabelFechaVencimiento, gbc);

        gbc.gridx = 1;
        gbc.gridy = 8;
        getContentPane().add(jTextFieldFechaVencimiento, gbc);

        gbc.gridx = 0;
        gbc.gridy = 9;
        getContentPane().add(jButton1, gbc);

        gbc.gridx = 1;
        gbc.gridy = 9;
        getContentPane().add(jButtonInforme, gbc);

        gbc.gridx = 2;
        gbc.gridy = 9;
        getContentPane().add(jButtonBorrar, gbc);

        gbc.gridx = 3;
        gbc.gridy = 9;
        getContentPane().add(jButtonSalir, gbc);

        pack();
        
        //VERIFICA SI SE INGRESA UN DIGITO
        jTextCaja.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c)) {
                    e.consume();  // Ignora la entrada si no es un dígito
                }
            }
        });
        
        jTextConteo.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c)) {
                    e.consume();  // Ignora la entrada si no es un dígito
                }
            }
        });

        jTextFieldProducto.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c)&&!Character.isAlphabetic(c)) {
                    e.consume();  // Ignora la entrada si no es un dígito
                }
            }
        });

        // Añadir un DocumentListener al JTextField de Producto para contar la longitud de caracteres mayor a 20
        jTextFieldProducto.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateButtonState();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateButtonState();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateButtonState();
            }

            private void updateButtonState() {
                // Habilitar o deshabilitar el botón según la longitud del texto
                if (jTextFieldProducto.getText().length() >= 20) {
                	jButtonBuscar.setEnabled(true);
                } else {
                	jButtonBuscar.setEnabled(false);
                }
            }
        });
        
        jTextFieldCantidad.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c)) {
                    e.consume();  // Ignora la entrada si no es un dígito
                }
            }
        });

        //BUSCAR BOTÓN
        jButtonBuscar.addActionListener(new ActionListener() {
       	 @Override
//            public void actionPerformed(ActionEvent e, Connection connection) {
            public void actionPerformed(ActionEvent e) {
                // Acción a realizar cuando se haga clic en el botón
                String grupo = (String) jComboBox1.getSelectedItem(); //().toUpperCase().trim(); // leer del jcombobox
                if(jTextConteo.getText().trim().isEmpty()||jTextCaja.getText().trim().isEmpty()||
                		jTextFieldProducto.getText().trim().isEmpty()||
                		grupo.trim().isEmpty()) {
                	//No hace nada
                    System.out.println("¡Seleccione el grupo asignado!");
                    //ingrese el número de la caja
                    //ingrese el código de producto
                    //ingrese el número de conteo
                }
                else{ 
                try {
        			Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
                    grupo = (String) jComboBox1.getSelectedItem(); //().toUpperCase().trim(); // leer del jcombobox
                    int num_conteo = Integer.parseInt(jTextConteo.getText()); //().toUpperCase().trim(); // leer del jcombobox
                    //Verificar si algún grupo ya leyó la caja ingresada
                    int caja = Integer.parseInt(jTextCaja.getText());
                    int total = 0;
           	        System.out.println("Conexión a MySQL establecida.");
                    VerificarExistencias verifica = obtenerInfo(connection, grupo, caja);
                    String infoMessage;
                    if(grupo.equals(verifica.getGrupo()) && num_conteo==verifica.getNum_conteo()) {
//                    if(grupo.equals(verifica.getGrupo())) { //original
                        num_conteo = verifica.getNum_conteo();
                        String codigobarra = jTextFieldProducto.getText().trim();
                        BuscarExistencias buscar = buscarInfo(connection, codigobarra, grupo, caja, num_conteo, total);
                        jTextFieldReferencia.setText(buscar.getReferencia());
                        jTextFieldItemcode.setText(buscar.getItemcode());
                        jTextFieldLote.setText(buscar.getLote());
                        jTextFieldFechaVencimiento.setText(buscar.getFecha());
                        jTextFieldTotal.setText(Integer.toString(buscar.getTotal()));
                        //proceso_contar(connection, codigobarra, grupo, caja, num_conteo);
                    }else if(!grupo.equals(verifica.getGrupo()) && num_conteo==verifica.getNum_conteo()) {
                    	System.out.print("El grupo " + verifica.getGrupo() + " ya contó esta caja. \n");
                        System.out.print("Por favor tomar otra caja! \n");
                        infoMessage = "Caja revisada por el grupo "+ verifica.getGrupo() + "\n"
                              		+"Por favor tomar otra caja!";
                        JOptionPane.showMessageDialog(new JFrame(), infoMessage, "Advertencia", JOptionPane.ERROR_MESSAGE);                        
                    }else if(grupo.equals(verifica.getGrupo()) && !(num_conteo==verifica.getNum_conteo())) {
                    	infoMessage = "El conteo que lleva es: " + verifica.getNum_conteo();
                    	JOptionPane.showMessageDialog(new JFrame(), infoMessage, "Advertencia", JOptionPane.ERROR_MESSAGE);                        
                    }else {
                        infoMessage = "Caja revisada por el grupo "+ verifica.getGrupo() + "\n"
                        		+"Por favor tomar otra caja! \n" +
                        		"El conteo que lleva es: " + verifica.getNum_conteo();
                        JOptionPane.showMessageDialog(new JFrame(), infoMessage, "Advertencia", JOptionPane.ERROR_MESSAGE);                        
                    }
                } catch (SQLException ex) {
        			ex.printStackTrace();
        		}
       	 }
       	 }
       });

        //GUARDAR EL CONTEO
        jButtonGuardar.addActionListener(new ActionListener() {
          	 @Override
           public void actionPerformed(ActionEvent e) {
                 String grupo = (String) jComboBox1.getSelectedItem(); //().toUpperCase().trim(); // leer del jcombobox
          		 if(jTextConteo.getText().trim().isEmpty()||jTextCaja.getText().trim().isEmpty()
          				 ||jTextFieldProducto.getText().trim().isEmpty()||
          				 grupo.trim().isEmpty()||jTextFieldCantidad.getText().trim().isEmpty()
          				||jTextFieldReferencia.getText().trim().isEmpty()) {
                 	//No hace nada
                 }
               // Acción a realizar cuando se haga clic en el botón
                 else {
               try {
       			Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
//                   String grupo = (String) jComboBox1.getSelectedItem(); //().toUpperCase().trim(); // leer del jcombobox
                   grupo = (String) jComboBox1.getSelectedItem(); //().toUpperCase().trim(); // leer del jcombobox
                   int caja = Integer.parseInt(jTextCaja.getText());
                   int num_conteo = Integer.parseInt(jTextConteo.getText()); //().toUpperCase().trim(); // leer del jcombobox
                   String codebars = jTextFieldProducto.getText().substring(0, 16);
                   int cantidad = Integer.parseInt(jTextFieldCantidad.getText());
                   String referencia = jTextFieldReferencia.getText();
                   String itemcode = jTextFieldItemcode.getText();
                   String batchnum = jTextFieldLote.getText();
                   String vencimiento = jTextFieldFechaVencimiento.getText();
                   int total = Integer.parseInt(jTextFieldTotal.getText());
                   //debo guardar total en una variable temporal o cargarla en un text que no sea visible 
                   //o esté oculto
                   //OJO ESTE ES EL ORIGINAL Y FUNCIONA
                   //conteo(connection, codebars, batchnum, itemcode, referencia, vencimiento, total, 
                   //		   cantidad, grupo, caja, num_conteo);
                   //////////////OJO ESTE ES SÓLO PARA PRUEBAS
                   conteo(connection, codebars, batchnum, itemcode, referencia, vencimiento, total, 
                		   cantidad, grupo, caja, num_conteo);
                   //////////////OJO ESTE ES SÓLO PARA PRUEBAS

                   jTextFieldProducto.setText(null);
                   jTextFieldCantidad.setText(null);
                   jTextFieldReferencia.setText(null);
                   jTextFieldItemcode.setText(null);
                   jTextFieldLote.setText(null);
                   jTextFieldFechaVencimiento.setText(null);
//                   jTextFieldTotal.setText(null);
                   
               } catch (SQLException ex) {
       			ex.printStackTrace();
       		}
      	 }
          	 }	
        }
        	
        	    );

        //GENERAR INFORME
        jButtonInforme.addActionListener(new ActionListener() {
          	 @Override
           public void actionPerformed(ActionEvent e) {
               // Acción a realizar cuando se haga clic en el botón
               try {
                   String grupo = (String) jComboBox1.getSelectedItem(); //().toUpperCase().trim(); // leer del jcombobox
            	   Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            	   if(!jTextConteo.getText().trim().isEmpty()&&!jTextCaja.getText().trim().isEmpty()&&
                   		!grupo.isEmpty()) {                   
            	   //String grupo = (String) jComboBox1.getSelectedItem(); //().toUpperCase().trim(); // leer del jcombobox
                   int caja = Integer.parseInt(jTextCaja.getText());
                   int num_conteo = Integer.parseInt(jTextConteo.getText()); //().toUpperCase().trim(); // leer del jcombobox
            	   reporte(connection, caja, grupo, num_conteo);
            	   }
               } catch (SQLException ex) {
       			ex.printStackTrace();
       		}
      	 }
        	
        });

        //SALIR DE LA INTERFAZ
        jButtonSalir.addActionListener(new ActionListener() {
          	 @Override
           public void actionPerformed(ActionEvent e) {
          		dispose();
          	 }	
        });
        

    }
        
//    }

    

    public static void main(String args[]) {
                
    	new InterfazConteo().setVisible(true);

        int num_conteo = 0;

        try {
			Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
   	        System.out.println("Conexión a MySQL establecida.");
            String codigobarra;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//    	        int num_conteo = 0;
    }

    //Está funcionando
    private static void conteo(Connection connection, String codebars, String batchnum, String itemcode,
    		String referencia, String vencimiento, int total, int cantidad, String grupo, int caja,
    		int num_conteo) {
    	
    	//Verificar en la tabla si la caja existe
    	String verifica_caja = "SELECT * FROM cajas where caja = " + caja;
        try (PreparedStatement selectStatement = connection.prepareStatement(verifica_caja)) {
        	ResultSet resultSet = selectStatement.executeQuery(verifica_caja);
        	if(!resultSet.isBeforeFirst()) { //agregar en la tabla cajas si no existe
                String insertar_caja = "INSERT INTO cajas (grupo, caja, num_conteo) VALUES (?, ?, ?)";
                PreparedStatement insertStatement = connection.prepareStatement(insertar_caja);
                insertStatement.setString(1, grupo);
                insertStatement.setInt(2, caja);
                insertStatement.setInt(3, num_conteo);
                insertStatement.executeUpdate();
                insertStatement.close();
        	} else {
//            	int contar = resultSet.getInt("caja"); //si la caja no existe va a aparecer en vacío
                System.out.println(verifica_caja);
        		resultSet.next(); //Pasa a la siguiente línea de lectura del texto
        		int conteo_num = resultSet.getInt("num_conteo");
                System.out.println("Conteo de la caja " + conteo_num);
        		if((conteo_num+1) == num_conteo){ //se agrega el número del reconteo manualmente en 1 unidad
                    String incrementarConteo = "UPDATE cajas SET num_conteo = " + num_conteo;
                    PreparedStatement incrementarStatement = connection.prepareStatement(incrementarConteo);
                    incrementarStatement.executeUpdate();
                    incrementarStatement.close();
        		}
        	}
 
            String insertar_conteo = "INSERT INTO conteo (codebars, batchnum, itemcode, referencia, "
            		+ "vencimiento, total, cantidad, grupo, caja, "
            		+ "fecha, num_conteo, hora) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement statement = connection.prepareStatement(insertar_conteo);
            statement.setString(1, codebars);
            statement.setString(2, batchnum);
            statement.setString(3, itemcode);
            statement.setString(4, referencia);
            statement.setString(5, vencimiento);
            statement.setInt(6, total);
            statement.setInt(7, cantidad);
            statement.setString(8, grupo);
            statement.setInt(9, caja);
            statement.setDate(10, new Date(System.currentTimeMillis()));
            statement.setInt(11, num_conteo);
            statement.setTimestamp(12, Timestamp.valueOf(LocalDateTime.now()));
            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Conteo agregado exitosamente.");
                String infoMessage = "Conteo agregado!";
                JOptionPane.showMessageDialog(null, infoMessage, "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                System.out.println("No se pudo agregar! Por favor verifique!.");
                String infoMessage = "No se pudo agregar el conteo!";
                JOptionPane.showMessageDialog(null, infoMessage, "Error", JOptionPane.ERROR_MESSAGE);
            }   
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void reconteo(int caja) {
    	//Verificar primero si la caja existe. Si existe borrarla de la tabla conteo
    	//luego actualizar la tabla de las cajas para agregar el grupo, el número de la caja
    	//y el incremento en el conteo + 1 (Aunque mejor que agregue el número del conteo 
    	//manualmente)
    	//Luego cargo el formulario y utilizar la función conteo normal para agregar 
    	//los artículos del reconteo
    	
    	String verifica_caja = "SELECT * FROM cajas WHERE caja = " + caja;
        String conteo_query = "SELECT * FROM conteo WHERE caja = " + caja
        		+ " GROUP BY caja, num_conteo";
        String delete_query = "DELETE FROM conteo WHERE caja = " + caja;
    	
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
        	PreparedStatement selectStatement = connection.prepareStatement(verifica_caja)) {
           	ResultSet resultSet = selectStatement.executeQuery(verifica_caja);
        	if(resultSet.isBeforeFirst()) { //si encuentra la caja en conteo 
            	System.out.println("Encontrado " + conteo_query);
            	resultSet.next();
            	int conteo = resultSet.getInt("num_conteo");
            	conteo++;
            	ResultSet resultSet1 = selectStatement.executeQuery(conteo_query);
            	if(resultSet1.isBeforeFirst()) { //si encuentra la caja en conteo´
                    try (PreparedStatement statement = connection.prepareStatement(delete_query)) {
                        statement.executeUpdate();
                    	String incrementarConteo = "UPDATE cajas SET num_conteo = " + conteo + " WHERE caja = " + caja;
                    	System.out.println(incrementarConteo);
                        PreparedStatement incrementarStatement = connection.prepareStatement(incrementarConteo);
                        incrementarStatement.executeUpdate();
                        incrementarStatement.close();
                    	statement.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
            	}else {
                	System.out.println("No se ha iniciado el conteo de la caja " + caja);
            	}
        	}else{
            	System.out.println("No se ha hecho el conteo de la caja " + caja);
        	}

           	System.out.println("Encontrado " + verifica_caja);

        	selectStatement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void reporte(Connection connection, int caja, String grupo, int num_conteo) {

    	// Ruta del archivo PDF a crear
        String destino = "C:\\Users\\javie\\OneDrive\\Documents\\VJ Cardiosistemas\\Inventario\\mi_pdf.pdf";

        // Crear el documento
        Document document = new Document(PageSize.HALFLETTER.rotate());

//ESTA SENTENCIA ESTÁ BIEN
//        String query = "SELECT batchnum, itemcode, referencia, SUM(total) AS TotalCantidad, num_conteo" +
//                " FROM conteo WHERE caja = " + caja + " GROUP BY batchnum, itemcode, referencia, num_conteo";
        
      String query = "SELECT batchnum, itemcode, referencia, SUM(cantidad) AS TotalCantidad, num_conteo" +
      " FROM conteo WHERE caja = " + caja + " AND grupo = '" + grupo + 
      "' AND num_conteo = " + num_conteo +" GROUP BY batchnum, itemcode, referencia, num_conteo";

//      System.out.println(query);

        try (Statement statement = connection.createStatement();
        	ResultSet resultSet = statement.executeQuery(query)) {
        	
            // Crear el escritor de PDF
            PdfWriter.getInstance(document, new FileOutputStream(destino));

            // Abrir el documento
            document.open();

         // Añadir un título
            document.add(new Paragraph("Informe caja número: " + caja + ",	 Grupo: " + grupo
            		+ ",	 Conteo: " + num_conteo));

        // Crear una tabla con 4 columnas
            PdfPTable table = new PdfPTable(4);

            // Añadir la cabecera de la tabla
            table.addCell(crearCeldaSinBordes("Lote"));
            table.addCell(crearCeldaSinBordes("Código"));
            table.addCell(crearCeldaSinBordes("Referencia"));
            table.addCell(crearCeldaSinBordes("Cantidad"));
            
            while (resultSet.next()) {
                String batchnum = resultSet.getString("batchnum");
                table.addCell(crearCeldaSinBordes(batchnum));
                String itemcode = resultSet.getString("itemcode");
                table.addCell(crearCeldaSinBordes(itemcode));
                String referencia = resultSet.getString("referencia");
                table.addCell(crearCeldaSinBordes(referencia));
                int contados = resultSet.getInt("TotalCantidad");//no TotalCantidad
                table.addCell(crearCeldaSinBordes(Integer.toString(contados)));
                System.out.println("Lote: " + batchnum + ", Código: " + itemcode + ", referencia: " + 
                referencia + "," + "Contados: " + contados);
            }

            // Añadir la tabla al documento
            document.add(table);
            
            // Cerrar el documento
            document.close();
            
            System.out.println("PDF creado exitosamente.");
            
         // Abrir el PDF automáticamente
            if (Desktop.isDesktopSupported()) {
                try {
                    File archivoPDF = new File(destino);
                    if (archivoPDF.exists()) {
                        Desktop.getDesktop().open(archivoPDF);
                    } else {
                        System.out.println("El archivo PDF no existe.");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Desktop no es compatible en este sistema.");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
           }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    	
    }
    
    // Método para crear una celda sin bordes
    private static PdfPCell crearCeldaSinBordes(String contenido) {
        PdfPCell cell = new PdfPCell(new Paragraph(contenido));
        cell.setBorder(PdfPCell.NO_BORDER);
        return cell;
    }

//NO BORRAR ES EL CONTEO DEL CONSOLIDADO
//OJO DEBO REVISAR EL TEMA DE LAS DIFERENCIAS
//HAY UNAS DIFERENCIAS QUE NO CUADRAN
//TENER EN CUENTA QUE SI HAGO RECONTEO DEBO RECALCULAR LAS DIFERENCIAS
//PROBAR SOLUCIÓN ELIMINANDO TABLA DE DESCARGA LUEGO DE UN RECONTEO    
    //    public static void consolidado(Connection connection) {
      public static void consolidado() {

    	String batchnum;
        String itemcode;
        String referencia;
        int cantidad; //las contadas en la caja

        try {
    		Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
        	// Verificar si la tabla está vacía
                String countQuery = "SELECT COUNT(*) FROM descarga";
                PreparedStatement countStatement = connection.prepareStatement(countQuery);
                ResultSet resultSet = countStatement.executeQuery();
                resultSet.next();
                int rowCount = resultSet.getInt(1);

                Workbook workbook = Workbook.getWorkbook(new File(origen));

                // Si la tabla descarga está vacía, copia los datos del archivo descargado de excel 
                if (rowCount == 0) {
                    String insertQuery = "INSERT INTO descarga (referencia, batchnum, codeproduct, cantidad) VALUES (?, ?, ?, ?)";
                    PreparedStatement insertStatement = connection.prepareStatement(insertQuery);

                    Sheet sheet = workbook.getSheet(0);

                    for (int i = 0; i < sheet.getRows(); i++) {
                        Cell cellReferencia = sheet.getCell(3, i);
                        Cell cellLote = sheet.getCell(1, i);
                        Cell cellCodigo = sheet.getCell(2, i);
                        Cell cellCantidad = sheet.getCell(5, i);
    	                referencia = cellReferencia.getContents().trim();
        	            batchnum = cellLote.getContents().trim();
        	            itemcode = cellCodigo.getContents();
       	                cantidad = Integer.parseInt(cellCantidad.getContents().trim()); //las contadas en la caja

                        insertStatement.setString(1, referencia);
                        insertStatement.setString(2, batchnum);
                        insertStatement.setString(3, itemcode);
                        insertStatement.setInt(4, cantidad);
                        insertStatement.executeUpdate();
                    } 
                } else {
                    // Actualizar el valor de la diferencia en caso de requerir reportes
                    String resetQuery = "UPDATE descarga SET conteo = 0";
                    PreparedStatement resetStatement = connection.prepareStatement(resetQuery);
                    int rowsAffected = resetStatement.executeUpdate();
                    System.out.println("Número de filas actualizadas: " + rowsAffected);
                	
                	String query = "UPDATE descarga d " +
                            "JOIN ( " +
//                            "    SELECT referencia, batchnum, SUM(total) AS total_conteo " + //acá sumo el total n veces de la tabla conteo
                            "    SELECT referencia, batchnum, SUM(cantidad) AS total_conteo " + //acá sumo la cantidad contadadel producto
                            "    FROM conteo " + 
                            "    GROUP BY referencia, batchnum " +
                            ") c " +
                            "ON d.referencia = c.referencia AND d.batchnum = c.batchnum " +
                            "SET d.conteo = c.total_conteo";
              	PreparedStatement statement = connection.prepareStatement(query);

             // Ejecutar la sentencia
             int resetRowsAffected = statement.executeUpdate();
             System.out.println("Número de filas afectadas: " + resetRowsAffected);
             
             // Crear la sentencia UPDATE para calcular y actualizar la columna `diferencia`
             String updateDiferenciaQuery = "UPDATE descarga " +
                                            "SET diferencia = cantidad - conteo";
             PreparedStatement updateDiferenciaStatement = connection.prepareStatement(updateDiferenciaQuery);
             int updateDiferenciaRowsAffected = updateDiferenciaStatement.executeUpdate();
             System.out.println("Número de filas actualizadas para diferencia: " + updateDiferenciaRowsAffected);

             imprimirConsolidado(connection);             
             //agregar seguridad en el aplicativo
             //configurar para que sólo permita agregar un limite de caracteres
             //Arreglar para los valores de codigo de barras largos
             //Arreglar para los que no tengan códigos de barras, ingresar por referencia y/o lote
                }

                // Cerrar la conexión
                resultSet.close();
                countStatement.close();
                //connection.close();

                workbook.close();
                 
                }catch (SQLException e) {
                	e.printStackTrace();
                }catch (FileNotFoundException e) {
                	e.printStackTrace();
                }catch (IOException | BiffException e) {
                    e.printStackTrace();
                }
    	}
    
    private static void imprimirConsolidado(Connection connection) {
    
	// Ruta del archivo PDF a crear
    String destino = "C:\\Users\\javie\\OneDrive\\Documents\\VJ Cardiosistemas\\Inventario\\consolidado.pdf";

    // Crear el documento
    Document document = new Document();

    String query = "SELECT * FROM descarga ORDER BY referencia, batchnum";
    		
    try (Statement statement = connection.createStatement();
    	ResultSet resultSet = statement.executeQuery(query)) {
    	
        // Crear el escritor de PDF
        PdfWriter.getInstance(document, new FileOutputStream(destino));

        // Abrir el documento
        document.open();

     // Añadir un título
        document.add(new Paragraph("DIFERENCIAS CONSOLIDADAS INVENTARIO FINAL"));

    // Añadir espacio
        document.add(new Paragraph("\n"));
        
    // Crear una tabla con 5 columnas
        PdfPTable table = new PdfPTable(5);

        // Añadir la cabecera de la tabla
        table.addCell(crearCeldaSinBordes("Referencia"));
        table.addCell(crearCeldaSinBordes("Lote"));
        table.addCell(crearCeldaSinBordes("Existencia"));
        table.addCell(crearCeldaSinBordes("Contado"));
        table.addCell(crearCeldaSinBordes("Diferencia"));
        
        while (resultSet.next()) {
            String referencia = resultSet.getString("referencia");
            table.addCell(crearCeldaSinBordes(referencia));
            String batchnum = resultSet.getString("batchnum");
            table.addCell(crearCeldaSinBordes(batchnum));
            int cantidad = resultSet.getInt("cantidad");
            table.addCell(crearCeldaSinBordes(Integer.toString(cantidad)));
            int conteo = resultSet.getInt("conteo");
            table.addCell(crearCeldaSinBordes(Integer.toString(conteo)));
            int diferencia = resultSet.getInt("diferencia");
            table.addCell(crearCeldaSinBordes(Integer.toString(diferencia)));
        }
        // Añadir la tabla al documento
        document.add(table);
        
        // Cerrar el documento
        document.close();
        
        System.out.println("PDF creado exitosamente.");
        
     // Abrir el PDF automáticamente
        if (Desktop.isDesktopSupported()) {
            try {
                File archivoPDF = new File(destino);
                if (archivoPDF.exists()) {
                    Desktop.getDesktop().open(archivoPDF);
                } else {
                    System.out.println("El archivo PDF no existe.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Desktop no es compatible en este sistema.");
        }
        
    } catch (SQLException e) {
        e.printStackTrace();
       }
    catch (FileNotFoundException e) {
        e.printStackTrace();
    } catch (DocumentException e) {
        e.printStackTrace();
    }
	
	}

    //INTERFAZ
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JTextField jTextCaja;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextConteo;

    // Getters and Setters
    public JButton getjButton1() {
        return jButton1;
    }

    public void setjButton1(JButton jButton1) {
        this.jButton1 = jButton1;
    }

    public JLabel getjLabel1() {
        return jLabel1;
    }

    public void setjLabel1(JLabel jLabel1) {
        this.jLabel1 = jLabel1;
    }

    public JLabel getjLabel2() {
        return jLabel2;
    }

    public void setjLabel2(JLabel jLabel2) {
        this.jLabel2 = jLabel2;
    }

    public JLabel getjLabel3() {
        return jLabel3;
    }

    public void setjLabel3(JLabel jLabel3) {
        this.jLabel4 = jLabel4;
    }

    public JLabel getjLabel4() {
        return jLabel4;
    }

    public void setjLabel4(JLabel jLabel4) {
        this.jLabel3 = jLabel3;
    }

    public JTextField getjTextCaja() {
        return jTextCaja;
    }

    public void setjTextCaja(JTextField jTextCaja) {
        this.jTextCaja = jTextCaja;
    }

    public JTextField getjTextField2() {
        return jTextField2;
    }

    public void setjTextField2(JTextField jTextField2) {
        this.jTextField2 = jTextField2;
    }
    public JTextField getjTextConteo() {
        return jTextConteo;
    }

    public void setjTextConteo(JTextField jTextConteo) {
        this.jTextConteo = jTextConteo;
    }
    
}
