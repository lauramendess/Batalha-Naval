import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

public class BotaoMetalico extends JButton {
    private boolean sobreBotao = false;

    public BotaoMetalico(String texto) {
        super(texto);
        setFont(new Font("Arial Black", Font.BOLD, 22));
        setForeground(new Color(20, 50, 80));
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { sobreBotao = true; repaint(); }
            @Override
            public void mouseExited(MouseEvent e) { sobreBotao = false; repaint(); }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        RoundRectangle2D forma = new RoundRectangle2D.Double(2, 2, w - 4, h - 4, 18, 18);

        Color corBase = sobreBotao ? new Color(210, 220, 228) : new Color(180, 190, 198);
        GradientPaint gradiente = new GradientPaint(0, 0, corBase.brighter(), 0, h, corBase.darker());
        g2.setPaint(gradiente);
        g2.fill(forma);

        g2.setStroke(new BasicStroke(3));
        g2.setColor(new Color(30, 70, 110));
        g2.draw(forma);

        g2.setStroke(new BasicStroke(1));
        g2.setColor(new Color(255, 255, 255, 120));
        g2.draw(new RoundRectangle2D.Double(5, 5, w - 10, h - 10, 14, 14));

        g2.dispose();
        super.paintComponent(g);
    }
}
