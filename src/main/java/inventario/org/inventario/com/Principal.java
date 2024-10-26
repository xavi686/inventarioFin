package inventario.org.inventario.com;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;

import inventario.org.inventario.com.InterfazConteo;

public class Principal {

    public static void main(String[] args) {
        // Crear la ventana principal
        JFrame mainFrame = new JFrame("Aplicación Principal"); //Interfaz principal
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(300, 200);
        mainFrame.setLayout(new FlowLayout());

        // Crear botones en la ventana principal
        JButton btnConteo = new JButton("Conteo");
        JButton btnCook = new JButton("Cook");
        JButton btnHS = new JButton("HS");
        JButton btnBD = new JButton("BD");
        JButton btnReconteo = new JButton("Reconteo");
        JButton btnConsolidado = new JButton("Consolidado");

        // Añadir botones a la ventana principal
        mainFrame.add(btnConteo);
        mainFrame.add(btnCook);
        mainFrame.add(btnHS);
        mainFrame.add(btnBD);
        mainFrame.add(btnReconteo);
        mainFrame.add(btnConsolidado);

        // Acción para el botón de Conteo
        btnConteo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	crearInterfazConteo();
            }
        });

        // Acción para el botón de Reconteo
        btnReconteo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	// Opciones personalizadas para los botones
                Object[] options = {"Sí", "No"};
            	int respuesta = JOptionPane.showOptionDialog(null, 
                        "¿Deseas recontar alguna caja?", 
                        "Confirmar", 
                        JOptionPane.YES_NO_OPTION, 
                        JOptionPane.QUESTION_MESSAGE, 
                        null, 
                        options, 
                        options[0]);
            	if (respuesta == JOptionPane.YES_OPTION) {
                	// Crear cuadro de texto para ingresar el número de la caja
                	JTextField textField = new JTextField();
                    Dimension textFieldSize = new Dimension(50, 24);
                    textField.setPreferredSize(textFieldSize); //número de caja
                    JButton actionButton = new JButton("Realizar Acción");

                    // Panel para contener el cuadro de texto y el botón
                    JPanel panel = new JPanel();
                    JLabel numCaja = new JLabel("Número de la caja:");

                    GridBagConstraints gbc = new GridBagConstraints();
                    gbc.insets = new Insets(5, 5, 5, 5);  // Add some padding

                    gbc.gridx = 0;
                    gbc.gridy = 0;
                    panel.add(numCaja, gbc);

                    gbc.gridx = 1;
                    gbc.gridy = 0;
                    panel.add(textField, gbc);

                    // Crear un cuadro de diálogo con el panel
                    int option = JOptionPane.showConfirmDialog(null, panel, "Número de la Caja", JOptionPane.OK_CANCEL_OPTION);
                    if (option == JOptionPane.OK_OPTION) {
                    	String numeroCaja = textField.getText();
                        // Aquí puedes agregar la validación del número ingresado si es necesario
                        if (!numeroCaja.isEmpty()){
                            System.out.println("Número de la caja ingresado: " + numCaja);
                        	InterfazConteo.reconteo(Integer.parseInt(numeroCaja)); //modifica las tablas para reconteo
                        	crearInterfazConteo(); //carga la interfaz para hacer el reconteo
                        } else {
                            System.out.println("Número de la caja no ingresado: ");
                        }

                        // Añadir acción al botón
                        actionButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                // Acción a realizar con el número de la caja
                                System.out.println("Número de la caja ingresado: " + numeroCaja);
                                System.out.println("Realizando la acción programada...");
                                // Aquí puedes agregar el código para la acción que deseas realizar
                                if (!numeroCaja.isEmpty()){
                                    System.out.println("Número de la caja ingresado: " + numeroCaja);
                                } else {
                                    System.out.println("Número de la caja no ingresado. ");
                                }
                            }
                        });
                    } else {
                        System.out.println("Operación cancelada.");
                    }                } else if (respuesta == JOptionPane.NO_OPTION) {
                    // Acción a realizar cuando se presiona "No"
                    System.out.println("Se ha seleccionado 'No'. Cerrando el cuadro de mensaje...");
                    // El cuadro de mensaje se cerrará automáticamente
                }            }
        });

        //Acción para el conteo consolidado
        btnConsolidado.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//            	consolidado(connection);
            	InterfazConteo.consolidado();
            }
        });

        // Acción para el botón de conteo de Cook
        btnCook.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame formularioFrame = new JFrame("Formulario");
                formularioFrame.setSize(300, 200);
                formularioFrame.setLayout(new FlowLayout());

                JComboBox<String> comboBox = new JComboBox<>(new String[]{"Opción 1", "Opción 2", "Opción 3"});
                JRadioButton radioButton = new JRadioButton("Opción");
                JTextField textField = new JTextField(10);
                JButton btnFinalizar = new JButton("Finalizar");

                formularioFrame.add(comboBox);
                formularioFrame.add(radioButton);
                formularioFrame.add(textField);
                formularioFrame.add(btnFinalizar);

                btnFinalizar.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        formularioFrame.dispose();
                    }
                });

                formularioFrame.setVisible(true);
            }
        });

        
        
        mainFrame.setVisible(true);
    }

	protected static void crearInterfazConteo() {
		// TODO Auto-generated method stub
    	InterfazConteo interfazConteo = new InterfazConteo();
    	interfazConteo.setTitle("Conteo");
    	interfazConteo.setVisible(true);
	}
}