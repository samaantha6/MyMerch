package gui;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import db.BaseDatosConfig;
import domain.Usuario;

public class VentanaRegistro extends JFrame {

    private static final long serialVersionUID = 1L;

    public VentanaRegistro() {
        setTitle("Registro");
        setSize(700, 600);
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

        JLabel lblTitulo = new JLabel("Registro", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Tahoma", Font.BOLD, 28));
        pSuperior.add(lblTitulo, BorderLayout.CENTER);

        ImageIcon iconLogo = new ImageIcon("resources/images/logo.png");
        Image img = iconLogo.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
        JLabel lblLogo = new JLabel(new ImageIcon(img));
        pSuperior.add(lblLogo, BorderLayout.EAST);

        add(pSuperior, BorderLayout.NORTH);

        JPanel pCentral = new JPanel(new GridLayout(1, 2, 30, 0));
        pCentral.setBorder(BorderFactory.createEmptyBorder(30, 80, 30, 80));

        JTextField txtNombre = crearCampo("Nombre");
        JTextField txtCorreo = crearCampo("Correo (sin dominio)");
        JTextField txtApellidos = crearCampo("Apellidos");
        JTextField txtTelefono = crearCampo("Teléfono");

        JPasswordField txtContrasena = new JPasswordField();
        JPasswordField txtConfirmar = new JPasswordField();

        JPanel pIzq = new JPanel();
        pIzq.setLayout(new BoxLayout(pIzq, BoxLayout.Y_AXIS));
        pIzq.setOpaque(false);

        pIzq.add(txtNombre.getParent());
        pIzq.add(Box.createRigidArea(new Dimension(0, 25)));
        pIzq.add(txtCorreo.getParent());
        pIzq.add(Box.createRigidArea(new Dimension(0, 25)));
        pIzq.add(crearCampoContrasena("Contraseña", txtContrasena));

        JPanel pDer = new JPanel();
        pDer.setLayout(new BoxLayout(pDer, BoxLayout.Y_AXIS));
        pDer.setOpaque(false);

        pDer.add(txtApellidos.getParent());
        pDer.add(Box.createRigidArea(new Dimension(0, 25)));
        pDer.add(txtTelefono.getParent());
        pDer.add(Box.createRigidArea(new Dimension(0, 25)));
        pDer.add(crearCampoContrasena("Confirmar contraseña", txtConfirmar));

        pCentral.add(pIzq);
        pCentral.add(pDer);
        add(pCentral, BorderLayout.CENTER);

        JPanel pInferior = new JPanel();
        pInferior.setLayout(new BoxLayout(pInferior, BoxLayout.Y_AXIS));
        pInferior.setBorder(BorderFactory.createEmptyBorder(10, 10, 55, 10));

        JCheckBox chkTerminos = new JCheckBox("Acepto los términos y condiciones");
        chkTerminos.setAlignmentX(Component.CENTER_ALIGNMENT);

        chkTerminos.addActionListener(e -> {
            if (chkTerminos.isSelected()) {
                mostrarTyC(chkTerminos);
            }
        });

        JButton btnRegistrar = crearBotonEstilizado("Registrarse");
        btnRegistrar.setAlignmentX(Component.CENTER_ALIGNMENT);

        pInferior.add(Box.createRigidArea(new Dimension(0, 10)));
        pInferior.add(chkTerminos);
        pInferior.add(Box.createRigidArea(new Dimension(0, 20)));
        pInferior.add(btnRegistrar);

        add(pInferior, BorderLayout.SOUTH);

        // Eventos
        btnAtras.addActionListener(e -> {
            new VentanaInicioSesion();
            dispose();
        });

        btnRegistrar.addActionListener(e -> {
            String nombre = txtNombre.getText().trim();
            String apellidos = txtApellidos.getText().trim();
            String correoBase = txtCorreo.getText().trim();
            String correo = correoBase + "@gmail.com";
            String telefono = txtTelefono.getText().trim();

            String contrasena = new String(txtContrasena.getPassword());
            String confirmar = new String(txtConfirmar.getPassword());

            // Validaciones
            if (nombre.isEmpty() || apellidos.isEmpty() || correoBase.isEmpty() || telefono.isEmpty() ||
                contrasena.isEmpty() || confirmar.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!contrasena.equals(confirmar)) {
                JOptionPane.showMessageDialog(this, "Las contraseñas no coinciden", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!telefono.matches("\\d{9}")) {
                JOptionPane.showMessageDialog(this, "El teléfono debe tener 9 dígitos", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!chkTerminos.isSelected()) {
                JOptionPane.showMessageDialog(this, "Debes aceptar los términos y condiciones", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (usuarioExiste(correo, telefono)) {
                JOptionPane.showMessageDialog(this, "El correo o teléfono ya están registrados", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Usuario u = new Usuario(nombre, apellidos, correo, telefono, contrasena);

            if (BaseDatosConfig.insertarUsuario(u)) {
                JOptionPane.showMessageDialog(this, "Registro completado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                new VentanaCatalogo(u); // ✅ pasar el usuario al constructor
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Error al guardar el usuario", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        setVisible(true);
        setResizable(false);
    }

    // Metodos
    private JTextField crearCampo(String etiqueta) {
        JPanel pCampo = new JPanel();
        pCampo.setLayout(new BoxLayout(pCampo, BoxLayout.Y_AXIS));
        JLabel lbl = new JLabel(etiqueta);
        JTextField txt = new JTextField();
        txt.setMaximumSize(new Dimension(300, 30));
        txt.setAlignmentX(Component.CENTER_ALIGNMENT);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        pCampo.add(lbl);
        pCampo.add(Box.createRigidArea(new Dimension(0, 5)));
        pCampo.add(txt);
        return txt;
    }

    private Box crearCampoContrasena(String etiqueta, JPasswordField txt) {
        JLabel lbl = new JLabel(etiqueta);
        txt.setMaximumSize(new Dimension(200, 30));
        JButton btnOjo = agregarOjo(txt);

        Box box = Box.createVerticalBox();
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        box.add(lbl);

        Box fila = Box.createHorizontalBox();
        fila.add(txt);
        fila.add(Box.createRigidArea(new Dimension(5, 0)));
        fila.add(btnOjo);
        box.add(fila);

        return box;
    }

    private JButton agregarOjo(JPasswordField txt) {
        ImageIcon iconOjo = new ImageIcon("resources/images/ojo.png");
        Image imgOjo = iconOjo.getImage().getScaledInstance(35, 35, Image.SCALE_SMOOTH);
        JButton btnOjo = new JButton(new ImageIcon(imgOjo));
        btnOjo.setPreferredSize(new Dimension(40, 35));
        btnOjo.setFocusPainted(false);
        btnOjo.setBorder(BorderFactory.createEmptyBorder());
        btnOjo.setContentAreaFilled(false);
        btnOjo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnOjo.addActionListener(e -> {
            if (txt.getEchoChar() != '\u0000') {
                txt.setEchoChar((char) 0);
            } else {
                txt.setEchoChar('•');
            }
        });
        return btnOjo;
    }

    private JButton crearBotonEstilizado(String texto) {
        JButton boton = new JButton(texto);
        boton.setFocusPainted(false);
        boton.setFont(new Font("Tahoma", Font.BOLD, 16));
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
            public void mouseEntered(java.awt.event.MouseEvent evt) { boton.setBackground(colorHover); }
            public void mouseExited(java.awt.event.MouseEvent evt) { boton.setBackground(colorPrincipal); }
        });
        return boton;
    }

    private void mostrarTyC(JCheckBox chk) {
        JDialog dialog = new JDialog(this, "Términos y Condiciones", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JTextArea txtTerminos = new JTextArea("TÉRMINOS Y CONDICIONES DE MyMerch\r\n" + "Fecha de entrada en vigor: 07/10/2025\r\n"
				                + "\r\n"
				                + "1. Aceptación de los Términos\r\n"
				                + "Al registrarte y/o realizar compras en MyMerch, aceptas estos Términos y Condiciones en su totalidad. \r\n"
				                + "Si no estás de acuerdo con alguna parte, no uses nuestra tienda.\r\n"
				                + "\r\n"
				                + "2. Registro de usuario\r\n"
				                + "Para comprar en MyMerch es necesario crear una cuenta con datos veraces y completos. \r\n"
				                + "Cada usuario es responsable de mantener la confidencialidad de su contraseña y correo electrónico.\r\n"
				                + "\r\n"
				                + "3. Productos y disponibilidad\r\n"
				                + "MyMerch se esfuerza por mantener actualizados los productos y sus existencias. \r\n"
				                + "Sin embargo, no garantizamos la disponibilidad continua de todos los artículos. \r\n"
				                + "Los precios y descripciones pueden cambiar sin previo aviso.\r\n"
				                + "\r\n"
				                + "4. Pagos y facturación\r\n"
				                + "Todos los pagos se realizarán mediante los métodos disponibles en la plataforma. \r\n"
				                + "Los datos de pago deben ser correctos y estar autorizados por el titular de la cuenta bancaria o tarjeta.\r\n"
				                + "\r\n"
				                + "5. Envíos y devoluciones\r\n"
				                + "- Los envíos se realizarán a la dirección proporcionada por el usuario durante la compra.\r\n"
				                + "- Las devoluciones se aceptarán dentro de los 14 días posteriores a la entrega del producto, siempre que el artículo esté en condiciones originales.\r\n"
				                + "- Los gastos de envío de devoluciones corren a cargo del cliente, salvo error de MyMerch.\r\n"
				                + "\r\n"
				                + "6. Uso del sitio\r\n"
				                + "Está prohibido usar MyMerch para fines ilegales o inapropiados. \r\n"
				                + "Esto incluye, pero no se limita a, fraude, spam, y la distribución de contenido ofensivo.\r\n"
				                + "\r\n"
				                + "7. Propiedad intelectual\r\n"
				                + "Todos los logos, imágenes, diseños y contenidos de MyMerch son propiedad exclusiva de la tienda \r\n"
				                + "y están protegidos por derechos de autor. Queda prohibida su reproducción sin autorización.\r\n"
				                + "\r\n"
				                + "8. Limitación de responsabilidad\r\n"
				                + "MyMerch no se hace responsable de daños indirectos, pérdidas de beneficios, \r\n"
				                + "o cualquier inconveniente derivado del uso de la tienda o de los productos adquiridos.\r\n"
				                + "\r\n"
				                + "9. Modificaciones\r\n"
				                + "Nos reservamos el derecho de modificar estos Términos y Condiciones en cualquier momento. \r\n"
				                + "Los cambios serán comunicados en el sitio web y entrarán en vigor inmediatamente.\r\n"
				                + "\r\n"
				                + "10. Ley aplicable\r\n"
				                + "Estos Términos y Condiciones se rigen por la legislación española. \r\n"
				                + "Cualquier disputa será resuelta en los tribunales de la ciudad de residencia de MyMerch.\r\n"        
        		);

        txtTerminos.setWrapStyleWord(true);
        txtTerminos.setLineWrap(true);
        txtTerminos.setEditable(false);
        txtTerminos.setCaretPosition(0);

        JScrollPane scroll = new JScrollPane(txtTerminos);
        dialog.add(scroll, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel();
        JButton btnAceptar = new JButton("Aceptar");
        JButton btnCancelar = new JButton("Cancelar");
        panelBotones.add(btnAceptar);
        panelBotones.add(btnCancelar);
        dialog.add(panelBotones, BorderLayout.SOUTH);

        btnAceptar.addActionListener(e -> dialog.dispose());
        btnCancelar.addActionListener(e -> {
            chk.setSelected(false);
            dialog.dispose();
        });

        dialog.setVisible(true);
    }

    private boolean usuarioExiste(String correo, String telefono) {
        boolean existe = false;
        Connection con = BaseDatosConfig.initBD("resources/db/MyMerch.db");
        if (con != null) {
            try {
                String sql = "SELECT * FROM Usuarios WHERE correo=? OR telefono=?";
                PreparedStatement pst = con.prepareStatement(sql);
                pst.setString(1, correo);
                pst.setString(2, telefono);
                if (pst.executeQuery().next()) {
                    existe = true;
                }
                pst.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                BaseDatosConfig.closeBD(con);
            }
        }
        return existe;
    }
}
