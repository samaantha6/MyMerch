package gui;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import db.BaseDatosConfig;
import domain.Usuario;

public class VentanaModificarPedido extends JFrame {

    private static final long serialVersionUID = 1L;
    private Usuario usuario;
    private int idPedido;
    private JTextField txtDireccion, txtPais, txtCP, txtProvincia;
    private JTable tablaProductos;
    private DefaultTableModel modeloTabla;
    private VentanaMisPedidos ventanaAnterior;

    public VentanaModificarPedido(Usuario usuario, int idPedido, VentanaMisPedidos ventanaMisPedidos) {
        this.usuario = usuario;
        this.idPedido = idPedido;
        this.ventanaAnterior = ventanaAnterior;

        setTitle("Modificar Pedido");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // PANEL SUPERIOR: Flecha atrás + título
        JPanel pSuperior = new JPanel(new BorderLayout());
        pSuperior.setBorder(new EmptyBorder(10, 10, 10, 10));

        JButton btnAtras = new JButton("←");
        btnAtras.setFont(new Font("Tahoma", Font.BOLD, 16));
        btnAtras.setFocusPainted(false);
        btnAtras.setBorderPainted(false);
        btnAtras.setContentAreaFilled(false);
        btnAtras.addActionListener(e -> {
            this.dispose(); // cerrar esta ventana
            ventanaAnterior.setVisible(true); // volver a la ventana anterior
        });
        pSuperior.add(btnAtras, BorderLayout.WEST);

        JLabel lblTitulo = new JLabel("Modificar Pedido", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Tahoma", Font.BOLD, 22));
        pSuperior.add(lblTitulo, BorderLayout.CENTER);

        add(pSuperior, BorderLayout.NORTH);

        // PANEL CENTRO: ID y pestañas
        JPanel pCentro = new JPanel(new BorderLayout());
        pCentro.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel lblID = new JLabel("ID: " + idPedido);
        lblID.setFont(new Font("Tahoma", Font.PLAIN, 16));
        pCentro.add(lblID, BorderLayout.NORTH);

        // PESTAÑAS
        JTabbedPane pestañas = new JTabbedPane();

        // --- PESTAÑA 1: Productos ---
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

        // --- PESTAÑA 2: Detalles ---
        JPanel pDetalles = new JPanel();
        pDetalles.setLayout(new BoxLayout(pDetalles, BoxLayout.Y_AXIS));
        pDetalles.setBorder(new EmptyBorder(10, 50, 10, 50));

        txtDireccion = crearCampoEditable("Dirección", "direccion");
        txtPais = crearCampoEditable("País", "pais");
        txtCP = crearCampoEditable("Código Postal", "cp");
        txtProvincia = crearCampoEditable("Provincia", "provincia");

        pDetalles.add(Box.createVerticalGlue());
        JButton btnConfirmar = new JButton("Confirmar");
        btnConfirmar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnConfirmar.addActionListener(e -> actualizarDireccion());

        pDetalles.add(Box.createRigidArea(new Dimension(0, 20)));
        pDetalles.add(btnConfirmar);
        pDetalles.add(Box.createVerticalGlue());

        pestañas.addTab("Detalles", pDetalles);

        pCentro.add(pestañas, BorderLayout.CENTER);

        add(pCentro, BorderLayout.CENTER);

        // Cargar datos desde BD
        cargarProductos();
        cargarDetalles();

        setVisible(true);
        setResizable(false);
    }

    // Método para crear campos de detalles con lápiz para modificar
    private JTextField crearCampoEditable(String labelTexto, String nombreCampo) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        JLabel lbl = new JLabel(labelTexto + ":");
        JTextField txt = new JTextField();
        txt.setEditable(false);

        JButton btnEditar = new JButton("✎"); // icono lápiz simple
        btnEditar.setPreferredSize(new Dimension(40, 25));
        btnEditar.setFocusPainted(false);
        btnEditar.addActionListener(e -> txt.setEditable(true));

        panel.add(lbl, BorderLayout.WEST);
        panel.add(txt, BorderLayout.CENTER);
        panel.add(btnEditar, BorderLayout.EAST);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        getContentPane().add(panel);
        JPanel contenedor = new JPanel();
        contenedor.setLayout(new BoxLayout(contenedor, BoxLayout.Y_AXIS));
        contenedor.add(panel);

        // Añadir al panel de detalles
        ((JPanel) getContentPane().getComponent(1)).add(panel);

        return txt;
    }

    // Cargar productos del pedido
    private void cargarProductos() {
        modeloTabla.setRowCount(0);
        try (Connection con = BaseDatosConfig.initBD("resources/db/MyMerch.db")) {
            String sql = "SELECT dp.cantidad, prod.nombre, prod.precio " +
                         "FROM DetallePedido dp " +
                         "JOIN Productos prod ON dp.id_producto = prod.id " +
                         "WHERE dp.id_pedido = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, idPedido);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                modeloTabla.addRow(new Object[]{
                        rs.getString("nombre"),
                        rs.getInt("cantidad"),
                        rs.getDouble("precio")
                });
            }

            rs.close();
            pst.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar productos", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Cargar detalles de dirección
    private void cargarDetalles() {
        try (Connection con = BaseDatosConfig.initBD("resources/db/MyMerch.db")) {
            String sql = "SELECT direccion, pais, cp, provincia FROM Pedidos WHERE id = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, idPedido);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                txtDireccion.setText(rs.getString("direccion"));
                txtPais.setText(rs.getString("pais"));
                txtCP.setText(rs.getString("cp"));
                txtProvincia.setText(rs.getString("provincia"));
            }

            rs.close();
            pst.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // Guardar cambios de dirección en BD y actualizar tabla de MisPedidos
    private void actualizarDireccion() {
        try (Connection con = BaseDatosConfig.initBD("resources/db/MyMerch.db")) {
            String sql = "UPDATE Pedidos SET direccion=?, pais=?, cp=?, provincia=? WHERE id=?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, txtDireccion.getText());
            pst.setString(2, txtPais.getText());
            pst.setString(3, txtCP.getText());
            pst.setString(4, txtProvincia.getText());
            pst.setInt(5, idPedido);

            pst.executeUpdate();
            pst.close();

            JOptionPane.showMessageDialog(this, "Dirección actualizada correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);

            // Actualizar tabla en ventana anterior
            ventanaAnterior.cargarPedidosDesdeBD();

            this.dispose();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al actualizar dirección", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
