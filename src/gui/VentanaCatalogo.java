package gui;

import java.awt.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.*;
import domain.Usuario;
import domain.Producto;
import domain.ProductoCarrito;
import db.BaseDatosConfig;

public class VentanaCatalogo extends JFrame {

    private static final long serialVersionUID = 1L;
    private Usuario usuario;
    private Map<String, ProductoCarrito> carrito = new HashMap<>();
    private JPanel pCentral;
    private JLabel lblCargando;

    public VentanaCatalogo(Usuario usuario) {
        this.usuario = usuario;
        setTitle("Catálogo");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        crearPanelSuperior();
        crearPanelCentral();
        setVisible(true);
        setResizable(false);
    }

    private void crearPanelSuperior() {
        JPanel pSuperior = new JPanel(new BorderLayout());
        pSuperior.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        ImageIcon iconCarrito = new ImageIcon("resources/images/cesta.png");
        Image imgCarrito = iconCarrito.getImage().getScaledInstance(44, 44, Image.SCALE_SMOOTH);
        JButton btnCarrito = new JButton(new ImageIcon(imgCarrito));
        btnCarrito.setContentAreaFilled(false);
        btnCarrito.setBorderPainted(false);
        btnCarrito.setFocusPainted(false);
        pSuperior.add(btnCarrito, BorderLayout.WEST);

        ImageIcon iconLogo = new ImageIcon("resources/images/logo.png");
        Image imgOriginal = iconLogo.getImage();
        int nuevaAltura = 80;
        int nuevaAnchura = (int) (imgOriginal.getWidth(null) * ((double) nuevaAltura / imgOriginal.getHeight(null)));
        Image imgEscalada = imgOriginal.getScaledInstance(nuevaAnchura, nuevaAltura, Image.SCALE_SMOOTH);
        JLabel lblLogo = new JLabel(new ImageIcon(imgEscalada));
        lblLogo.setHorizontalAlignment(SwingConstants.CENTER);
        pSuperior.add(lblLogo, BorderLayout.CENTER);

        ImageIcon iconUser = new ImageIcon("resources/images/user.png");
        Image imgUser = iconUser.getImage().getScaledInstance(48, 48, Image.SCALE_SMOOTH);
        JButton btnUser = new JButton(new ImageIcon(imgUser));
        btnUser.setContentAreaFilled(false);
        btnUser.setBorderPainted(false);
        btnUser.setFocusPainted(false);
        pSuperior.add(btnUser, BorderLayout.EAST);

        JPopupMenu menuUser = new JPopupMenu();
        JMenuItem miPedidos = new JMenuItem("Pedidos");
        JMenuItem miCuenta = new JMenuItem("Mi cuenta");
        menuUser.add(miPedidos);
        menuUser.add(miCuenta);

        miCuenta.addActionListener(e -> new VentanaMiCuenta(usuario));
        miPedidos.addActionListener(e -> {
            new VentanaMisPedidos(usuario);
            dispose();
        });

        btnUser.addActionListener(e -> menuUser.show(btnUser, 0, btnUser.getHeight()));

        add(pSuperior, BorderLayout.NORTH);

        btnCarrito.addActionListener(e -> new VentanaCarrito(usuario, carrito, this));
    }

    private void crearPanelCentral() {
        JPanel pContenedor = new JPanel(new BorderLayout());
        pCentral = new JPanel(new GridLayout(0, 3, 20, 20));
        pCentral.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Hilo
        JScrollPane scroll = new JScrollPane(pCentral);
        scroll.getVerticalScrollBar().setUnitIncrement(30);
        scroll.getHorizontalScrollBar().setUnitIncrement(30);
        pContenedor.add(scroll, BorderLayout.CENTER);

        lblCargando = new JLabel("Cargando...", SwingConstants.CENTER);
        lblCargando.setFont(new Font("Tahoma", Font.BOLD, 20));
        lblCargando.setForeground(Color.BLUE);
        lblCargando.setVisible(false);
        pContenedor.add(lblCargando, BorderLayout.SOUTH);

        add(pContenedor, BorderLayout.CENTER);

        cargarProductos();

        // Scroll infinito
        JScrollBar vertical = scroll.getVerticalScrollBar();
        vertical.addAdjustmentListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int extent = vertical.getModel().getExtent();
                int max = vertical.getMaximum();
                int value = vertical.getValue();

                if (value + extent >= max - 50) {
                    lblCargando.setVisible(true);
                    new Thread(() -> {
                        try { Thread.sleep(500); } catch (InterruptedException ex) { ex.printStackTrace(); }

                        SwingUtilities.invokeLater(() -> {
                            cargarProductos();
                            lblCargando.setVisible(false);
                        });
                    }).start();
                }
            }
        });
    }

    private void cargarProductos() {
        java.util.List<Producto> listaProductos = BaseDatosConfig.obtenerProductos();
        for (Producto prod : listaProductos) {
            pCentral.add(crearPanelProducto(prod));
        }
        pCentral.revalidate();
        pCentral.repaint();
    }

    private JPanel crearPanelProducto(Producto prod) {
        String nombre = prod.getNombre();
        double precio = prod.getPrecio();
        String rutaImagen = prod.getImagen();

        // Stock manejado con AtomicInteger para poder modificar dentro de listeners
        AtomicInteger stockActual = new AtomicInteger(prod.getStock());

        JPanel panel = new JPanel(new BorderLayout(5,5));
        panel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        panel.setPreferredSize(new Dimension(180, 300));

        JPanel pImagen = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        pImagen.setOpaque(false);
        ImageIcon icon = new ImageIcon(rutaImagen);
        Image imgEscalada = icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
        JLabel lblImagen = new JLabel(new ImageIcon(imgEscalada));
        lblImagen.setHorizontalAlignment(SwingConstants.CENTER);

        lblImagen.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblImagen.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                abrirVentanaProducto(prod);
            }
        });

        pImagen.add(lblImagen);
        panel.add(pImagen, BorderLayout.NORTH);

        JLabel lblNombre = new JLabel(nombre, SwingConstants.CENTER);
        lblNombre.setFont(new Font("Tahoma", Font.BOLD, 14));
        JLabel lblPrecio = new JLabel(precio + " €", SwingConstants.CENTER);
        lblPrecio.setFont(new Font("Tahoma", Font.PLAIN, 12));
        JLabel lblStock = new JLabel("Stock: " + stockActual.get(), SwingConstants.CENTER);

        JPanel pInfo = new JPanel(new GridLayout(3,1));
        pInfo.add(lblNombre);
        pInfo.add(lblPrecio);
        pInfo.add(lblStock);
        panel.add(pInfo, BorderLayout.CENTER);

        JPanel pCantidad = new JPanel(new FlowLayout(FlowLayout.CENTER,5,5));
        JLabel lblCantidad = new JLabel("0");
        lblCantidad.setPreferredSize(new Dimension(30, 20));
        lblCantidad.setHorizontalAlignment(SwingConstants.CENTER);

        JButton btnMenos = new JButton("-");
        btnMenos.addActionListener(e -> {
            int cant = Integer.parseInt(lblCantidad.getText());
            if(cant > 0) lblCantidad.setText(String.valueOf(cant-1));
        });

        JButton btnMas = new JButton("+");
        btnMas.addActionListener(e -> {
            int cant = Integer.parseInt(lblCantidad.getText());
            if(cant < stockActual.get()) lblCantidad.setText(String.valueOf(cant+1));
        });

        JButton btnAgregarCarrito = new JButton(new ImageIcon("resources/images/carrito.png"));
        btnAgregarCarrito.setPreferredSize(new Dimension(40,25));
        btnAgregarCarrito.setFocusPainted(false);
        btnAgregarCarrito.setContentAreaFilled(false);
        btnAgregarCarrito.setBorderPainted(false);

        btnAgregarCarrito.addActionListener(e -> {
            int cant = Integer.parseInt(lblCantidad.getText());
            if(cant > 0 && cant <= stockActual.get()){
                ProductoCarrito pc = carrito.get(nombre);
                if(pc == null) carrito.put(nombre, new ProductoCarrito(nombre, precio, cant));
                else pc.setCantidad(pc.getCantidad() + cant);

                stockActual.addAndGet(-cant);
                lblStock.setText("Stock: " + stockActual.get());

                lblCantidad.setText("0");
            }
        });

        pCantidad.add(btnMenos);
        pCantidad.add(lblCantidad);
        pCantidad.add(btnMas);
        pCantidad.add(btnAgregarCarrito);

        panel.add(pCantidad, BorderLayout.SOUTH);

        return panel;
    }

    private void abrirVentanaProducto(Producto prod) {
        JDialog dialog = new JDialog(this, prod.getNombre(), true);
        dialog.setSize(400, 500);
        dialog.setLayout(new BorderLayout(10,10));
        dialog.setLocationRelativeTo(this);

        ImageIcon icon = new ImageIcon(prod.getImagen());
        Image imgEscalada = icon.getImage().getScaledInstance(300, 300, Image.SCALE_SMOOTH);
        JLabel lblImagen = new JLabel(new ImageIcon(imgEscalada));
        lblImagen.setHorizontalAlignment(SwingConstants.CENTER);
        dialog.add(lblImagen, BorderLayout.NORTH);
        
        JPanel pInfo = new JPanel(new GridLayout(4,1,5,5));
        pInfo.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        pInfo.add(new JLabel("Nombre: " + prod.getNombre()));
        pInfo.add(new JLabel("Descripción: " + prod.getDescripcion()));
        pInfo.add(new JLabel("Precio: " + prod.getPrecio() + " €"));
        pInfo.add(new JLabel("Stock: " + prod.getStock()));
        dialog.add(pInfo, BorderLayout.CENTER);

        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dialog.dispose());
        JPanel pBoton = new JPanel();
        pBoton.add(btnCerrar);
        dialog.add(pBoton, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }
}
