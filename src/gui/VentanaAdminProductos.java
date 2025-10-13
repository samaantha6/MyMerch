package gui;

import java.awt.*;
import java.io.*;
import java.nio.file.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import db.BaseDatosConfig;
import domain.Producto;
import domain.Usuario;

public class VentanaAdminProductos extends JFrame {

    private JTextField txtNombre, txtPrecio, txtStock;
    private JTextArea txtDescripcion;
    private JLabel lblImagenSeleccionada, lblVistaPrevia;
    private JButton btnSeleccionarImagen, btnAgregarProducto;
    private File archivoImagen = null;

    public VentanaAdminProductos(Usuario usuario) {
        super("Administración de Productos - " + usuario.getNombre());
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        setSize(500, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panelCentral = new JPanel(new GridBagLayout());
        panelCentral.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5,5,5,5);

        // Nombre
        gbc.gridx = 0; gbc.gridy = 0;
        panelCentral.add(new JLabel("Nombre:"), gbc);
        txtNombre = new JTextField();
        gbc.gridx = 1; gbc.gridy = 0;
        panelCentral.add(txtNombre, gbc);

        // Descripción
        gbc.gridx = 0; gbc.gridy = 1;
        panelCentral.add(new JLabel("Descripción:"), gbc);
        txtDescripcion = new JTextArea(4, 20);
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        JScrollPane scrollDesc = new JScrollPane(txtDescripcion);
        gbc.gridx = 1; gbc.gridy = 1;
        panelCentral.add(scrollDesc, gbc);

        // Precio
        gbc.gridx = 0; gbc.gridy = 2;
        panelCentral.add(new JLabel("Precio (€):"), gbc);
        txtPrecio = new JTextField();
        gbc.gridx = 1; gbc.gridy = 2;
        panelCentral.add(txtPrecio, gbc);

        // Stock
        gbc.gridx = 0; gbc.gridy = 3;
        panelCentral.add(new JLabel("Stock:"), gbc);
        txtStock = new JTextField();
        gbc.gridx = 1; gbc.gridy = 3;
        panelCentral.add(txtStock, gbc);

        // Imagen
        gbc.gridx = 0; gbc.gridy = 4;
        panelCentral.add(new JLabel("Imagen:"), gbc);
        JPanel panelImagen = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        lblImagenSeleccionada = new JLabel("No seleccionada");
        btnSeleccionarImagen = new JButton("Seleccionar...");
        btnSeleccionarImagen.addActionListener(e -> seleccionarImagen());
        panelImagen.add(lblImagenSeleccionada);
        panelImagen.add(btnSeleccionarImagen);
        gbc.gridx = 1; gbc.gridy = 4;
        panelCentral.add(panelImagen, gbc);

        // Vista previa de la imagen
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        lblVistaPrevia = new JLabel();
        lblVistaPrevia.setPreferredSize(new Dimension(200, 200));
        lblVistaPrevia.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        panelCentral.add(lblVistaPrevia, gbc);

        // Botón agregar producto
        btnAgregarProducto = new JButton("Añadir Producto");
        btnAgregarProducto.addActionListener(e -> agregarProducto());
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panelCentral.add(btnAgregarProducto, gbc);

        add(panelCentral, BorderLayout.CENTER);
        setResizable(false);
        setVisible(true);
    }

    private void seleccionarImagen() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Archivos de imagen", "jpg", "png", "jpeg", "gif"));
        int opcion = chooser.showOpenDialog(this);
        if(opcion == JFileChooser.APPROVE_OPTION) {
            archivoImagen = chooser.getSelectedFile();
            lblImagenSeleccionada.setText(archivoImagen.getName());

            try {
                Image img = ImageIO.read(archivoImagen).getScaledInstance(200, 200, Image.SCALE_SMOOTH);
                lblVistaPrevia.setIcon(new ImageIcon(img));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void agregarProducto() {
        String nombre = txtNombre.getText().trim();
        String descripcion = txtDescripcion.getText().trim();
        String precioStr = txtPrecio.getText().trim();
        String stockStr = txtStock.getText().trim();

        if(nombre.isEmpty() || precioStr.isEmpty() || stockStr.isEmpty() || archivoImagen == null) {
            JOptionPane.showMessageDialog(this, "Rellena todos los campos y selecciona una imagen.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double precio;
        int stock;
        try {
            precio = Double.parseDouble(precioStr);
            stock = Integer.parseInt(stockStr);
            if(precio < 0 || stock < 0) throw new NumberFormatException();
        } catch(NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Precio y stock deben ser números positivos.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String carpetaDestino = "resources/images/";
        File dir = new File(carpetaDestino);
        if(!dir.exists()) dir.mkdirs();

        File destino = new File(carpetaDestino + archivoImagen.getName());
        try {
            Files.copy(archivoImagen.toPath(), destino.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al copiar la imagen.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String nombreImagen = "resources/images/" + destino.getName(); 
        Producto p = new Producto(0, nombre, descripcion, precio, nombreImagen, stock);

        try (Connection con = BaseDatosConfig.initBD("resources/db/MyMerch.db")) {
            String sql = "INSERT INTO Productos(nombre, descripcion, precio, imagen, stock) VALUES(?,?,?,?,?)";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, p.getNombre());
            pst.setString(2, p.getDescripcion());
            pst.setDouble(3, p.getPrecio());
            pst.setString(4, p.getImagen());
            pst.setInt(5, p.getStock());
            pst.executeUpdate();
            pst.close();

            JOptionPane.showMessageDialog(this, "Producto añadido correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            limpiarCampos();

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al añadir el producto.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarCampos() {
        txtNombre.setText("");
        txtDescripcion.setText("");
        txtPrecio.setText("");
        txtStock.setText("");
        lblImagenSeleccionada.setText("No seleccionada");
        lblVistaPrevia.setIcon(null);
        archivoImagen = null;
    }
}
