import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

public class TabuleiroPainel extends JPanel {

    private static final int IMG_ORIGINAL = 1024;

    private static final int GRID_X = 103;
    private static final int GRID_Y = 142;

    private static final int GRID_W = 820;
    private static final int GRID_H = 820;
    private static final double PROPORCAO_IMAGEM = 1.0;

    public interface CliqueCelulaListener {
        void aoClicar(int linha, int coluna);
    }

    private Image imagemFundo;
    private final Tabuleiro tabuleiro;
    private final boolean mostrarNavios;
    private CliqueCelulaListener listenerClique;

    private int previewTamanho = 0;
    private boolean previewHorizontal = true;
    private int previewLinha = -1;
    private int previewColuna = -1;
    private boolean modoPosicionamento = false;

    private double gradeX, gradeY, celulaW, celulaH;

    public TabuleiroPainel(Tabuleiro tabuleiro, boolean mostrarNavios) {
        this.tabuleiro = tabuleiro;
        this.mostrarNavios = mostrarNavios;
        setOpaque(false);
        carregarImagem();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int[] celula = celulaEm(e.getX(), e.getY());
                if (celula != null && listenerClique != null) {
                    listenerClique.aoClicar(celula[0], celula[1]);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                previewLinha = -1;
                previewColuna = -1;
                repaint();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (!modoPosicionamento)
                    return;
                int[] celula = celulaEm(e.getX(), e.getY());
                if (celula != null) {
                    previewLinha = celula[0];
                    previewColuna = celula[1];
                } else {
                    previewLinha = -1;
                    previewColuna = -1;
                }
                repaint();
            }
        });
    }

    private void carregarImagem() {
        try {
            ImageIcon icon = new ImageIcon("lib/tabuleiro.png");

            if (icon.getIconWidth() == -1) {
                throw new Exception("Imagem tabuleiro.png não encontrada.");
            }

            imagemFundo = icon.getImage();

        } catch (Exception e) {
            System.out.println("Erro ao carregar a imagem do tabuleiro.");
            e.printStackTrace();
            imagemFundo = null;
        }
    }

    public void setListenerClique(CliqueCelulaListener listener) {
        this.listenerClique = listener;
    }

    public void ativarPreview(int tamanho, boolean horizontal) {
        this.modoPosicionamento = true;
        this.previewTamanho = tamanho;
        this.previewHorizontal = horizontal;
        repaint();
    }

    public void desativarPreview() {
        this.modoPosicionamento = false;
        previewLinha = -1;
        previewColuna = -1;
        repaint();
    }

    private int[] celulaEm(int x, int y) {
        if (celulaW <= 0 || celulaH <= 0)
            return null;
        double relX = (x - gradeX) / celulaW;
        double relY = (y - gradeY) / celulaH;
        int coluna = (int) Math.floor(relX);
        int linha = (int) Math.floor(relY);
        if (linha < 0 || linha >= Tabuleiro.TAMANHO || coluna < 0 || coluna >= Tabuleiro.TAMANHO) {
            return null;
        }
        return new int[] { linha, coluna };
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        int painelW = getWidth();
        int painelH = getHeight();

        double imgW, imgH;
        if (painelW / (double) painelH > PROPORCAO_IMAGEM) {
            imgH = painelH;
            imgW = imgH * PROPORCAO_IMAGEM;
        } else {
            imgW = painelW;
            imgH = imgW / PROPORCAO_IMAGEM;
        }
        double imgX = (painelW - imgW) / 2.0;
        double imgY = (painelH - imgH) / 2.0;

        if (imagemFundo != null) {
            g2.drawImage(imagemFundo, (int) imgX, (int) imgY, (int) imgW, (int) imgH, this);
        } else {
            g2.setColor(new Color(20, 60, 100));
            g2.fillRoundRect((int) imgX, (int) imgY, (int) imgW, (int) imgH, 12, 12);
        }

        gradeX = imgX + imgW * GRID_X / IMG_ORIGINAL;
        gradeY = imgY + imgH * GRID_Y / IMG_ORIGINAL;

        double gradeW = imgW * GRID_W / IMG_ORIGINAL;
        double gradeH = imgH * GRID_H / IMG_ORIGINAL;
        celulaW = gradeW / Tabuleiro.TAMANHO;
        celulaH = gradeH / Tabuleiro.TAMANHO;

        for (int i = 0; i < Tabuleiro.TAMANHO; i++) {
            for (int j = 0; j < Tabuleiro.TAMANHO; j++) {
                double cx = Math.round(gradeX + j * celulaW);
                double cy = Math.round(gradeY + i * celulaH);
                desenharCelula(g2, cx, cy, tabuleiro.getEstado(i, j));
            }
        }

        if (modoPosicionamento && previewLinha >= 0 && previewColuna >= 0 && previewTamanho > 0) {
            desenharPreview(g2);
        }

        g2.dispose();
    }

    private void desenharCelula(Graphics2D g2, double cx, double cy, Tabuleiro.Estado estado) {
        double margem = Math.min(celulaW, celulaH) * 0.06;
        Rectangle2D area = new Rectangle2D.Double(cx + margem, cy + margem,
                celulaW - margem * 2, celulaH - margem * 2);

        switch (estado) {
            case NAVIO:
                if (mostrarNavios) {
                    RoundRectangle2D navio = new RoundRectangle2D.Double(
                            area.getX(), area.getY(), area.getWidth(), area.getHeight(),
                            area.getWidth() * 0.3, area.getHeight() * 0.3);
                    GradientPaint grad = new GradientPaint(
                            (float) area.getMinX(), (float) area.getMinY(), new Color(150, 158, 165),
                            (float) area.getMaxX(), (float) area.getMaxY(), new Color(90, 98, 106));
                    g2.setPaint(grad);
                    g2.fill(navio);
                    g2.setColor(new Color(50, 56, 62));
                    g2.setStroke(new BasicStroke(Math.max(1f, (float) (celulaW * 0.05))));
                    g2.draw(navio);
                }
                break;
            case ACERTO: {
                Color vermelho = new Color(220, 40, 30);
                Ellipse2D circulo = new Ellipse2D.Double(area.getX(), area.getY(), area.getWidth(), area.getHeight());
                g2.setColor(new Color(0, 0, 0, 70));
                g2.fill(circulo);
                double f = 0.55;
                double fx = cx + celulaW * (1 - f) / 2;
                double fy = cy + celulaH * (1 - f) / 2;
                Ellipse2D nucleo = new Ellipse2D.Double(fx, fy, celulaW * f, celulaH * f);
                g2.setColor(vermelho);
                g2.fill(nucleo);
                g2.setColor(new Color(255, 200, 190));
                g2.setStroke(new BasicStroke(Math.max(1f, (float) (celulaW * 0.05))));
                g2.draw(nucleo);
                break;
            }
            case ERRO: {
                double f = 0.30;
                double fx = cx + celulaW * (1 - f) / 2;
                double fy = cy + celulaH * (1 - f) / 2;
                Ellipse2D ponto = new Ellipse2D.Double(fx, fy, celulaW * f, celulaH * f);
                g2.setColor(new Color(235, 240, 245, 230));
                g2.fill(ponto);
                g2.setColor(new Color(255, 255, 255, 160));
                g2.setStroke(new BasicStroke(1f));
                g2.draw(ponto);
                break;
            }
            case AGUA:
            default:
                break;
        }
    }

    private void desenharPreview(Graphics2D g2) {
        boolean valido = tabuleiro.podePosicionar(previewLinha, previewColuna, previewTamanho, previewHorizontal);
        Color cor = valido ? new Color(60, 220, 100, 150) : new Color(230, 50, 50, 150);

        for (int k = 0; k < previewTamanho; k++) {
            int l = previewHorizontal ? previewLinha : previewLinha + k;
            int c = previewHorizontal ? previewColuna + k : previewColuna;
            if (l < 0 || l >= Tabuleiro.TAMANHO || c < 0 || c >= Tabuleiro.TAMANHO)
                continue;
            double cx = gradeX + c * celulaW;
            double cy = gradeY + l * celulaH;
            g2.setColor(cor);
            g2.fill(new RoundRectangle2D.Double(cx + 1, cy + 1, celulaW - 2, celulaH - 2,
                    celulaW * 0.25, celulaH * 0.25));
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(460, (int) (460 / PROPORCAO_IMAGEM));
    }
}
