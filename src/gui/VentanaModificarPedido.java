package gui;

import java.awt.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import db.BaseDatosConfig;
import domain.Usuario;

public class VentanaModificarPedido extends JFrame {

    private static final long serialVersionUID = 1L;
    private Usuario usuario;
    private int idPedido;
    private JTextField txtDireccion, txtCP;
    private JComboBox<String> comboPais, comboProvincia;
    private JTable tablaProductos;
    private DefaultTableModel modeloTabla;
    private VentanaMisPedidos ventanaAnterior;

    private final Map<String, String[]> provinciasPorPais = new HashMap<>();

    public VentanaModificarPedido(Usuario usuario, int idPedido, VentanaMisPedidos ventanaMisPedidos) {
        this.usuario = usuario;
        this.idPedido = idPedido;
        this.ventanaAnterior = ventanaMisPedidos;

        inicializarProvincias();

        setTitle("Modificar Pedido");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel pSuperior = new JPanel(new BorderLayout());
        pSuperior.setBorder(new EmptyBorder(10, 10, 10, 10));

        JButton btnAtras = new JButton("←");
        btnAtras.setFont(new Font("Tahoma", Font.BOLD, 16));
        btnAtras.setFocusPainted(false);
        btnAtras.setBorderPainted(false);
        btnAtras.setContentAreaFilled(false);
        btnAtras.addActionListener(e -> {
            this.dispose();
            ventanaAnterior.setVisible(true);
        });
        
        pSuperior.add(btnAtras, BorderLayout.WEST);

        JLabel lblTitulo = new JLabel("Modificar Pedido", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Tahoma", Font.BOLD, 22));
        pSuperior.add(lblTitulo, BorderLayout.CENTER);

        add(pSuperior, BorderLayout.NORTH);

        JPanel pCentro = new JPanel(new BorderLayout());
        pCentro.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel lblID = new JLabel("ID: " + idPedido);
        lblID.setFont(new Font("Tahoma", Font.PLAIN, 16));
        pCentro.add(lblID, BorderLayout.NORTH);

        JTabbedPane pestañas = new JTabbedPane();

        JPanel pProductos = new JPanel(new BorderLayout());
        modeloTabla = new DefaultTableModel(new String[]{"Producto", "Cantidad", "Precio"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaProductos = new JTable(modeloTabla);
        tablaProductos.setRowHeight(30);
        JScrollPane scrollProductos = new JScrollPane(tablaProductos);
        pProductos.add(scrollProductos, BorderLayout.CENTER);

        JButton btnContinuar = new JButton("Continuar →");
        btnContinuar.addActionListener(e -> pestañas.setSelectedIndex(1));
        JPanel pBoton = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pBoton.add(btnContinuar);
        pProductos.add(pBoton, BorderLayout.SOUTH);

        pestañas.addTab("Productos del pedido", pProductos);

        JPanel pDetalles = new JPanel(new GridBagLayout());
        pDetalles.setBorder(new EmptyBorder(10, 50, 10, 50));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int fila = 0;

        txtDireccion = new JTextField();
        txtDireccion.setEditable(false);
        JButton btnEditarDireccion = new JButton(new ImageIcon("resources/images/lapiz.png"));
        btnEditarDireccion.setPreferredSize(new Dimension(40, 25));
        btnEditarDireccion.setFocusPainted(false);
        btnEditarDireccion.setContentAreaFilled(false);
        btnEditarDireccion.setBorderPainted(false);

        btnEditarDireccion.addActionListener(e -> txtDireccion.setEditable(!txtDireccion.isEditable()));

        JPanel panelDireccion = new JPanel(new BorderLayout(5, 0));
        panelDireccion.add(new JLabel("Dirección:"), BorderLayout.WEST);
        panelDireccion.add(txtDireccion, BorderLayout.CENTER);
        panelDireccion.add(btnEditarDireccion, BorderLayout.EAST);

        gbc.gridx = 0;
        gbc.gridy = fila++;
        gbc.gridwidth = 2;
        pDetalles.add(panelDireccion, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = fila;
        pDetalles.add(new JLabel("País:"), gbc);

        comboPais = new JComboBox<>(provinciasPorPais.keySet().toArray(new String[0]));
        comboPais.setPreferredSize(new Dimension(200, 25));
        comboPais.addActionListener(e -> actualizarProvincias());
        gbc.gridx = 1;
        pDetalles.add(comboPais, gbc);
        fila++;

        gbc.gridx = 0;
        gbc.gridy = fila;
        pDetalles.add(new JLabel("Provincia:"), gbc);

        comboProvincia = new JComboBox<>();
        comboProvincia.setPreferredSize(new Dimension(200, 25));
        actualizarProvincias();
        gbc.gridx = 1;
        pDetalles.add(comboProvincia, gbc);
        fila++;

        txtCP = new JTextField();
        txtCP.setEditable(false);
        JButton btnEditarCP = new JButton(new ImageIcon("resources/images/lapiz.png"));
        btnEditarCP.setPreferredSize(new Dimension(40, 25));
        btnEditarCP.setFocusPainted(false);
        btnEditarCP.setContentAreaFilled(false);
        btnEditarCP.setBorderPainted(false);

        btnEditarCP.addActionListener(e -> txtCP.setEditable(!txtCP.isEditable()));

        JPanel panelCP = new JPanel(new BorderLayout(5, 0));
        panelCP.add(new JLabel("Código Postal:"), BorderLayout.WEST);
        panelCP.add(txtCP, BorderLayout.CENTER);
        panelCP.add(btnEditarCP, BorderLayout.EAST);

        gbc.gridx = 0;
        gbc.gridy = fila++;
        gbc.gridwidth = 2;
        pDetalles.add(panelCP, gbc);

        JButton btnConfirmar = new JButton("Confirmar");
        btnConfirmar.addActionListener(e -> actualizarDireccion());
        gbc.gridy = fila++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        pDetalles.add(btnConfirmar, gbc);

        pestañas.addTab("Detalles", pDetalles);

        pCentro.add(pestañas, BorderLayout.CENTER);
        add(pCentro, BorderLayout.CENTER);

        cargarProductos();
        cargarDetalles();

        setVisible(true);
        setResizable(false);
    }

    private void inicializarProvincias() {
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

    private void actualizarProvincias() {
        String paisSeleccionado = (String) comboPais.getSelectedItem();
        comboProvincia.removeAllItems();
        if (paisSeleccionado != null && provinciasPorPais.containsKey(paisSeleccionado)) {
            for (String prov : provinciasPorPais.get(paisSeleccionado)) {
                comboProvincia.addItem(prov);
            }
        }
    }

    private void cargarProductos() {
        modeloTabla.setRowCount(0);
        try (Connection con = BaseDatosConfig.initBD("resources/db/MyMerch.db")) {
            String sql = "SELECT dp.cantidad, prod.nombre, prod.descripcion, prod.precio " +
                         "FROM DetallePedido dp " +
                         "JOIN Productos prod ON dp.id_producto = prod.id " +
                         "WHERE dp.id_pedido = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, idPedido);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                String detallesProducto = rs.getString("nombre") + " (" + rs.getString("descripcion") + ")";
                String precioConEuro = rs.getDouble("precio") + " €";
                modeloTabla.addRow(new Object[]{detallesProducto, rs.getInt("cantidad"), precioConEuro});
            }

            rs.close();
            pst.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar productos", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarDetalles() {
        try (Connection con = BaseDatosConfig.initBD("resources/db/MyMerch.db")) {
            String sql = "SELECT direccion, pais, cp, provincia FROM Pedidos WHERE id = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, idPedido);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                txtDireccion.setText(rs.getString("direccion"));
                txtCP.setText(rs.getString("cp"));
                comboPais.setSelectedItem(rs.getString("pais"));
                actualizarProvincias();
                comboProvincia.setSelectedItem(rs.getString("provincia"));
            }

            rs.close();
            pst.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void actualizarDireccion() {
        try (Connection con = BaseDatosConfig.initBD("resources/db/MyMerch.db")) {
            String sql = "UPDATE Pedidos SET direccion=?, cp=?, pais=?, provincia=? WHERE id=?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, txtDireccion.getText());
            pst.setString(2, txtCP.getText());
            pst.setString(3, (String) comboPais.getSelectedItem());
            pst.setString(4, (String) comboProvincia.getSelectedItem());
            pst.setInt(5, idPedido);

            pst.executeUpdate();
            pst.close();

            JOptionPane.showMessageDialog(this, "Dirección actualizada correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            ventanaAnterior.cargarPedidosDesdeBD();
            this.dispose();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al actualizar dirección", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
