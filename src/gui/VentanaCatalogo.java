package gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

        // Botón Carrito a la izquierda
        JButton btnCarrito = new JButton("Carrito");
        pSuperior.add(btnCarrito, BorderLayout.WEST);

        // Logo centrado
        ImageIcon iconLogo = new ImageIcon("resources/images/logo.png");
        Image imgOriginal = iconLogo.getImage();

        int nuevaAltura = 80;
        int nuevaAnchura = (int) (imgOriginal.getWidth(null) * ((double) nuevaAltura / imgOriginal.getHeight(null)));
        Image imgEscalada = imgOriginal.getScaledInstance(nuevaAnchura, nuevaAltura, Image.SCALE_SMOOTH);
        JLabel lblLogo = new JLabel(new ImageIcon(imgEscalada));
        lblLogo.setHorizontalAlignment(SwingConstants.CENTER);
        pSuperior.add(lblLogo, BorderLayout.CENTER);

        // Botón User a la derecha
        JButton btnUser = new JButton("User");
        pSuperior.add(btnUser, BorderLayout.EAST);

        add(pSuperior, BorderLayout.NORTH);

        // Panel central con productos
        JPanel pCentral = new JPanel();
        pCentral.setLayout(new GridLayout(0, 3, 20, 20)); // 3 columnas
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

        for (int i = 0; i < productos.length; i++) {
            String nombre = productos[i][0];
            String precio = productos[i][1];
            String rutaImagen = productos[i][2];
            pCentral.add(crearPanelProducto(nombre, precio, rutaImagen));
        }

        JScrollPane scroll = new JScrollPane(pCentral);
        add(scroll, BorderLayout.CENTER);

        setVisible(true);
        setResizable(false);
    }

    // Método para crear panel de cada producto
    private JPanel crearPanelProducto(String nombre, String precio, String rutaImagen) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        // Imagen
        ImageIcon icon = new ImageIcon(rutaImagen);
        Image imgEscalada = icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
        JLabel lblImagen = new JLabel(new ImageIcon(imgEscalada));
        lblImagen.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(lblImagen, BorderLayout.NORTH);

        // Nombre y precio
        JLabel lblNombre = new JLabel(nombre, SwingConstants.CENTER);
        lblNombre.setFont(new Font("Tahoma", Font.BOLD, 14));
        JLabel lblPrecio = new JLabel(precio, SwingConstants.CENTER);
        lblPrecio.setFont(new Font("Tahoma", Font.PLAIN, 12));

        JPanel pInfo = new JPanel(new GridLayout(2, 1));
        pInfo.add(lblNombre);
        pInfo.add(lblPrecio);
        panel.add(pInfo, BorderLayout.CENTER);

        // Botones - y + para cantidad
        JPanel pCantidad = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        final JLabel lblCantidad = new JLabel("0");
        lblCantidad.setPreferredSize(new Dimension(30, 20));
        lblCantidad.setHorizontalAlignment(SwingConstants.CENTER);

        JButton btnMenos = new JButton("-");
        btnMenos.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int cantidad = Integer.parseInt(lblCantidad.getText());
                if (cantidad > 0) {
                    lblCantidad.setText(String.valueOf(cantidad - 1));
                }
            }
        });

        JButton btnMas = new JButton("+");
        btnMas.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int cantidad = Integer.parseInt(lblCantidad.getText());
                lblCantidad.setText(String.valueOf(cantidad + 1));
            }
        });

        pCantidad.add(btnMenos);
        pCantidad.add(lblCantidad);
        pCantidad.add(btnMas);

        panel.add(pCantidad, BorderLayout.SOUTH); 

        return panel;
    }

}
