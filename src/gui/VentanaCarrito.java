package gui;

import java.awt.*;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import domain.Usuario;
import domain.ProductoCarrito;

public class VentanaCarrito extends JFrame {

    private Usuario usuario;
    private Map<String, ProductoCarrito> carrito;
    private Map<String, AtomicInteger> stockProductos;
    private JPanel pProductos;
    private JLabel lblTotal;
    private JTextField txtCupon;
    private JButton btnPagar;

    public VentanaCarrito(Usuario usuario, Map<String, ProductoCarrito> carrito,
                          JFrame ventanaCatalogo, Map<String, AtomicInteger> stockProductos) {
        this.usuario = usuario;
        this.carrito = carrito;
        this.stockProductos = stockProductos;

        setTitle("Carrito");
        setSize(750, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        crearPanelSuperior(ventanaCatalogo);
        crearPanelCentral();
        crearPanelInferior();

        actualizarCarrito();

        setVisible(true);
        setResizable(false);
    }

    private void crearPanelSuperior(JFrame ventanaCatalogo) {
        JPanel pSuperior = new JPanel(new BorderLayout());
        pSuperior.setBorder(new EmptyBorder(10, 10, 10, 10));
        pSuperior.setBackground(Color.WHITE);

        JLabel lblTitulo = new JLabel("           Tu Carrito", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Tahoma", Font.BOLD, 24));
        lblTitulo.setForeground(Color.BLACK);
        pSuperior.add(lblTitulo, BorderLayout.CENTER);

        ImageIcon iconCasa = new ImageIcon("resources/images/casa.png");
        Image imgCasa = iconCasa.getImage().getScaledInstance(35, 35, Image.SCALE_SMOOTH);
        JButton btnInicio = new JButton(new ImageIcon(imgCasa));
        btnInicio.setContentAreaFilled(false);
        btnInicio.setBorderPainted(false);
        btnInicio.setFocusPainted(false);
        btnInicio.addActionListener(e -> {
            ventanaCatalogo.setVisible(true);
            ventanaCatalogo.repaint();
            ventanaCatalogo.revalidate();
            dispose();
        });
        
        pSuperior.add(btnInicio, BorderLayout.EAST);
        add(pSuperior, BorderLayout.NORTH);
    }

    private void crearPanelCentral() {
        pProductos = new JPanel();
        pProductos.setLayout(new BoxLayout(pProductos, BoxLayout.Y_AXIS));
        pProductos.setBackground(new Color(245, 245, 245));

        JScrollPane scroll = new JScrollPane(pProductos);
        scroll.getVerticalScrollBar().setUnitIncrement(20);
        scroll.setBorder(null);

        add(scroll, BorderLayout.CENTER);
    }

    private void crearPanelInferior() {
        JPanel pInferior = new JPanel();
        pInferior.setLayout(new BoxLayout(pInferior, BoxLayout.Y_AXIS));
        pInferior.setBorder(new EmptyBorder(15, 50, 15, 50));
        pInferior.setBackground(new Color(245, 245, 245));

        lblTotal = new JLabel("Total: 0 €");
        lblTotal.setFont(new Font("Tahoma", Font.BOLD, 20));
        lblTotal.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel pCupon = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        pCupon.setBackground(new Color(245, 245, 245));
        txtCupon = new JTextField(12);
        JButton btnAplicarCupon = new JButton("Aplicar Cupón");
        btnAplicarCupon.setBackground(new Color(30, 144, 255));
        btnAplicarCupon.setForeground(Color.WHITE);
        btnAplicarCupon.setFocusPainted(false);
        btnAplicarCupon.addActionListener(e -> aplicarCupon());
        pCupon.add(new JLabel("Cupón:"));
        pCupon.add(txtCupon);
        pCupon.add(btnAplicarCupon);

        btnPagar = new JButton("Pagar");
        btnPagar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnPagar.setBackground(new Color(50, 205, 50));
        btnPagar.setForeground(Color.WHITE);
        btnPagar.setFont(new Font("Tahoma", Font.BOLD, 16));
        btnPagar.setFocusPainted(false);
        btnPagar.addActionListener(e -> {
            String textoTotal = lblTotal.getText().replace("Total: ", "").replace(" €", "");
            double totalConCupon = 0;
            try {
                totalConCupon = Double.parseDouble(textoTotal);
            } catch (NumberFormatException ex) {
                totalConCupon = carrito.values().stream().mapToDouble(ProductoCarrito::getSubtotal).sum();
            }

            new VentanaCrearPedido(usuario, carrito, stockProductos, totalConCupon);
            dispose();
        });

        pInferior.add(lblTotal);
        pInferior.add(Box.createRigidArea(new Dimension(0,10)));
        pInferior.add(pCupon);
        pInferior.add(Box.createRigidArea(new Dimension(0,10)));
        pInferior.add(btnPagar);

        add(pInferior, BorderLayout.SOUTH);
    }

    private void actualizarCarrito() {
        pProductos.removeAll();

        for (ProductoCarrito pc : carrito.values()) {
            JPanel panel = new JPanel(new BorderLayout(10,10));
            panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
            panel.setBackground(Color.WHITE);
            panel.setBorder(new LineBorder(new Color(200,200,200), 1, true));

            JLabel lblNombre = new JLabel(pc.getNombre());
            lblNombre.setFont(new Font("Tahoma", Font.BOLD, 16));

            JLabel lblCantidad = new JLabel(String.valueOf(pc.getCantidad()));
            lblCantidad.setFont(new Font("Tahoma", Font.PLAIN, 14));
            lblCantidad.setHorizontalAlignment(SwingConstants.CENTER);
            lblCantidad.setPreferredSize(new Dimension(30,25));

            JLabel lblPrecio = new JLabel(pc.getSubtotal() + " €");
            lblPrecio.setFont(new Font("Tahoma", Font.PLAIN, 14));

            JPanel panelCentro = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
            panelCentro.setOpaque(false);
            panelCentro.add(lblNombre);
            panelCentro.add(lblCantidad);
            panelCentro.add(lblPrecio);

            JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
            panelBotones.setOpaque(false);

            JButton btnMenos = new JButton("-");
            btnMenos.addActionListener(e -> {
                if (pc.getCantidad() > 1) {
                    pc.setCantidad(pc.getCantidad() - 1);
                    stockProductos.get(pc.getNombre()).incrementAndGet();
                } else {
                    stockProductos.get(pc.getNombre()).addAndGet(pc.getCantidad());
                    carrito.remove(pc.getNombre());
                }
                actualizarCarrito();
            });

            JButton btnMas = new JButton("+");
            btnMas.addActionListener(e -> {
                int stockDisponible = stockProductos.get(pc.getNombre()).get();
                if (stockDisponible > 0) {
                    pc.setCantidad(pc.getCantidad() + 1);
                    stockProductos.get(pc.getNombre()).decrementAndGet();
                    actualizarCarrito();
                } else {
                    JOptionPane.showMessageDialog(this, "No hay más stock disponible");
                }
            });

            JButton btnEliminar = new JButton("X");
            btnEliminar.setForeground(Color.RED);
            btnEliminar.addActionListener(e -> {
                stockProductos.get(pc.getNombre()).addAndGet(pc.getCantidad());
                carrito.remove(pc.getNombre());
                actualizarCarrito();
            });

            panelBotones.add(btnMenos);
            panelBotones.add(btnMas);
            panelBotones.add(btnEliminar);

            panel.add(panelCentro, BorderLayout.CENTER);
            panel.add(panelBotones, BorderLayout.EAST);

            pProductos.add(Box.createRigidArea(new Dimension(0,5)));
            pProductos.add(panel);
        }

        calcularTotal();

        btnPagar.setEnabled(!carrito.isEmpty());

        pProductos.revalidate();
        pProductos.repaint();
    }

    private void calcularTotal() {
        double total = carrito.values().stream().mapToDouble(ProductoCarrito::getSubtotal).sum();
        lblTotal.setText("Total: " + total + " €");
    }

    private void aplicarCupon() {
        if(carrito.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El carrito está vacío");
            return;
        }
        String cupon = txtCupon.getText().trim().toUpperCase();
        double total = carrito.values().stream().mapToDouble(ProductoCarrito::getSubtotal).sum();
        if (cupon.equals("DESCUENTO10")) total *= 0.9;
        lblTotal.setText("Total: " + total + " €");
    }
}
