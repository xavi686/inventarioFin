package inventario.org.inventario.com;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import java.sql.*;
import java.text.DateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import java.awt.Desktop;


public class CodigoBarrasBardUreteralStent {

	private static final String URL = "jdbc:mysql://localhost:3314/inventario";
    private static final String USER = "root";  // Cambia esto a tu usuario de MySQL
    private static final String PASSWORD = "root";  // Cambia esto a tu contraseña de MySQL
    
    public static void main(String[] args) {
	    	
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
	            System.out.println("Conexión a MySQL establecida.");

	        Scanner scanner = new Scanner(System.in);

	        boolean continuar_principal = true;
	        while (continuar_principal) {
		        System.out.println("\nOpciones:");
                System.out.println("1. Ingreso");
                System.out.println("2. Reconteo");
                System.out.println("3. Consolidado");
                System.out.println("4. Salir");
                System.out.print("Ingrese su opción: ");
                int opcion_menu = scanner.nextInt();
                scanner.nextLine();
                String codigobarra;

                switch (opcion_menu) {
                case 1:
                    // Ingreso datos de la caja y grupo
                    System.out.print("Ingrese el grupo: "); //A, B o C
                    String grupo = scanner.nextLine().toUpperCase();
                    System.out.print("Ingrese el número de la caja: "); 
                    int caja = scanner.nextInt();
                    int num_conteo = 1;
                    scanner.nextLine();
//                    datosgrupo(grupo, caja, num_conteo);
//OJO
//CODIGO PARA URETERAL STENT KIT DE BARD          
                    codigobarra = leercodigo(scanner);
//                    System.out.print("Leer código parte 1: "); 
//                    String codigobarra1 = scanner.nextLine();
//                    System.out.print("Leer código parte 2: "); 
//                    String codigobarra2 = scanner.nextLine();
                    
//                    String codigobarra = codigobarra1+codigobarra2;
//                    System.out.print(codigobarra); 

                    proceso_contar(connection, codigobarra, grupo, caja, num_conteo);
//Sub menu para continuar con el conteo
                    boolean continuar = true;
        	        while (continuar) {
        	        	System.out.println("Seleccione la opción a realizar:");
        	        	System.out.println("1. Seguir contando");
        	        	System.out.println("2. Imprimir reporte");
        	        	System.out.println("3. Finalizar conteo");
        	        	System.out.print("Ingrese su opción: ");
        	        	int tarea = scanner.nextInt();
        	        	scanner.nextLine();
        	        	switch (tarea) {
                        	case 1:
//                        		System.out.print("Leer código: "); 
//                        		codigobarra = scanner.nextLine();
                        		codigobarra = leercodigo(scanner);                        		
                        		proceso_contar(connection, codigobarra, grupo, caja, num_conteo);
                        		break;
                        	case 2:
                                reporte(connection, caja);
                        		break;
                        	case 3:
                        		continuar = false;
                        		break;
                        	default:
                        		System.out.println("Opción inválida.");
                        		break;
        	        	}
        	        }	
        	        break;
                    case 2:
                        // Imprimir reporte
                        break;
                    case 3:
                        //Consolidado
                    	consolidado(connection);
                        break;
                    case 4:
                        continuar_principal = false;
                        break;
                    default:
                        System.out.println("Opción inválida. Por favor, ingrese un número entre 1 y 3.");
                        break;
                }
	            
	    	}//while continuar_principal
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }
    
    private static void datosgrupo(String grupo, int caja, int num_conteo){
    }
    
    private static void proceso_contar(Connection connection, String codigobarra, String grupo, int caja, 
    		int num_conteo){

    	if (codigobarra.length() < 22) {
            System.out.println("El código de barras es demasiado corto.");
            return;
        }

        // Extraer los 16 primeros caracteres
        String codigo = codigobarra.substring(0, 16);

        // Verificar los caracteres en las posiciones 17 y 18 (índice 16 y 17)
        if (!codigobarra.substring(16, 18).equals("17")) {
            System.out.println("Error: Los caracteres en las posiciones 18 y 19 no son '17'.");
            return;
        }

        // Extraer la fecha (6 caracteres después de "17")
        String fecha = codigobarra.substring(18, 24);

        // Verificar los caracteres en las posiciones 24 y 25 (índice 23 y 24)
        if (!codigobarra.substring(24, 26).equals("10")) {
            System.out.println("Error: Los caracteres en las posiciones 24 y 25 no son '10'.");
            return;
        }

        // Extraer el lote (resto de los caracteres después de "10")
        String lote = codigobarra.substring(26);

        // Mostrar los resultados preliminares
        System.out.println("Código: " + codigo);
        System.out.println("Fecha: " + fecha);
        System.out.println("Lote: " + lote);
        
        String itemcode = "";
        String referencia = "";
        int cantidad = 0;

        Scanner scanner = new Scanner(System.in);
        System.out.print("Ingrese la cantidad contada: ");
        int total = scanner.nextInt();
                
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
	                System.out.println(itemcode + "\n");
	                celda = sheet.getCell(3, i);
	                referencia = celda.getContents().trim();
	                System.out.println(referencia + "\n");
	                celda = sheet.getCell(5, i);
	                cantidad = Integer.parseInt(celda.getContents().trim()); //las contadas en la caja
	                System.out.println(cantidad + "\n");
	   			    System.out.println();
                    encontrado = true;
                    //Ingresar las unidades encontradas, la caja, el grupo
                    //en el reconteo sólo es restar la info de las unidades encontradas en las cajas
                }
            }

            if (encontrado==true) {
//Con este código guardo todo los caracteres del código de barras			                    
//                    	conteo(connection, codigobarra, lote, itemcode, referencia, fecha, total, cantidad, grupo, caja, num_conteo);
//Con este sólo guardo los primeros 16 caracteres del código de barras
            	conteo(connection, codigo, lote, itemcode, referencia, fecha, total, cantidad, grupo, caja, num_conteo);
            }

            if (!encontrado) {
                System.out.println("No se encontraron coincidencias para el código " + codigo + " y lote " + lote + " en el archivo de Excel.");
            }

            workbook.close();

        } catch (IOException | BiffException e) {
            e.printStackTrace();
        }
    }

    private static void conteo(Connection connection, String codebars, String batchnum, String itemcode,
    		String referencia, String vencimiento, int total, int cantidad, String grupo, int caja,
    		int num_conteo) {
        String query = "INSERT INTO conteo (codebars, batchnum, itemcode, referencia, "
        		+ "vencimiento, total, cantidad, grupo, caja, "
        		+ "fecha, num_conteo, hora) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
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
            } else {
                System.out.println("No se pudo agregar! Por favor verifique!.");
            }   
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static String leercodigo(Scanner scanner) {
    	//OJO
    	//CODIGO PARA URETERAL STENT KIT DE BARD                
//    						Scanner scanner = new Scanner(System.in);
    	                    System.out.print("Leer código parte 1: "); 
    	                    String codigobarra1 = scanner.nextLine();
    	                    System.out.print("Leer código parte 2: "); 
    	                    String codigobarra2 = scanner.nextLine();
    	                    String codigobarra = codigobarra1+codigobarra2;
    	                    return codigobarra;
    }

   		private static void reconteo(Connection connection, String codebars, String batchnum, String itemcode,
    		String referencia, String vencimiento, int total, int cantidad, String grupo, int caja,
    		int num_conteo) {
        String query = "INSERT INTO conteo (codebars, batchnum, itemcode, referencia, "
        		+ "vencimiento, total, cantidad, grupo, caja, "
        		+ "fecha, num_conteo, hora) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
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
            } else {
                System.out.println("No se pudo agregar! Por favor verifique!.");
            }   
        } catch (SQLException e) {
            e.printStackTrace();
        }
    	
    }

    private static void reporte(Connection connection, int caja) {

    	// Ruta del archivo PDF a crear
        String destino = "C:\\Users\\javie\\OneDrive\\Documents\\VJ Cardiosistemas\\Inventario\\mi_pdf.pdf";

        // Crear el documento
        Document document = new Document();

//        String query = "SELECT * FROM conteo WHERE caja = " + caja;

        String query = "SELECT batchnum, itemcode, referencia, SUM(total) AS TotalCantidad, num_conteo" +
                " FROM conteo WHERE caja = " + caja + " GROUP BY batchnum, itemcode, referencia, num_conteo";
        
        try (Statement statement = connection.createStatement();
        	ResultSet resultSet = statement.executeQuery(query)) {
        	
            // Crear el escritor de PDF
            PdfWriter.getInstance(document, new FileOutputStream(destino));

            // Abrir el documento
            document.open();

         // Añadir un título
            document.add(new Paragraph("Informe caja número " + caja));

        // Añadir espacio
            document.add(new Paragraph("\n"));
            
        // Crear una tabla con 4 columnas
            PdfPTable table = new PdfPTable(4);
            
            boolean contador = true;
            while (resultSet.next()) {
            	while(contador){
                    int num_conteo = resultSet.getInt("num_conteo");
                    document.add(new Paragraph("Conteo: " + num_conteo));
                    // Añadir espacio 	
                    document.add(new Paragraph("\n"));
                    // Añadir la cabecera de la tabla
                    table.addCell(crearCeldaSinBordes("Lote"));
                    table.addCell(crearCeldaSinBordes("Código"));
                    table.addCell(crearCeldaSinBordes("Referencia"));
                    table.addCell(crearCeldaSinBordes("Cantidad"));
            		contador = false;
            	}
                String batchnum = resultSet.getString("batchnum");
                table.addCell(crearCeldaSinBordes(batchnum));
                String itemcode = resultSet.getString("itemcode");
                table.addCell(crearCeldaSinBordes(itemcode));
                String referencia = resultSet.getString("referencia");
                table.addCell(crearCeldaSinBordes(referencia));
//                int contados = resultSet.getInt("total");
                int contados = resultSet.getInt("TotalCantidad");
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

    private static void consolidado(Connection connection) {

    	String batchnum;
        String itemcode;
        String referencia;
        int cantidad; //las contadas en la caja
        // Cargar la tabla con la información descargada en el archivo de Excel

        try {
        	// Verificar si la tabla está vacía
                String countQuery = "SELECT COUNT(*) FROM descarga";
                PreparedStatement countStatement = connection.prepareStatement(countQuery);
                ResultSet resultSet = countStatement.executeQuery();
                resultSet.next();
                int rowCount = resultSet.getInt(1);

                Workbook workbook = Workbook.getWorkbook(new File("C:\\Users\\javie\\OneDrive\\Documents\\VJ Cardiosistemas\\Inventario\\inv.xls"));

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
                            "    SELECT referencia, batchnum, SUM(total) AS total_conteo " +
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
             //Se configuró la base de datos en para permitir eliminar datos para el reconteo
             //Falta hacer el proceso para el reconteo, que incremente automàticamente el reconteo
             //Eliminar los datos a partir del número de la caja en la tabla conteo
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
    
}


