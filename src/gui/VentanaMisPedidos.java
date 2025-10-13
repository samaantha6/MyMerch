package gui;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.*;

import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import domain.Usuario;
import db.BaseDatosConfig;

public class VentanaMisPedidos extends JFrame {

    private static final long serialVersionUID = 1L;
    private Usuario usuario;
    private JTable tablaPedidos;
    private DefaultTableModel modeloTabla;
    private JComboBox<Integer> comboPedidos;

    public VentanaMisPedidos(Usuario usuario) {
        System.out.println("ID de usuario: " + usuario.getId());
        this.usuario = usuario;

        setTitle("Mis Pedidos");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel pSuperior = new JPanel(new BorderLayout());
        pSuperior.setBorder(new EmptyBorder(10, 10, 10, 10));

        ImageIcon iconLogo = new ImageIcon("resources/images/logo.png");
        Image imgLogo = iconLogo.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
        JLabel lblLogo = new JLabel(new ImageIcon(imgLogo), SwingConstants.CENTER);
        pSuperior.add(lblLogo, BorderLayout.CENTER);

        JPanel pIzq = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        ImageIcon iconUser = new ImageIcon("resources/images/user.png");
        Image imgUser = iconUser.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        JLabel lblUser = new JLabel(new ImageIcon(imgUser));
        JLabel lblPedidos = new JLabel("Pedidos");
        lblPedidos.setFont(new Font("Tahoma", Font.BOLD, 18));
        pIzq.add(lblUser);
        pIzq.add(lblPedidos);
        pSuperior.add(pIzq, BorderLayout.WEST);

        ImageIcon iconCasa = new ImageIcon("resources/images/casa.png");
        Image imgCasa = iconCasa.getImage().getScaledInstance(35, 35, Image.SCALE_SMOOTH);
        JButton btnHome = new JButton(new ImageIcon(imgCasa));
        btnHome.setContentAreaFilled(false);
        btnHome.setBorderPainted(false);
        btnHome.setFocusPainted(false);
        btnHome.addActionListener(e -> {
            new VentanaCatalogo(usuario);
            dispose();
        });
        pSuperior.add(btnHome, BorderLayout.EAST);
        add(pSuperior, BorderLayout.NORTH);

        // Tabla
        String[] columnas = {"ID Pedido", "Detalles", "Fecha", "Dirección", "Estado", "Acciones"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Solo editable la columna "Acciones"
                return getColumnName(column).equals("Acciones");
            }
        };

        tablaPedidos = new JTable(modeloTabla);
        tablaPedidos.setRowHeight(40);

        tablaPedidos.getColumn("Acciones").setCellRenderer(new AccionRenderer());
        tablaPedidos.getColumn("Acciones").setCellEditor(new AccionEditor());

        JScrollPane scroll = new JScrollPane(tablaPedidos);
        add(scroll, BorderLayout.CENTER);

        JPanel pInferior = new JPanel();
        pInferior.setLayout(new BoxLayout(pInferior, BoxLayout.Y_AXIS));
        pInferior.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel lblExport = new JLabel("Exportar factura de envío a PDF:");
        lblExport.setAlignmentX(Component.CENTER_ALIGNMENT);

        comboPedidos = new JComboBox<>();
        comboPedidos.setMaximumSize(new Dimension(200, 25));
        comboPedidos.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnExportar = new JButton("Exportar");
        btnExportar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnExportar.addActionListener(e -> exportarPDF());

        pInferior.add(lblExport);
        pInferior.add(Box.createRigidArea(new Dimension(0, 5)));
        pInferior.add(comboPedidos);
        pInferior.add(Box.createRigidArea(new Dimension(0, 5)));
        pInferior.add(btnExportar);

        add(pInferior, BorderLayout.SOUTH);

        cargarPedidosDesdeBD();

        setVisible(true);
        setResizable(false);
    }

    protected void cargarPedidosDesdeBD() {
        modeloTabla.setRowCount(0);
        comboPedidos.removeAllItems();

        try (Connection con = BaseDatosConfig.initBD("resources/db/MyMerch.db")) {
            String sqlPedidos = "SELECT * FROM Pedidos WHERE id_usuario = ? ORDER BY id";
            PreparedStatement pstPedidos = con.prepareStatement(sqlPedidos);
            pstPedidos.setInt(1, usuario.getId());
            ResultSet rsPedidos = pstPedidos.executeQuery();

            while (rsPedidos.next()) {
                int idPedido = rsPedidos.getInt("id");
                String fecha = rsPedidos.getString("fecha");
                String estado = rsPedidos.getString("estado");
                String direccion = rsPedidos.getString("direccion");

                String sqlDetalles = "SELECT dp.cantidad, prod.nombre, prod.descripcion, prod.precio " +
                                     "FROM DetallePedido dp " +
                                     "JOIN Productos prod ON dp.id_producto = prod.id " +
                                     "WHERE dp.id_pedido = ?";
                
                PreparedStatement pstDet = con.prepareStatement(sqlDetalles);
                pstDet.setInt(1, idPedido);
                ResultSet rsDet = pstDet.executeQuery();

                StringBuilder detalles = new StringBuilder();
                while (rsDet.next()) {
                    detalles.append(rsDet.getInt("cantidad"))
                            .append(" x ")
                            .append(rsDet.getString("nombre"))
                            .append(" (")
                            .append(rsDet.getString("descripcion"))
                            .append(") - ")
                            .append(rsDet.getDouble("precio"))
                            .append("€\n");
                }
                rsDet.close();
                pstDet.close();

                if (detalles.length() == 0) detalles.append("Sin productos");

                Object[] fila = {idPedido, detalles.toString(), fecha, direccion, estado, ""};
                modeloTabla.addRow(fila);
                comboPedidos.addItem(idPedido);
            }
            rsPedidos.close();
            pstPedidos.close();

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar pedidos desde la base de datos", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportarPDF() {
        Integer idPedido = (Integer) comboPedidos.getSelectedItem();
        if (idPedido == null) {
            JOptionPane.showMessageDialog(this, "Selecciona un pedido", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JFileChooser guardar = new JFileChooser();
        guardar.setDialogTitle("Guardar como");
        FileNameExtensionFilter filtro = new FileNameExtensionFilter("Archivos PDF (*.pdf)", "pdf");
        guardar.setFileFilter(filtro);
        int guardarComo = guardar.showSaveDialog(this);
        if (guardarComo != JFileChooser.APPROVE_OPTION) return;

        String fpath = guardar.getSelectedFile().getAbsolutePath();
        if (!fpath.toLowerCase().endsWith(".pdf")) fpath += ".pdf";

        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage();
            doc.addPage(page);

            PDPageContentStream content = new PDPageContentStream(doc, page);
            PDFont fuenteTitulo = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
            PDFont fuenteNormal = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

            float margin = 50;
            float y = 750;

            InputStream imagen = getClass().getClassLoader().getResourceAsStream("resources/images/logo.png");
            if (imagen != null) {
                PDImageXObject logo = PDImageXObject.createFromByteArray(doc, IOUtils.toByteArray(imagen), "logo");
                float escala = 0.3f;
                content.drawImage(logo, 450, y - 50, logo.getWidth() * escala, logo.getHeight() * escala);
                imagen.close();
            }

            content.beginText();
            content.setFont(fuenteTitulo, 18);
            content.newLineAtOffset(margin, y);
            content.showText("Factura de Pedido #" + idPedido);
            content.endText();
            y -= 40;

            content.beginText();
            content.setFont(fuenteTitulo, 12);
            content.newLineAtOffset(margin, y);
            content.showText(String.format("%-30s %-10s %-10s %-10s", "Producto", "Cantidad", "Precio", "Subtotal"));
            content.endText();
            y -= 20;

            double subtotalProductos = 0;

            try (Connection con = BaseDatosConfig.initBD("resources/db/MyMerch.db")) {
                String sql = "SELECT dp.cantidad, prod.nombre, prod.precio " +
                             "FROM DetallePedido dp JOIN Productos prod ON dp.id_producto = prod.id " +
                             "WHERE dp.id_pedido = ?";
                PreparedStatement pst = con.prepareStatement(sql);
                pst.setInt(1, idPedido);
                ResultSet rs = pst.executeQuery();

                content.setFont(fuenteNormal, 12);
                while (rs.next()) {
                    String nombre = rs.getString("nombre");
                    int cantidad = rs.getInt("cantidad");
                    double precioUnitario = rs.getDouble("precio");
                    double subtotal = cantidad * precioUnitario;
                    subtotalProductos += subtotal;

                    content.beginText();
                    content.newLineAtOffset(margin, y);
                    content.showText(String.format("%-30s %-10d %-10.2f %-10.2f", nombre, cantidad, precioUnitario, subtotal));
                    content.endText();
                    y -= 20;
                }
                rs.close();
                pst.close();

                double envio = 7.0;
                y -= 10;
                content.beginText();
                content.setFont(fuenteNormal, 12);
                content.newLineAtOffset(margin, y);
                content.showText(String.format("%-30s %-10s %-10s %-10.2f", "Envío express", "-", "-", envio));
                content.endText();

                double totalConEnvio = subtotalProductos + envio;

                String sqlTotal = "SELECT total FROM Pedidos WHERE id = ?";
                PreparedStatement pstTotal = con.prepareStatement(sqlTotal);
                pstTotal.setInt(1, idPedido);
                ResultSet rsTotal = pstTotal.executeQuery();
                double totalReal = rsTotal.next() ? rsTotal.getDouble("total") : totalConEnvio;
                rsTotal.close();
                pstTotal.close();

                y -= 30;
                content.beginText();
                content.setFont(fuenteTitulo, 14);
                content.newLineAtOffset(margin, y);
                if (totalReal < totalConEnvio) {
                    content.showText(String.format("TOTAL: %.2f € (precio con descuento)", totalReal));
                } else {
                    content.showText(String.format("TOTAL: %.2f €", totalReal));
                }
                content.endText();

            } catch (SQLException e) {
                e.printStackTrace();
            }

            content.close();
            doc.save(fpath);
            JOptionPane.showMessageDialog(this, "PDF generado correctamente: " + fpath, "Éxito", JOptionPane.INFORMATION_MESSAGE);

        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al generar PDF", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    class AccionRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {

            JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
            String estado = (String) table.getValueAt(row, 4);
            if ("pendiente".equalsIgnoreCase(estado)) {
                JButton btnCancelar = new JButton(new ImageIcon("resources/images/x.png"));
                btnCancelar.setPreferredSize(new Dimension(25, 25));

                JButton btnEditar = new JButton(new ImageIcon("resources/images/lapiz.png"));
                btnEditar.setPreferredSize(new Dimension(25, 25));

                panel.add(btnCancelar);
                panel.add(btnEditar);
            }
            return panel;
        }
    }

    class AccionEditor extends AbstractCellEditor implements TableCellEditor {
        private JPanel panel;
        private JButton btnCancelar;
        private JButton btnEditar;

        public AccionEditor() {
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));

            btnCancelar = new JButton(new ImageIcon("resources/images/x.png"));
            btnCancelar.setPreferredSize(new Dimension(25, 25));
            btnCancelar.addActionListener(e -> {
                JTable table = (JTable) SwingUtilities.getAncestorOfClass(JTable.class, panel);
                if (table != null) {
                    int row = table.getEditingRow();
                    if (row != -1) {
                        int opcion = JOptionPane.showConfirmDialog(
                                VentanaMisPedidos.this,
                                "¿Está seguro de que desea cancelar su pedido?",
                                "Confirmar Cancelación",
                                JOptionPane.YES_NO_OPTION
                        );
                        if (opcion == JOptionPane.YES_OPTION) {
                            int idPedido = (Integer) table.getValueAt(row, 0);
                            cancelarPedido(idPedido);
                            table.setValueAt("Cancelado", row, 4); // actualizar tabla visual
                        }
                        fireEditingStopped();
                    }
                }
            });

            btnEditar = new JButton(new ImageIcon("resources/images/lapiz.png"));
            btnEditar.setPreferredSize(new Dimension(25, 25));
            btnEditar.addActionListener(e -> {
                JTable table = (JTable) SwingUtilities.getAncestorOfClass(JTable.class, panel);
                if (table != null) {
                    int row = table.getEditingRow();
                    if (row != -1) {
                        int idPedido = (Integer) table.getValueAt(row, 0);
                        new VentanaModificarPedido(usuario, idPedido, VentanaMisPedidos.this);
                        fireEditingStopped();
                    }
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            panel.removeAll();
            String estado = (String) table.getValueAt(row, 4);

            if ("Pendiente".equalsIgnoreCase(estado)) {
                panel.add(btnCancelar);
                panel.add(btnEditar);
            }

            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return null;
        }

        private void cancelarPedido(int idPedido) {
            try (Connection con = BaseDatosConfig.initBD("resources/db/MyMerch.db")) {
                String sql = "UPDATE Pedidos SET estado = ? WHERE id = ?";
                PreparedStatement pst = con.prepareStatement(sql);
                pst.setString(1, "Cancelado");
                pst.setInt(2, idPedido);
                pst.executeUpdate();
                pst.close();
                JOptionPane.showMessageDialog(VentanaMisPedidos.this,
                        "El pedido ha sido cancelado correctamente",
                        "Pedido Cancelado",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(VentanaMisPedidos.this,
                        "Error al cancelar el pedido",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

}
