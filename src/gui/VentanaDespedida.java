package gui;

import java.awt.*;
import javax.swing.*;

public class VentanaDespedida extends JFrame {

    private String texto = "¡Hasta la próxima!";
    private StringBuilder textoActual = new StringBuilder();
    private JLabel lblTexto;
    private JLabel lblEmoji;

    public VentanaDespedida() {
        setTitle("¡Hasta la próxima!");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                GradientPaint grad = new GradientPaint(0, 0, new Color(135, 206, 250),
                        0, getHeight(), new Color(70, 130, 180));
                g2.setPaint(grad);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panel.setLayout(new GridBagLayout());
        add(panel, BorderLayout.CENTER);

        lblEmoji = new JLabel("🕺");
        lblEmoji.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 60));
        panel.add(lblEmoji, new GridBagConstraints());

        lblTexto = new JLabel("");
        lblTexto.setFont(new Font("Arial", Font.BOLD, 36));
        lblTexto.setForeground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 1;
        panel.add(lblTexto, gbc);

        setResizable(false);
        setVisible(true);

        mostrarLetraRecursivo(0);
    }

	//IAG ChatGPT

    private void mostrarLetraRecursivo(int index) {
        if (index < texto.length()) {
            textoActual.append(texto.charAt(index));
            lblTexto.setText(textoActual.toString());

            lblTexto.setForeground(new Color(
                (int)(Math.random()*256),
                (int)(Math.random()*256),
                (int)(Math.random()*256)
            ));

            lblEmoji.setLocation(lblEmoji.getX(), 50 + (int)(Math.sin(index * 0.5) * 20));

            new javax.swing.Timer(150, e -> {
                ((javax.swing.Timer)e.getSource()).stop();
                mostrarLetraRecursivo(index + 1);
            }).start();
        } else {
            new javax.swing.Timer(1000, e -> {
                ((javax.swing.Timer)e.getSource()).stop();
                dispose();
            }).start();
        }
    }

    public static void mostrarDespedida() {
        SwingUtilities.invokeLater(VentanaDespedida::new);
    }
}
