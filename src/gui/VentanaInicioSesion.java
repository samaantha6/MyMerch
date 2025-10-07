package gui;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import db.BaseDatosConfig;

public class VentanaInicioSesion extends JFrame {

    private static final long serialVersionUID = 1L;

    public VentanaInicioSesion() {
        setTitle("Inicio de Sesión");
        setSize(600, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

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
        ImageIcon iconLogo = new ImageIcon("resources/images/logo.png"); 
        Image imgOriginal = iconLogo.getImage();
        int nuevaAltura = 60;
        int nuevaAnchura = (int) (imgOriginal.getWidth(null) * ((double) nuevaAltura / imgOriginal.getHeight(null)));
        Image imgEscalada = imgOriginal.getScaledInstance(nuevaAnchura, nuevaAltura, Image.SCALE_SMOOTH);
        JLabel lblLogo = new JLabel(new ImageIcon(imgEscalada));
        pSuperior.add(lblLogo);

        add(pSuperior, BorderLayout.NORTH);

        JPanel pCentral = new JPanel(new GridBagLayout());
        pCentral.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblCorreo = new JLabel("Correo electrónico:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        pCentral.add(lblCorreo, gbc);

        JPanel pCorreo = new JPanel(new BorderLayout(5,0));
        JTextField txtUsuario = new JTextField();
        txtUsuario.setPreferredSize(new Dimension(200, 25));
        String[] dominios = {"@gmail.com", "@merch.com"};
        JComboBox<String> comboDominio = new JComboBox<>(dominios);
        comboDominio.setPreferredSize(new Dimension(100, 25));

        pCorreo.add(txtUsuario, BorderLayout.CENTER);
        pCorreo.add(comboDominio, BorderLayout.EAST);

        gbc.gridx = 1;
        gbc.gridy = 0;
        pCentral.add(pCorreo, gbc);

     
        JLabel lblContrasena = new JLabel("Contraseña:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        pCentral.add(lblContrasena, gbc);

        // Panel para la contraseña + botón
        JPanel pContrasena = new JPanel(new BorderLayout());
        JPasswordField txtContrasena = new JPasswordField();
        txtContrasena.setPreferredSize(new Dimension(260, 30));

        // Botón ojo con icono
        ImageIcon iconOjo = new ImageIcon("resources/images/ojo.png");
        Image imgOjo = iconOjo.getImage().getScaledInstance(35, 35, Image.SCALE_SMOOTH);
        JButton btnOjo = new JButton(new ImageIcon(imgOjo));
        btnOjo.setPreferredSize(new Dimension(40, 30));
        btnOjo.setFocusPainted(false);
        btnOjo.setBorder(BorderFactory.createEmptyBorder());
        btnOjo.setContentAreaFilled(false);
        btnOjo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Mostrar/ocultar contraseña
        btnOjo.addActionListener(e -> {
            if (txtContrasena.getEchoChar() != '\u0000') { // mostrar
                txtContrasena.setEchoChar((char)0);
            } else { // ocultar
                txtContrasena.setEchoChar('•');
            }
        });

        pContrasena.add(txtContrasena, BorderLayout.CENTER);
        pContrasena.add(btnOjo, BorderLayout.EAST);

        gbc.gridx = 1;
        gbc.gridy = 1;
        pCentral.add(pContrasena, gbc);

        add(pCentral, BorderLayout.CENTER);

        
        JPanel pInferior = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        JButton btnRegistrarse = crearBotonEstilizado("Registrarse");
        JButton btnIniciarSesion = crearBotonEstilizado("Iniciar Sesión");

        btnRegistrarse.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> new VentanaRegistro());
            dispose();
        });

        // Botón Iniciar Sesión valida usuario en la BD
        btnIniciarSesion.addActionListener(e -> {
            String correo = txtUsuario.getText().trim() + comboDominio.getSelectedItem().toString();
            String contrasena = new String(txtContrasena.getPassword());

            if (correo.isEmpty() || contrasena.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Introduce correo y contraseña", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (validarUsuario(correo, contrasena)) {
                SwingUtilities.invokeLater(() -> new VentanaCatalogo());
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Correo o contraseña incorrectos", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        pInferior.add(btnRegistrarse);
        pInferior.add(btnIniciarSesion);
        add(pInferior, BorderLayout.SOUTH);

        setResizable(false);
        setVisible(true);
    }

    private JButton crearBotonEstilizado(String texto) {
        JButton boton = new JButton(texto);
        boton.setFocusPainted(false);
        boton.setFont(new Font("Tahoma", Font.BOLD, 14));

        Color colorPrincipal = new Color(0, 120, 215);
        Color colorHover = new Color(72, 209, 204);

        boton.setBackground(colorPrincipal);
        boton.setForeground(Color.WHITE);
        boton.setBorder(new EmptyBorder(10, 25, 10, 25));
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        boton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(colorPrincipal, 1, true),
                new EmptyBorder(8, 20, 8, 20)
        ));

        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                boton.setBackground(colorHover);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                boton.setBackground(colorPrincipal);
            }
        });

        return boton;
    }

    // Validar usuario en la base de datos
    private boolean validarUsuario(String correo, String contrasena) {
        boolean valido = false;
        Connection con = BaseDatosConfig.initBD("resources/db/MyMerch.db");
        if (con != null) {
            try {
                String sql = "SELECT * FROM Usuarios WHERE correo=? AND contrasena=?";
                PreparedStatement pst = con.prepareStatement(sql);
                pst.setString(1, correo);
                pst.setString(2, contrasena);
                ResultSet rs = pst.executeQuery();
                if (rs.next()) {
                    valido = true;
                }
                rs.close();
                pst.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            } finally {
                BaseDatosConfig.closeBD(con);
            }
        }
        return valido;
    }
}
