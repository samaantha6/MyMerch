package gui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import db.BaseDatosConfig;
import domain.Usuario;

public class VentanaMiCuenta extends JFrame {

    private static final long serialVersionUID = 1L;
    private Usuario usuario;

    public VentanaMiCuenta(Usuario usuario) {
        this.usuario = usuario;

        setTitle("Mi Cuenta");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel pSuperior = new JPanel(new BorderLayout());
        pSuperior.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton btnAtras = new JButton("←");
        btnAtras.setFont(new Font("Arial", Font.BOLD, 18));
        btnAtras.setFocusPainted(false);
        btnAtras.setBorderPainted(false);
        btnAtras.setContentAreaFilled(false);
        pSuperior.add(btnAtras, BorderLayout.WEST);

        JLabel lblTitulo = new JLabel("Mi Cuenta", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Tahoma", Font.BOLD, 28));
        pSuperior.add(lblTitulo, BorderLayout.CENTER);

        ImageIcon iconLogo = new ImageIcon("resources/images/logo.png");
        Image img = iconLogo.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
        JLabel lblLogo = new JLabel(new ImageIcon(img));
        pSuperior.add(lblLogo, BorderLayout.EAST);

        add(pSuperior, BorderLayout.NORTH);

        JPanel pCentral = new JPanel(new GridLayout(1, 2, 50, 0));
        pCentral.setBorder(new EmptyBorder(30, 50, 30, 50));

        JPanel pIzq = new JPanel();
        pIzq.setLayout(new BoxLayout(pIzq, BoxLayout.Y_AXIS));
        pIzq.setOpaque(false);

        JLabel lblNombre = new JLabel("Nombre: " + usuario.getNombre());
        JLabel lblCorreo = new JLabel("Correo: " + usuario.getCorreo());

        lblNombre.setFont(new Font("Tahoma", Font.PLAIN, 16));
        lblCorreo.setFont(new Font("Tahoma", Font.PLAIN, 16));

        pIzq.add(lblNombre);
        pIzq.add(Box.createRigidArea(new Dimension(0, 20)));
        pIzq.add(lblCorreo);

        JPanel pDer = new JPanel();
        pDer.setLayout(new BoxLayout(pDer, BoxLayout.Y_AXIS));
        pDer.setOpaque(false);

        JLabel lblApellidos = new JLabel("Apellidos: " + usuario.getApellidos());
        JLabel lblTelefono = new JLabel("Teléfono: " + usuario.getTelefono());

        lblApellidos.setFont(new Font("Tahoma", Font.PLAIN, 16));
        lblTelefono.setFont(new Font("Tahoma", Font.PLAIN, 16));

        pDer.add(lblApellidos);
        pDer.add(Box.createRigidArea(new Dimension(0, 20)));
        pDer.add(lblTelefono);

        pCentral.add(pIzq);
        pCentral.add(pDer);

        add(pCentral, BorderLayout.CENTER);

        JPanel pInferior = new JPanel();
        pInferior.setLayout(new BoxLayout(pInferior, BoxLayout.Y_AXIS));
        pInferior.setBorder(new EmptyBorder(10, 10, 30, 10));

        JLabel linkCambiarContrasena = new JLabel("<HTML><U>Cambiar contraseña</U></HTML>");
        linkCambiarContrasena.setForeground(Color.BLUE.darker());
        linkCambiarContrasena.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        linkCambiarContrasena.setAlignmentX(Component.CENTER_ALIGNMENT);

        linkCambiarContrasena.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String nuevaContrasena = JOptionPane.showInputDialog(VentanaMiCuenta.this,
                        "Introduce tu nueva contraseña:");
                if (nuevaContrasena != null && !nuevaContrasena.trim().isEmpty()) {
                    cambiarContrasena(nuevaContrasena.trim());
                }
            }
        });

        pInferior.add(linkCambiarContrasena);
        add(pInferior, BorderLayout.SOUTH);

        // Eventos
        btnAtras.addActionListener(e -> {
            new VentanaCatalogo(usuario);
            dispose();
        });

        setVisible(true);
        setResizable(false);
    }

    // Método para cambiar la contraseña
    private void cambiarContrasena(String nuevaContrasena) {
        Connection con = BaseDatosConfig.initBD("resources/db/MyMerch.db");
        if (con != null) {
            try {
                String sql = "UPDATE Usuarios SET contrasena=? WHERE correo=?";
                PreparedStatement pst = con.prepareStatement(sql);
                pst.setString(1, nuevaContrasena);
                pst.setString(2, usuario.getCorreo());
                int actualizado = pst.executeUpdate();
                if (actualizado > 0) {
                    usuario.setContrasena(nuevaContrasena);
                    JOptionPane.showMessageDialog(this, "Contraseña cambiada correctamente",
                            "Éxito", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Error al cambiar la contraseña",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
                pst.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                BaseDatosConfig.closeBD(con);
            }
        }
    }
}
