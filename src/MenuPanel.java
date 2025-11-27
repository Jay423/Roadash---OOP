import java.awt.*;
import java.io.InputStream;
import javax.swing.*;

public class MenuPanel extends JPanel {

    private GameFrame frame;
    private MusicManager music; // Music manager reference
    private static int highestScore = 0;

    private JButton playBtn;
    private JButton exitBtn;

    private Image bgImage; // Background image

    private Font customFont;

    // Title image fields
    private Image titleImage;
    private int titleWidth = 550;  // scale width (adjust as you like)
    private int titleHeight = 200; // scale height (adjust as you like)

    public MenuPanel(GameFrame frame, MusicManager music) {
        this.frame = frame;
        this.music = music;

        setLayout(null);

        loadCustomFont();

        // Load the background image
        ImageIcon icon = new ImageIcon(getClass().getResource("/assets/menu-bg.png"));
        bgImage = icon.getImage();

        // Load + SCALE title image ONCE
        ImageIcon titleIcon = new ImageIcon(getClass().getResource("/assets/title.png"));
        titleImage = titleIcon.getImage().getScaledInstance(titleWidth, titleHeight, Image.SCALE_SMOOTH);

        // Start menu music
        this.music.stopMusic();
        this.music.playMusic("menu-music.wav", true);

        // Create Play button
        ImageIcon playIcon = new ImageIcon(getClass().getResource("/assets/play_button.png"));
        Image scaledPlayImg = playIcon.getImage().getScaledInstance(200, 90, Image.SCALE_SMOOTH);

        ImageIcon exitIcon = new ImageIcon(getClass().getResource("/assets/exit.png"));
        Image scaledExitImg = exitIcon.getImage().getScaledInstance(200, 90, Image.SCALE_SMOOTH);

        playBtn = new JButton(new ImageIcon(scaledPlayImg));
        exitBtn = new JButton(new ImageIcon(scaledExitImg));

        playBtn.setBorderPainted(false);
        playBtn.setContentAreaFilled(false);
        playBtn.setFocusPainted(false);
        playBtn.setOpaque(false);
        playBtn.setSize(200, 60);
        exitBtn.setBorderPainted(false);
        exitBtn.setContentAreaFilled(false);
        exitBtn.setFocusPainted(false);
        exitBtn.setOpaque(false);
        exitBtn.setSize(200, 60);

        add(playBtn);
        add(exitBtn);

        // Button actions
        playBtn.addActionListener(e -> {
            music.playSFX("click.wav");
            startGame();
        });

        exitBtn.addActionListener(e -> {
            music.playSFX("click.wav");
            System.exit(0);
        });

        // Recenter components when resized
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                centerComponents();
                repaint();
            }
        });

        SwingUtilities.invokeLater(this::centerComponents);
    }

    private void centerComponents() {
        int panelWidth = getWidth();
        int panelHeight = getHeight();

        int spacing = 10;
        int startY = (panelHeight / 2) + 50;

        playBtn.setLocation((panelWidth - playBtn.getWidth()) / 2, startY);
        exitBtn.setLocation((panelWidth - exitBtn.getWidth()) / 2, startY + playBtn.getHeight() + spacing);
    }

    private void startGame() {
        music.stopMusic();
        music.playMusic("game-music.wav", true);
        music.setVolume(-10.0f); // lower volume

        frame.setContentPane(new GamePanel(frame, music));
        frame.revalidate();

        // ⭐ FIX: Give GamePanel keyboard focus again
        SwingUtilities.invokeLater(() -> {
            frame.getContentPane().setFocusable(true);
            frame.getContentPane().requestFocusInWindow();
        });
    }

    public static void updateHighscore(int score) {
        if (score > highestScore) highestScore = score;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw background image
        if (bgImage != null) {
            g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
        }

        int panelWidth = getWidth();

        // Draw centered title image
        int titleX = (panelWidth - titleWidth) / 2;
        int titleY = 20;
        g.drawImage(titleImage, titleX, titleY, this);

        // Highest score text
        if (customFont != null) {
            g.setFont(customFont);
        } else {
            g.setFont(new Font("Arial", Font.PLAIN, 24));
        }
        g.setColor(Color.WHITE);
        String scoreText = "Highest Score: " + highestScore;
        int scoreWidth = g.getFontMetrics().stringWidth(scoreText);

        g.drawString(scoreText, (panelWidth - scoreWidth) / 2, 225);
    }

    private void loadCustomFont() {
        try (InputStream is = getClass().getResourceAsStream("/assets/font/Jersey10-Regular.ttf")) {
            if (is == null) {
                System.err.println("Font resource not found!");
                customFont = new Font("Arial", Font.PLAIN, 24);
                return;
            }
            customFont = Font.createFont(Font.TRUETYPE_FONT, is);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(customFont);
            customFont = customFont.deriveFont(Font.PLAIN, 24f);  // set initial size
        } catch (Exception e) {
            e.printStackTrace();
            // fallback to Arial if loading fails
            customFont = new Font("Arial", Font.PLAIN, 24);
        }
    }
    
}
