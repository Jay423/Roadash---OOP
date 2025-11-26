import java.awt.*;
import javax.swing.*;

public class MenuPanel extends JPanel {

    private GameFrame frame;
    private MusicManager music; // Music manager reference
    private static int highestScore = 0;

    private JButton playBtn;
    private JButton exitBtn;

    private Image bgImage; // Background image

    public MenuPanel(GameFrame frame, MusicManager music) {
        this.frame = frame;
        this.music = music;

        setLayout(null);

        // Load the background image from assets
        ImageIcon icon = new ImageIcon(getClass().getResource("/assets/menu-bg.png"));
        bgImage = icon.getImage();

        // Start menu music
        this.music.stopMusic(); // stop any previous music
        this.music.playMusic("menu-music.wav", true); // loop menu music

        // Create buttons
        playBtn = new JButton("Play Game");
        exitBtn = new JButton("Exit Game");

        playBtn.setSize(200, 50);
        exitBtn.setSize(200, 50);

        add(playBtn);
        add(exitBtn);

        // Button actions
        playBtn.addActionListener(e -> startGame());
        exitBtn.addActionListener(e -> System.exit(0));

        // Recenter everything if panel size changes
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                centerComponents();
                repaint(); // redraw title, score, and background
            }
        });

        // Initial centering
        SwingUtilities.invokeLater(this::centerComponents);
    }

    private void centerComponents() {
        int panelWidth = getWidth();
        int panelHeight = getHeight();

        int spacing = 20;
        int totalHeight = playBtn.getHeight() + spacing + exitBtn.getHeight();
        int startY = (panelHeight / 2) + 50; // slightly below center for title

        playBtn.setLocation((panelWidth - playBtn.getWidth()) / 2, startY);
        exitBtn.setLocation((panelWidth - exitBtn.getWidth()) / 2, startY + playBtn.getHeight() + spacing);
    }

    private void startGame() {
        // Stop menu music and start game music
        music.stopMusic();
        music.playMusic("game-music.wav", true);
  // Lower game music volume
    music.setVolume(-10.0f); // adjust value to your liking, negative = quieter

        frame.setContentPane(new GamePanel(frame, music)); // Pass music to game panel
        frame.revalidate();
        SwingUtilities.invokeLater(() -> frame.getContentPane().requestFocusInWindow());
    }

    public static void updateHighscore(int score) {
        if (score > highestScore) highestScore = score;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw background image, scaled to panel size
        if (bgImage != null) {
            g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
        }

        int panelWidth = getWidth();

        // Draw centered game title
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 36));
        String title = "Bern Biot";
        int titleWidth = g.getFontMetrics().stringWidth(title);
        g.drawString(title, (panelWidth - titleWidth) / 2, 150);

        // Draw centered highest score
        g.setFont(new Font("Arial", Font.PLAIN, 24));
        String scoreText = "Highest Score: " + highestScore;
        int scoreWidth = g.getFontMetrics().stringWidth(scoreText);
        g.drawString(scoreText, (panelWidth - scoreWidth) / 2, 200);
    }
}
