import java.awt.*;
import java.io.InputStream;
import javax.swing.*;

public class GameOverPanel extends JPanel {

    private GameFrame frame;
    private MusicManager music;
    private int finalScore;
    private int highScore;

    private JButton playAgainBtn;
    private JButton exitBtn;

    private Image bgImage;
    private Font customFont;
    private Image chickenImage;

    // Using text-based title instead of image

    public GameOverPanel(GameFrame frame, MusicManager music, int score, int highScore) {
        this.frame = frame;
        this.music = music;
        this.finalScore = score;
        this.highScore = highScore;

        setLayout(null);
        loadCustomFont();

        // Load the background image (same as menu)
        ImageIcon icon = new ImageIcon(getClass().getResource("/assets/menu-bg.png"));
        bgImage = icon.getImage();

        // Load chicken1.png image
        ImageIcon chickenIcon = new ImageIcon(getClass().getResource("/assets/chicken1.png"));
        chickenImage = chickenIcon.getImage().getScaledInstance(80, 80, Image.SCALE_FAST);

        // Create "GAME OVER" title image or text
        // For now we'll use text, but you could create a game-over image
        
        // Create Play Again button
        ImageIcon playIcon = new ImageIcon(getClass().getResource("/assets/play_button.png"));
        Image scaledPlayImg = playIcon.getImage().getScaledInstance(200, 90, Image.SCALE_SMOOTH);

        ImageIcon exitIcon = new ImageIcon(getClass().getResource("/assets/exit.png"));
        Image scaledExitImg = exitIcon.getImage().getScaledInstance(200, 90, Image.SCALE_SMOOTH);

        playAgainBtn = new JButton(new ImageIcon(scaledPlayImg));
        exitBtn = new JButton(new ImageIcon(scaledExitImg));

        // Style buttons same as menu
        playAgainBtn.setBorderPainted(false);
        playAgainBtn.setContentAreaFilled(false);
        playAgainBtn.setFocusPainted(false);
        playAgainBtn.setOpaque(false);
        playAgainBtn.setSize(200, 60);
        
        exitBtn.setBorderPainted(false);
        exitBtn.setContentAreaFilled(false);
        exitBtn.setFocusPainted(false);
        exitBtn.setOpaque(false);
        exitBtn.setSize(200, 60);

        add(playAgainBtn);
        add(exitBtn);

        // Button actions
        playAgainBtn.addActionListener(e -> {
            music.playSFX("click.wav");
            startNewGame();
        });

        exitBtn.addActionListener(e -> {
            music.playSFX("click.wav");
            returnToMenu();
        });

        // Center components when resized
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
        int startY = (panelHeight / 2) + 100;

        playAgainBtn.setLocation((panelWidth - playAgainBtn.getWidth()) / 2, startY);
        exitBtn.setLocation((panelWidth - exitBtn.getWidth()) / 2, startY + playAgainBtn.getHeight() + spacing);
    }

    private void startNewGame() {
        music.stopMusic();
        music.playMusic("game-music.wav", true);
        music.setVolume(-10.0f);

        frame.setContentPane(new GamePanel(frame, music));
        frame.revalidate();

        SwingUtilities.invokeLater(() -> {
            frame.getContentPane().setFocusable(true);
            frame.getContentPane().requestFocusInWindow();
        });
    }

    private void returnToMenu() {
        MenuPanel menuPanel = new MenuPanel(frame, music);
        menuPanel.resetChicken(); // Reset chicken to starting position
        frame.setContentPane(menuPanel);
        frame.revalidate();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw background image
        if (bgImage != null) {
            g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
        }

        int panelWidth = getWidth();

        // Set font
        if (customFont != null) {
            g.setFont(customFont);
        } else {
            g.setFont(new Font("Arial", Font.PLAIN, 36));
        }

        // Draw "GAME OVER" title
        g.setColor(Color.RED);
        g.setFont(g.getFont().deriveFont(Font.BOLD, 48f));
        String gameOverText = "GAME OVER";
        int gameOverWidth = g.getFontMetrics().stringWidth(gameOverText);
        g.drawString(gameOverText, (panelWidth - gameOverWidth) / 2, 120);

        // Draw final score
        g.setColor(Color.WHITE);
        g.setFont(g.getFont().deriveFont(Font.BOLD, 32f));
        String scoreText = "Final Score: " + finalScore;
        int scoreWidth = g.getFontMetrics().stringWidth(scoreText);
        g.drawString(scoreText, (panelWidth - scoreWidth) / 2, 200);

        // Draw high score
        g.setFont(g.getFont().deriveFont(Font.BOLD, 24f));
        String highScoreText = "High Score: " + highScore;
        int highScoreWidth = g.getFontMetrics().stringWidth(highScoreText);
        g.drawString(highScoreText, (panelWidth - highScoreWidth) / 2, 240);

        // Draw "NEW RECORD!" if applicable
        if (finalScore >= highScore && finalScore > 0) {
            g.setColor(Color.YELLOW);
            g.setFont(g.getFont().deriveFont(Font.BOLD, 28f));
            String newRecordText = "NEW RECORD!";
            int newRecordWidth = g.getFontMetrics().stringWidth(newRecordText);
            g.drawString(newRecordText, (panelWidth - newRecordWidth) / 2, 280);
        }

        // Draw chicken1.png above the play button
        if (chickenImage != null) {
            int chickenX = (panelWidth - 80) / 2; // Center horizontally
            int chickenY = 320; // Position above the play button
            g.drawImage(chickenImage, chickenX, chickenY, this);
        }
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
            customFont = customFont.deriveFont(Font.PLAIN, 24f);
        } catch (Exception e) {
            e.printStackTrace();
            customFont = new Font("Arial", Font.PLAIN, 24);
        }
    }
}