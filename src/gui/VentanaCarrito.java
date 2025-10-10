package gui;

import java.awt.*;
import java.awt.event.*;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import domain.Usuario;
import domain.ProductoCarrito;

public class VentanaCarrito extends JFrame {

    private Usuario usuario;
    private Map<String, ProductoCarrito> carrito;
    private JPanel pProductos;
    private JLabel lblTotal;
    private JTextField txtCupon;

    public VentanaCarrito(Usuario usuario, Map<String, ProductoCarrito> carrito, JFrame ventanaCatalogo) {
        this.usuario = usuario;
        this.carrito = carrito;

        setTitle("Carrito");
        setSize(700, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel pSuperior = new JPanel(new BorderLayout());
        pSuperior.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel lblTitulo = new JLabel("Carrito", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Tahoma", Font.BOLD, 22));
        pSuperior.add(lblTitulo, BorderLayout.CENTER);

        JButton btnInicio = new JButton(new ImageIcon("resources/images/casa.png"));
        btnInicio.setContentAreaFilled(false);
        btnInicio.setBorderPainted(false);
        btnInicio.setFocusPainted(false);
        btnInicio.addActionListener(e -> {
            ventanaCatalogo.setVisible(true);
            dispose();
        });
        pSuperior.add(btnInicio, BorderLayout.EAST);

        add(pSuperior, BorderLayout.NORTH);

        pProductos = new JPanel();
        pProductos.setLayout(new BoxLayout(pProductos, BoxLayout.Y_AXIS));
        JScrollPane scroll = new JScrollPane(pProductos);
        add(scroll, BorderLayout.CENTER);

        JPanel pInferior = new JPanel();
        pInferior.setLayout(new BoxLayout(pInferior, BoxLayout.Y_AXIS));
        pInferior.setBorder(new EmptyBorder(10, 50, 10, 50));

        lblTotal = new JLabel("Total: 0 €");
        lblTotal.setFont(new Font("Tahoma", Font.BOLD, 18));
        lblTotal.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel pCupon = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        txtCupon = new JTextField(10);
        JButton btnAplicarCupon = new JButton("Aplicar Cupón");
        btnAplicarCupon.addActionListener(e -> aplicarCupon());
        pCupon.add(new JLabel("Cupón:"));
        pCupon.add(txtCupon);
        pCupon.add(btnAplicarCupon);

        JButton btnPagar = new JButton("Pagar");
        btnPagar.setAlignmentX(Component.CENTER_ALIGNMENT);

        pInferior.add(lblTotal);
        pInferior.add(pCupon);
        pInferior.add(Box.createRigidArea(new Dimension(0,10)));
        pInferior.add(btnPagar);

        add(pInferior, BorderLayout.SOUTH);

        actualizarCarrito();

        setVisible(true);
        setResizable(false);
    }

    private void actualizarCarrito() {
        pProductos.removeAll();
        for (ProductoCarrito pc : carrito.values()) {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
            panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

            JLabel lblNombre = new JLabel(pc.getNombre());
            lblNombre.setPreferredSize(new Dimension(200, 25));

            JButton btnMenos = new JButton("-");
            btnMenos.addActionListener(e -> {
                if (pc.getCantidad() > 1) pc.setCantidad(pc.getCantidad() - 1);
                else carrito.remove(pc.getNombre());
                actualizarCarrito();
            });

            JLabel lblCantidad = new JLabel(String.valueOf(pc.getCantidad()));
            lblCantidad.setPreferredSize(new Dimension(30, 25));
            lblCantidad.setHorizontalAlignment(SwingConstants.CENTER);

            JButton btnMas = new JButton("+");
            btnMas.addActionListener(e -> {
                pc.setCantidad(pc.getCantidad() + 1);
                actualizarCarrito();
            });

            JButton btnEliminar = new JButton("X");
            btnEliminar.addActionListener(e -> {
                carrito.remove(pc.getNombre());
                actualizarCarrito();
            });

            JLabel lblPrecio = new JLabel(pc.getSubtotal() + " €");
            lblPrecio.setPreferredSize(new Dimension(80,25));

            panel.add(lblNombre);
            panel.add(btnMenos);
            panel.add(lblCantidad);
            panel.add(btnMas);
            panel.add(lblPrecio);
            panel.add(btnEliminar);

            pProductos.add(panel);
        }
        calcularTotal();
        pProductos.revalidate();
        pProductos.repaint();
    }

    private void calcularTotal() {
        double total = carrito.values().stream().mapToDouble(ProductoCarrito::getSubtotal).sum();
        lblTotal.setText("Total: " + total + " €");
    }

    private void aplicarCupon() {
        String cupon = txtCupon.getText().trim().toUpperCase();
        double total = carrito.values().stream().mapToDouble(ProductoCarrito::getSubtotal).sum();
        if (cupon.equals("DESCUENTO10")) total *= 0.9;
        lblTotal.setText("Total: " + total + " €");
    }
} 

