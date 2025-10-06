package gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class VentanaInicioSesion extends JFrame {

    private static final long serialVersionUID = 1L;

    public VentanaInicioSesion() {
        setTitle("Inicio de Sesión");
        setSize(600, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Panel superior con título centrado y logo a la derecha
        JPanel pSuperior = new JPanel();
        pSuperior.setLayout(new BoxLayout(pSuperior, BoxLayout.X_AXIS));
        pSuperior.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        pSuperior.add(Box.createHorizontalGlue());

        JLabel lblTitulo = new JLabel("INICIO SESIÓN");
        lblTitulo.setFont(new Font("Tahoma", Font.BOLD, 26));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        pSuperior.add(lblTitulo);

        pSuperior.add(Box.createHorizontalGlue());

        // Logo a la derecha
        ImageIcon iconLogo = new ImageIcon("resources/images/logo.png"); // ruta al logo
        Image imgOriginal = iconLogo.getImage();
        int nuevaAltura = 60;
        int nuevaAnchura = (int) (imgOriginal.getWidth(null) * ((double) nuevaAltura / imgOriginal.getHeight(null)));
        Image imgEscalada = imgOriginal.getScaledInstance(nuevaAnchura, nuevaAltura, Image.SCALE_SMOOTH);
        JLabel lblLogo = new JLabel(new ImageIcon(imgEscalada));
        pSuperior.add(lblLogo);

        add(pSuperior, BorderLayout.NORTH);

        // Panel central con campos de correo y contraseña
        JPanel pCentral = new JPanel(new GridBagLayout());
        pCentral.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Correo
        JLabel lblCorreo = new JLabel("Correo electrónico:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        pCentral.add(lblCorreo, gbc);

        JTextField txtCorreo = new JTextField();
        txtCorreo.setPreferredSize(new Dimension(300, 25)); // Campo más largo
        gbc.gridx = 1;
        gbc.gridy = 0;
        pCentral.add(txtCorreo, gbc);

        // Contraseña
        JLabel lblContrasena = new JLabel("Contraseña:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        pCentral.add(lblContrasena, gbc);

        JPasswordField txtContrasena = new JPasswordField();
        txtContrasena.setPreferredSize(new Dimension(300, 25)); // Campo más largo
        gbc.gridx = 1;
        gbc.gridy = 1;
        pCentral.add(txtContrasena, gbc);

        add(pCentral, BorderLayout.CENTER);

        // Panel inferior con botones 
        JPanel pInferior = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        JButton btnRegistrarse = new JButton("Registrarse");
        JButton btnIniciarSesion = new JButton("Iniciar Sesión");

        // Eventos de ejemplo
        btnRegistrarse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(VentanaInicioSesion.this, "Botón Registrarse pulsado");
            }
        });

        btnIniciarSesion.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        new gui.VentanaCatalogo(); 
                    }
                });

                VentanaInicioSesion.this.dispose();
            }
        });


        pInferior.add(btnRegistrarse);
        pInferior.add(btnIniciarSesion);

        add(pInferior, BorderLayout.SOUTH);

        setResizable(false);
        setVisible(true);
    }

}
