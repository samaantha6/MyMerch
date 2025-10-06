package gui;

import java.awt.*;
import javax.swing.*;

public class VentanaCatalogo extends JFrame {

    private static final long serialVersionUID = 1L;

    public VentanaCatalogo() {
        setTitle("Catálogo");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Panel superior con Carrito, Logo centrado y User
        JPanel pSuperior = new JPanel(new BorderLayout());
        pSuperior.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton btnCarrito = new JButton("Carrito");
        pSuperior.add(btnCarrito, BorderLayout.WEST);

        ImageIcon iconLogo = new ImageIcon("resources/images/logo.png");
        Image imgOriginal = iconLogo.getImage();
        int nuevaAltura = 80;
        int nuevaAnchura = (int) (imgOriginal.getWidth(null) * ((double) nuevaAltura / imgOriginal.getHeight(null)));
        Image imgEscalada = imgOriginal.getScaledInstance(nuevaAnchura, nuevaAltura, Image.SCALE_SMOOTH);
        JLabel lblLogo = new JLabel(new ImageIcon(imgEscalada));
        lblLogo.setHorizontalAlignment(SwingConstants.CENTER);
        pSuperior.add(lblLogo, BorderLayout.CENTER);

        JButton btnUser = new JButton("User");
        pSuperior.add(btnUser, BorderLayout.EAST);

        add(pSuperior, BorderLayout.NORTH);

        // Panel central contenedor para productos + "Cargando..."
        JPanel pContenedor = new JPanel(new BorderLayout());

        // Panel de productos
        JPanel pCentral = new JPanel(new GridLayout(0, 3, 20, 20));
        pCentral.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Datos de ejemplo de productos
        String[][] productos = {
                {"Camiseta", "15€", "resources/images/camiseta.png"},
                {"Gorra", "10€", "resources/images/gorra.png"},
                {"Taza", "8€", "resources/images/taza.png"},
                {"Sudadera", "25€", "resources/images/sudadera.png"},
                {"Póster", "5€", "resources/images/poster.png"},
                {"Llaveros", "3€", "resources/images/llavero.png"}
        };

        // Añadimos los productos iniciales
        for (int i = 0; i < productos.length; i++) {
            pCentral.add(crearPanelProducto(productos[i][0], productos[i][1], productos[i][2]));
        }

        JScrollPane scroll = new JScrollPane(pCentral);
  
        // Velocidad de scroll
        scroll.getVerticalScrollBar().setUnitIncrement(30); // por defecto es ~10, pon 30 o 40 para más rápido
        scroll.getHorizontalScrollBar().setUnitIncrement(30);

        pContenedor.add(scroll, BorderLayout.CENTER);

        // Label de cargando
        JLabel lblCargando = new JLabel("Cargando...", SwingConstants.CENTER);
        lblCargando.setFont(new Font("Tahoma", Font.BOLD, 20));
        lblCargando.setForeground(Color.BLUE);
        lblCargando.setVisible(false);
        pContenedor.add(lblCargando, BorderLayout.SOUTH);

        add(pContenedor, BorderLayout.CENTER);

        // Hilo Scroll infinito con "Cargando..."
        JScrollBar vertical = scroll.getVerticalScrollBar();
        vertical.addAdjustmentListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int extent = vertical.getModel().getExtent();
                int max = vertical.getMaximum();
                int value = vertical.getValue();

                if (value + extent >= max - 50) {
                    // Mostrar cargando
                    lblCargando.setVisible(true);

                    new Thread(() -> {
                        try {
                            Thread.sleep(500); // simula tiempo de carga
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }

                        // Añadimos productos nuevamente
                        for (int i = 0; i < productos.length; i++) {
                            String nombre = productos[i][0];
                            String precio = productos[i][1];
                            String rutaImagen = productos[i][2];

                            SwingUtilities.invokeLater(() -> pCentral.add(crearPanelProducto(nombre, precio, rutaImagen)));
                        }

                        // Actualizamos interfaz y ocultamos cargando
                        SwingUtilities.invokeLater(() -> {
                            pCentral.revalidate();
                            pCentral.repaint();
                            lblCargando.setVisible(false);
                        });
                    }).start();
                }
            }
        });

        setVisible(true);
        setResizable(false);
    }

    private JPanel crearPanelProducto(String nombre, String precio, String rutaImagen) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        panel.setPreferredSize(new Dimension(180, 300));

        JPanel pImagen = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        pImagen.setOpaque(false);

        ImageIcon icon = new ImageIcon(rutaImagen);
        Image imgEscalada = icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
        JLabel lblImagen = new JLabel(new ImageIcon(imgEscalada));
        lblImagen.setHorizontalAlignment(SwingConstants.CENTER);

        pImagen.add(lblImagen);
        panel.add(pImagen, BorderLayout.NORTH);

        JLabel lblNombre = new JLabel(nombre, SwingConstants.CENTER);
        lblNombre.setFont(new Font("Tahoma", Font.BOLD, 14));
        JLabel lblPrecio = new JLabel(precio, SwingConstants.CENTER);
        lblPrecio.setFont(new Font("Tahoma", Font.PLAIN, 12));

        JPanel pInfo = new JPanel(new GridLayout(2, 1));
        pInfo.add(lblNombre);
        pInfo.add(lblPrecio);
        panel.add(pInfo, BorderLayout.CENTER);

        JPanel pCantidad = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        final JLabel lblCantidad = new JLabel("0");
        lblCantidad.setPreferredSize(new Dimension(30, 20));
        lblCantidad.setHorizontalAlignment(SwingConstants.CENTER);

        JButton btnMenos = new JButton("-");
        btnMenos.addActionListener(e -> {
            int cantidad = Integer.parseInt(lblCantidad.getText());
            if (cantidad > 0) lblCantidad.setText(String.valueOf(cantidad - 1));
        });

        JButton btnMas = new JButton("+");
        btnMas.addActionListener(e -> {
            int cantidad = Integer.parseInt(lblCantidad.getText());
            lblCantidad.setText(String.valueOf(cantidad + 1));
        });

        pCantidad.add(btnMenos);
        pCantidad.add(lblCantidad);
        pCantidad.add(btnMas);

        panel.add(pCantidad, BorderLayout.SOUTH);

        return panel;
    }
}
