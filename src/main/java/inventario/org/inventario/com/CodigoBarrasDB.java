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


public class CodigoBarrasDB {

	private static final String URL = "jdbc:mysql://localhost:3314/inventario";
    private static final String USER = "root";  // Cambia esto a tu usuario de MySQL
    private static final String PASSWORD = "root";  // Cambia esto a tu contraseña de MySQL
    
    public static void main(String[] args) {
	    	
	        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
	            System.out.println("Conexión a MySQL establecida.");

		        Scanner scanner = new Scanner(System.in);

		        System.out.print("Ingrese el código de barras: ");
		        String input = scanner.nextLine();

		        if (input.length() < 22) {
		            System.out.println("El código de barras es demasiado corto.");
		            return;
		        }

		        // Extraer los 16 primeros caracteres
		        String codigo = input.substring(0, 16);

		        // Verificar los caracteres en las posiciones 16 y 17 (índice 15 y 16)
		        if (!input.substring(16, 18).equals("00")) {
		            System.out.println("Error: Los caracteres en las posiciones 17 y 18 no son '00'.");
		            return;
		        }

		        // Extraer la fecha (6 caracteres después de "00")
		        String fecha = input.substring(18, 24);

		        // Verificar los caracteres en las posiciones 24 y 25 (índice 23 y 24)
		        if (!input.substring(24, 26).equals("10")) {
		            System.out.println("Error: Los caracteres en las posiciones 25 y 26 no son '10'.");
		            return;
		        }

		        // Extraer el lote (resto de los caracteres después de "10")
		        String lote = input.substring(26);

		        // Mostrar los resultados preliminares
		        System.out.println("Código: " + codigo);
		        System.out.println("Fecha: " + fecha);
		        System.out.println("Lote: " + lote);
		        
		        String itemcode = "";
		        String referencia = "";
		        int total = 0;
		        int cantidad = 0;
		        String grupo = "";
		        int caja = 0;
	    		int num_conteo = 0;		        

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
			                Cell celda = sheet.getCell(2, i);
			                itemcode = celda.getContents().trim();
			                System.out.println(itemcode);
			                celda = sheet.getCell(3, i);
			                referencia = celda.getContents().trim();
			                System.out.println(referencia);
			                celda = sheet.getCell(5, i);
			                cantidad = Integer.parseInt(celda.getContents().trim()); //las contadas en la caja
			                System.out.println(cantidad);
			   			    System.out.println();
		                    encontrado = true;
		                    //Ingresar las unidades encontradas, la caja, el grupo
		                    //en el reconteo sólo es restar la info de las unidades encontradas en las cajas
		                    
		                }
		            }

		            if (encontrado==true) {
		                boolean continuar = true;
		                while (continuar) {
		                    System.out.println("\nOpciones:");
		                    System.out.println("1. Ingreso");
		                    System.out.println("2. Contar");
		                    System.out.println("3. Recontar");
		                    System.out.println("4. Mostrar existencias");
		                    System.out.println("5. Mostrar facturas");
		                    System.out.println("6. Salir");
		                    System.out.print("Ingrese su opción: ");
		                    int opcion = scanner.nextInt();
		                    scanner.nextLine();

		                    switch (opcion) {
		                    case 1:
		                    	//Ingreso
		                    	ingreso();
		                        // Contar
			                    System.out.print("Ingrese el número de artículos contados: ");
			                    total = scanner.nextInt();
			                    System.out.print("Ingrese el grupo: ");
			                    grupo = scanner.next();
			                    System.out.print("Ingrese el número de la caja: ");
			                    caja = scanner.nextInt();
			                    System.out.print("Ingrese el número del conteo: ");
			                    num_conteo = scanner.nextInt();

//Con este código guardo todo los caracteres del código de barras			                    
//		                    	conteo(connection, input, lote, itemcode, referencia, fecha, total, cantidad, grupo, caja, num_conteo);
//Con este sólo guardo los primeros 16 caracteres del código de barras
			                    conteo(connection, codigo, lote, itemcode, referencia, fecha, total, cantidad, grupo, caja, num_conteo);
		                        System.out.print("Ingrese el ID del producto que desea modificar: ");
		                        String idModificar = scanner.nextLine();
		                        break;
		                        case 2:
		                            // Eliminar producto
		                            System.out.print("Ingrese el ID del producto que desea eliminar: ");
		                            String idEliminar = scanner.nextLine();
		                            break;
		                        case 3:
		                            // Realizar venta
		                            System.out.println("Ingrese los productos que se van a vender (Ingrese '0' para finalizar): ");
		                            break;
		                        case 4:
		                            // Mostrar existencias
		                            System.out.println("ID del producto - Nombre del producto - Existencias");
		                            break;
		                        case 5:
		                            // Mostrar facturas
		                            System.out.println("\nFacturas:");
		                            System.out.println("Número de factura - ID del producto - Valor total de venta");
		                            break;
		                        case 6:
		                            continuar = false;
		                            break;
		                        default:
		                            System.out.println("Opción inválida. Por favor, ingrese un número entre 1 y 6.");
		                            break;
		                    }
		                }
		                scanner.close();
		            }

		            if (!encontrado) {
		                System.out.println("No se encontraron coincidencias para el código " + codigo + " y lote " + lote + " en el archivo de Excel.");
		            }

		            workbook.close();

		        } catch (IOException | BiffException e) {
		            e.printStackTrace();
		        }

		        scanner.close();
	            
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }
    
    private static void ingreso()
    		{
    	
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
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    	
    }
	    	
	}
