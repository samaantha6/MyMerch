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
    private Map<String, AtomicInteger> stockProductos = new HashMap<>();
    private JPanel pCentral;
    private JLabel lblCargando;
    private JTextField txtBusqueda;
    private boolean busquedaActiva = false;

    public VentanaCatalogo(Usuario usuario) {
        this.usuario = usuario;
        setTitle("Catálogo");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                dispose();
                new VentanaDespedida();
            }
        });

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

        // Logo
        ImageIcon iconLogo = new ImageIcon("resources/images/logo.png");
        Image imgOriginal = iconLogo.getImage();
        int nuevaAltura = 80;
        int nuevaAnchura = (int) (imgOriginal.getWidth(null) * ((double) nuevaAltura / imgOriginal.getHeight(null)));
        Image imgEscalada = imgOriginal.getScaledInstance(nuevaAnchura, nuevaAltura, Image.SCALE_SMOOTH);
        JLabel lblLogo = new JLabel(new ImageIcon(imgEscalada));
        lblLogo.setHorizontalAlignment(SwingConstants.CENTER);
        pSuperior.add(lblLogo, BorderLayout.CENTER);

        // Usuario
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

        // Campo de búsqueda
        JPanel pBusqueda = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        txtBusqueda = new JTextField(20);
        JButton btnBuscar = new JButton("Buscar");
        pBusqueda.add(txtBusqueda);
        pBusqueda.add(btnBuscar);
        pSuperior.add(pBusqueda, BorderLayout.SOUTH);

        btnBuscar.addActionListener(e -> {
            String nombreBuscado = txtBusqueda.getText().trim();
            if (!nombreBuscado.isEmpty()) {
                busquedaActiva = true; 
                java.util.List<Producto> coincidencias = buscarProductosRecursivo(BaseDatosConfig.obtenerProductos(), nombreBuscado);
                if (!coincidencias.isEmpty()) {
                    mostrarProductosFiltrados(coincidencias);
                } else {
                    JOptionPane.showMessageDialog(this, "Producto no encontrado");
                }
            } else {
                busquedaActiva = false; 
                pCentral.removeAll();
                cargarProductos();
            }
        });

        add(pSuperior, BorderLayout.NORTH);

        btnCarrito.addActionListener(e -> new VentanaCarrito(usuario, carrito, this, stockProductos));
    }

    private void crearPanelCentral() {
        JPanel pContenedor = new JPanel(new BorderLayout());
        pCentral = new JPanel(new GridLayout(0, 3, 20, 20));
        pCentral.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

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

        // Scroll infinito solo si no hay búsqueda activa
        JScrollBar vertical = scroll.getVerticalScrollBar();
        vertical.addAdjustmentListener(e -> {
            if (!e.getValueIsAdjusting() && !busquedaActiva) {
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
            stockProductos.putIfAbsent(prod.getNombre(), new AtomicInteger(prod.getStock()));
            pCentral.add(crearPanelProducto(prod));
        }
        pCentral.revalidate();
        pCentral.repaint();
    }

    private JPanel crearPanelProducto(Producto prod) {
        String nombre = prod.getNombre();
        double precio = prod.getPrecio();
        String rutaImagen = prod.getImagen();

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
        JPanel pInfo = new JPanel(new GridLayout(2,1));
        pInfo.add(lblNombre);
        pInfo.add(lblPrecio);
        panel.add(pInfo, BorderLayout.CENTER);

        JPanel pCantidad = new JPanel(new FlowLayout(FlowLayout.CENTER,5,5));
        JLabel lblCantidad = new JLabel("0");
        lblCantidad.setPreferredSize(new Dimension(30, 20));
        lblCantidad.setHorizontalAlignment(SwingConstants.CENTER);

        JButton btnMenos = new JButton("-");
        btnMenos.addActionListener(e -> {
            int cant = Integer.parseInt(lblCantidad.getText());
            if(cant>0) lblCantidad.setText(String.valueOf(cant-1));
        });

        JButton btnMas = new JButton("+");
        btnMas.addActionListener(e -> {
            int stockActual = stockProductos.get(nombre).get();
            int cant = Integer.parseInt(lblCantidad.getText());
            if(cant < stockActual) lblCantidad.setText(String.valueOf(cant+1));
        });

        JButton btnAgregarCarrito = new JButton(new ImageIcon("resources/images/carrito.png"));
        btnAgregarCarrito.setPreferredSize(new Dimension(40,25));
        btnAgregarCarrito.setFocusPainted(false);
        btnAgregarCarrito.setContentAreaFilled(false);
        btnAgregarCarrito.setBorderPainted(false);
        btnAgregarCarrito.addActionListener(e -> {
            int cant = Integer.parseInt(lblCantidad.getText());
            if (cant > 0) {
                ProductoCarrito pc = carrito.get(nombre);
                if (pc == null) {
                    carrito.put(nombre, new ProductoCarrito(prod.getId(), nombre, precio, cant));
                } else {
                    pc.setCantidad(pc.getCantidad() + cant);
                }
                stockProductos.get(nombre).addAndGet(-cant); 
                lblCantidad.setText("0");
                pCentral.revalidate();
                pCentral.repaint();
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
        pInfo.add(new JLabel("Stock: " + stockProductos.get(prod.getNombre()).get()));
        dialog.add(pInfo, BorderLayout.CENTER);

        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dialog.dispose());
        JPanel pBoton = new JPanel();
        pBoton.add(btnCerrar);
        dialog.add(pBoton, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    //Recursividad
    private java.util.List<Producto> buscarProductosRecursivo(java.util.List<Producto> lista, String texto) {
        java.util.List<Producto> encontrados = new ArrayList<>();
        buscarProductosRecursivo(lista, texto.toLowerCase(), 0, encontrados);
        return encontrados;
    }

    private void buscarProductosRecursivo(java.util.List<Producto> lista, String texto, int indice, java.util.List<Producto> encontrados) {
        if (indice >= lista.size()) return;
        Producto prod = lista.get(indice);
        if (prod.getNombre().toLowerCase().contains(texto)) {
            encontrados.add(prod);
        }
        buscarProductosRecursivo(lista, texto, indice + 1, encontrados);
    }

    private void mostrarProductosFiltrados(java.util.List<Producto> listaFiltrada) {
        pCentral.removeAll();
        for (Producto prod : listaFiltrada) {
            pCentral.add(crearPanelProducto(prod));
        }
        pCentral.revalidate();
        pCentral.repaint();
    }
}
