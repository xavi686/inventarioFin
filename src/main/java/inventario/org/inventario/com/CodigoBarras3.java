package inventario.org.inventario.com;


import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class CodigoBarras3 {

	    public static void main(String[] args) {
	        Scanner scanner = new Scanner(System.in);

	        System.out.print("Ingrese el código de barras: ");
	        String input = scanner.nextLine();

	        if (input.length() < 52) {
	            System.out.println("El código de barras es demasiado corto.");
	            return;
	        }

	        // Extraer los 34 primeros caracteres
	        String codigo = input.substring(0, 34);

	        // Verificar los caracteres en las posiciones 34 y 35 (índice 33 y 34)
	        if (!input.substring(34, 36).equals("00")) {
	            System.out.println("Error: Los caracteres en las posiciones 35 y 36 no son '00'.");
	            return;
	        }

	        // Extraer la fecha (6 caracteres después de "00")
	        String fecha = input.substring(36, 42);

	        // Verificar los caracteres en las posiciones 42 y 43 (índice 43 y 44)
	        if (!input.substring(42, 44).equals("10")) {
	            System.out.println("Error: Los caracteres en las posiciones 43 y 44 no son '10'.");
	            return;
	        }

	        // Extraer el lote (resto de los caracteres después de "10")
	        String lote = input.substring(44);

	        // Mostrar los resultados preliminares
	        System.out.println("Código: " + codigo);
	        System.out.println("Fecha: " + fecha);
	        System.out.println("Lote interno: " + lote);

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
	                    System.out.println();
	                    encontrado = true;
	                }
	            }

	            if (!encontrado) {
	                System.out.println("No se encontraron coincidencias para el código " + codigo + " y lote " + lote + " en el archivo de Excel.");
	            }

	            workbook.close();

	        } catch (IOException | BiffException e) {
	            e.printStackTrace();
	        }

	        scanner.close();
	    }
	}
