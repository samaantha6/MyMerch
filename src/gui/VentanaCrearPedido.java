package gui;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import domain.ProductoCarrito;
import domain.Usuario;

public class VentanaCrearPedido extends JFrame {

    private Usuario usuario;
    private Map<String, ProductoCarrito> carrito;
    private Map<String, AtomicInteger> stockProductos;

    private JTextField txtDireccion, txtCP;
    private JComboBox<String> comboPais, comboProvincia;

    private JRadioButton rbContrareembolso, rbTarjeta;
    private JTextField txtNumTarjeta, txtFechaCad, txtCVV, txtTitular;

    private JLabel lblTotal;
    private double envioExpress = 7.0;
    private double totalCarrito;

    private Map<String, String[]> provinciasPorPais;

    public VentanaCrearPedido(Usuario usuario, Map<String, ProductoCarrito> carrito,
                              Map<String, AtomicInteger> stockProductos, double totalCarrito) {
        this.usuario = usuario;
        this.carrito = carrito;
        this.stockProductos = stockProductos;
        this.totalCarrito = totalCarrito;

        inicializarProvincias();

        setTitle("Crear Pedido");
        setSize(700, 750);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        crearPanelSuperior();
        crearPanelCentral();
        setVisible(true);
        setResizable(false);
    }

    private void inicializarProvincias() {
        provinciasPorPais = new HashMap<>();
        provinciasPorPais.put("España", new String[]{"Madrid", "Barcelona", "Valencia", "Sevilla", "Bilbao"});
        provinciasPorPais.put("Francia", new String[]{"París", "Lyon", "Marsella"});
        provinciasPorPais.put("Italia", new String[]{"Roma", "Milán", "Nápoles"});
        provinciasPorPais.put("Portugal", new String[]{"Lisboa", "Oporto"});
        provinciasPorPais.put("Alemania", new String[]{"Berlín", "Hamburgo", "Múnich"});
        provinciasPorPais.put("Reino Unido", new String[]{"Londres", "Manchester"});
        provinciasPorPais.put("Estados Unidos", new String[]{"Nueva York", "Los Ángeles", "Chicago"});
        provinciasPorPais.put("México", new String[]{"Ciudad de México", "Guadalajara"});
        provinciasPorPais.put("Argentina", new String[]{"Buenos Aires", "Córdoba"});
        provinciasPorPais.put("Colombia", new String[]{"Bogotá", "Medellín"});
    }

    private void crearPanelSuperior() {
        JPanel pSuperior = new JPanel(new BorderLayout());
        pSuperior.setBorder(new EmptyBorder(10, 10, 10, 10));
        pSuperior.setBackground(Color.WHITE);

        JLabel lblTitulo = new JLabel("Crear Pedido", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Tahoma", Font.BOLD, 24));

        JButton btnAtras = new JButton("<-");
        btnAtras.setFocusPainted(false);
        btnAtras.setContentAreaFilled(false);
        btnAtras.setBorderPainted(false);
        btnAtras.addActionListener(e -> dispose());

        pSuperior.add(btnAtras, BorderLayout.WEST);
        pSuperior.add(lblTitulo, BorderLayout.CENTER);
        add(pSuperior, BorderLayout.NORTH);
    }

    private void crearPanelCentral() {
        JPanel pCentral = new JPanel();
        pCentral.setLayout(new BoxLayout(pCentral, BoxLayout.Y_AXIS));
        pCentral.setBorder(new EmptyBorder(10, 20, 10, 20));
        JScrollPane scroll = new JScrollPane(pCentral);
        add(scroll, BorderLayout.CENTER);

        JPanel pEntrega = new JPanel(new GridLayout(4, 2, 10, 10));
        pEntrega.setBorder(BorderFactory.createTitledBorder("Detalles de entrega"));

        pEntrega.add(new JLabel("Dirección:"));
        txtDireccion = new JTextField();
        pEntrega.add(txtDireccion);

        pEntrega.add(new JLabel("País:"));
        comboPais = new JComboBox<>(provinciasPorPais.keySet().toArray(new String[0]));
        comboPais.addActionListener(e -> actualizarProvincias());
        pEntrega.add(comboPais);

        pEntrega.add(new JLabel("Provincia:"));
        comboProvincia = new JComboBox<>();
        actualizarProvincias();
        pEntrega.add(comboProvincia);

        pEntrega.add(new JLabel("Código Postal:"));
        txtCP = new JTextField();
        pEntrega.add(txtCP);

        pCentral.add(pEntrega);
        pCentral.add(Box.createRigidArea(new Dimension(0, 15)));

        JPanel pPago = new JPanel();
        pPago.setLayout(new BoxLayout(pPago, BoxLayout.Y_AXIS));
        pPago.setBorder(BorderFactory.createTitledBorder("Método de pago"));

        rbContrareembolso = new JRadioButton("Contrareembolso");
        rbTarjeta = new JRadioButton("Tarjeta de crédito/débito");

        ButtonGroup bgPago = new ButtonGroup();
        bgPago.add(rbContrareembolso);
        bgPago.add(rbTarjeta);
        rbContrareembolso.setSelected(true);

        pPago.add(rbContrareembolso);
        pPago.add(rbTarjeta);

        JPanel pTarjeta = new JPanel(new GridLayout(4, 2, 10, 10));
        pTarjeta.setBorder(new EmptyBorder(10, 0, 10, 0));

        txtNumTarjeta = new JTextField();
        txtFechaCad = new JTextField();
        txtCVV = new JTextField();
        txtTitular = new JTextField();

        pTarjeta.add(new JLabel("Número de tarjeta:"));
        pTarjeta.add(txtNumTarjeta);
        pTarjeta.add(new JLabel("Fecha caducidad (MM/AA):"));
        pTarjeta.add(txtFechaCad);
        pTarjeta.add(new JLabel("CVV:"));
        pTarjeta.add(txtCVV);
        pTarjeta.add(new JLabel("Titular:"));
        pTarjeta.add(txtTitular);

        pPago.add(pTarjeta);
        pCentral.add(pPago);
        pCentral.add(Box.createRigidArea(new Dimension(0, 15)));

        rbTarjeta.addActionListener(e -> habilitarCamposTarjeta(true));
        rbContrareembolso.addActionListener(e -> habilitarCamposTarjeta(false));
        habilitarCamposTarjeta(false);

        JPanel pEnvio = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblEnvio = new JLabel("Envío express: 7 €");
        pEnvio.add(lblEnvio);
        pCentral.add(pEnvio);

        lblTotal = new JLabel("Total: " + (totalCarrito + envioExpress) + " €");
        lblTotal.setFont(new Font("Tahoma", Font.BOLD, 18));
        lblTotal.setAlignmentX(Component.CENTER_ALIGNMENT);
        pCentral.add(lblTotal);
        pCentral.add(Box.createRigidArea(new Dimension(0, 15)));

        JButton btnPagar = new JButton("Pagar");
        btnPagar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnPagar.addActionListener(e -> procesarPago());
        pCentral.add(btnPagar);
    }

    private void habilitarCamposTarjeta(boolean habilitar) {
        txtNumTarjeta.setEnabled(habilitar);
        txtFechaCad.setEnabled(habilitar);
        txtCVV.setEnabled(habilitar);
        txtTitular.setEnabled(habilitar);
    }

    private void actualizarProvincias() {
        comboProvincia.removeAllItems();
        String pais = (String) comboPais.getSelectedItem();
        String[] provincias = provinciasPorPais.get(pais);
        if (provincias != null) {
            for (String p : provincias) comboProvincia.addItem(p);
        }
    }

    private void procesarPago() {
        if (txtDireccion.getText().trim().isEmpty() || txtCP.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debes rellenar la dirección y el código postal");
            return;
        }

        if (rbTarjeta.isSelected() && !validarTarjeta()) return;

        JOptionPane.showMessageDialog(this, "Pago correcto");
        dispose();
    }

    private boolean validarTarjeta() {
        String num = txtNumTarjeta.getText().trim();
        String fecha = txtFechaCad.getText().trim();
        String cvv = txtCVV.getText().trim();
        String titular = txtTitular.getText().trim();

        if (num.isEmpty() || fecha.isEmpty() || cvv.isEmpty() || titular.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debes rellenar todos los datos de la tarjeta");
            return false;
        }
        if (!num.matches("\\d{16}")) {
            JOptionPane.showMessageDialog(this, "Número de tarjeta inválido (16 dígitos)");
            return false;
        }
        if (!fecha.matches("\\d{2}/\\d{2}")) {
            JOptionPane.showMessageDialog(this, "Fecha de caducidad inválida (MM/AA)");
            return false;
        }
        if (!cvv.matches("\\d{3}")) {
            JOptionPane.showMessageDialog(this, "CVV inválido (3 dígitos)");
            return false;
        }
        return true;
    }
}
