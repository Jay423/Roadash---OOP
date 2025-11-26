import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class GamePanel extends JPanel implements ActionListener, KeyListener {

    private GameFrame frame;
    private MusicManager music; // Added field
    private Timer timer;
    private Player player;
    private ArrayList<Car> cars;
    private Random rand;

    private boolean gameOver = false;
    private int score = 0;

    private boolean movingLeft = false;
    private boolean movingRight = false;
    private boolean movingUp = false;
    private boolean movingDown = false;

    private boolean paused = false;
    private JButton resumeBtn;
    private JButton exitBtn;

    private int numLanes = 4;
    private int laneWidth;

    // Lane scroll
    private int laneScrollOffset = 0;

    public GamePanel(GameFrame frame, MusicManager music) { // Constructor updated
        this.frame = frame;
        this.music = music;

        setFocusable(true);
        setLayout(null);
        addKeyListener(this);

        cars = new ArrayList<>();
        rand = new Random();

        timer = new Timer(16, this); // ~60 FPS
        timer.start();

        // Pause buttons
        resumeBtn = new JButton("Resume");
        exitBtn = new JButton("Exit Game");
        resumeBtn.setFocusable(false);
        exitBtn.setFocusable(false);
        resumeBtn.setVisible(false);
        exitBtn.setVisible(false);

        resumeBtn.addActionListener(e -> {
            music.playSFX("click.wav");
            resumeGame();
        });
        exitBtn.addActionListener(e -> {
            music.playSFX("click.wav");
            frame.setContentPane(new MenuPanel(frame, music)); // Pass music
            frame.revalidate();
        });

        add(resumeBtn);
        add(exitBtn);

        SwingUtilities.invokeLater(() -> requestFocusInWindow());

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                initPlayer();
                removeComponentListener(this);
            }
        });
    }

    private void initPlayer() {
        laneWidth = getWidth() / numLanes;
        int playerX = (getWidth() - 50) / 2;
        int playerY = getHeight() - 120;
        player = new Player(playerX, playerY);
    }

    private void pauseGame() {
        paused = true;
        timer.stop();
        repaint(); 

        resumeBtn.setVisible(true);
        exitBtn.setVisible(true);

        int btnWidth = 200, btnHeight = 50;
        int centerX = (getWidth() - btnWidth) / 2;
        resumeBtn.setBounds(centerX, getHeight() / 2 - 60, btnWidth, btnHeight);
        exitBtn.setBounds(centerX, getHeight() / 2 + 10, btnWidth, btnHeight);
    }

    private void resumeGame() {
        paused = false;
        timer.start();
        repaint(); 
        resumeBtn.setVisible(false);
        exitBtn.setVisible(false);
        SwingUtilities.invokeLater(() -> requestFocusInWindow());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver && !paused) updateGame();
        repaint();
    }

    private void updateGame() {
        score++;

        laneScrollOffset += 8;
        if (laneScrollOffset > 40) laneScrollOffset = 0;

        if (movingLeft) player.x -= 8;
        if (movingRight) player.x += 8;
        if (movingUp) player.y -= 8;
        if (movingDown) player.y += 8;

        player.x = Math.max(0, Math.min(player.x, getWidth() - 50));
        player.y = Math.max(0, Math.min(player.y, getHeight() - 80));

        // Spawn cars randomly
        if (rand.nextInt(20) == 0) {
            int carWidth = 60;
            int carHeight = 120;
            int newX;
            boolean overlap;

            do {
                overlap = false;
                newX = rand.nextInt(getWidth() - carWidth);

                for (Car c : cars) {
                    if (c.y < carHeight + 20) {
                        if (Math.abs(c.x - newX) < carWidth + 10) {
                            overlap = true;
                            break;
                        }
                    }
                }
            } while (overlap);

            cars.add(new Car(newX, -carHeight));
        }

        for (Car c : cars) {
            c.y += 8;

            if (player.getBounds().intersects(c.getBounds())) {
                music.playSoundEffect("chicken-soundeffect.wav");
                gameOver = true;
                timer.stop();
                MenuPanel.updateHighscore(score);
                showGameOverScreen();
            }
        }

        cars.removeIf(c -> c.y > getHeight() + 200);
    }

    private void showGameOverScreen() {
        int choice = JOptionPane.showOptionDialog(
                this,
                "Game Over! Score: " + score,
                "Game Over",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                new String[]{"Play Again", "Exit"},
                "Play Again"
        );

        if (choice == 0) {
            // Restart game
            frame.setContentPane(new GamePanel(frame, music)); // Pass music
            frame.revalidate();
            SwingUtilities.invokeLater(() -> frame.getContentPane().requestFocusInWindow());
        } else {
            // Go back to menu
            frame.setContentPane(new MenuPanel(frame, music)); // Pass music
            frame.revalidate();
        }
    }





    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.GRAY);
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(Color.WHITE);
        int dashWidth = 10;
        int dashHeight = 20;
        int dashSpacing = 20;

        laneWidth = getWidth() / numLanes;

        // Moving lane lines
        for (int i = 1; i < numLanes; i++) {
            int x = i * laneWidth - dashWidth / 2;
            for (int y = -dashHeight + laneScrollOffset; y < getHeight(); y += dashHeight + dashSpacing) {
                g.fillRect(x, y, dashWidth, dashHeight);
            }
        }

        if (player != null) player.draw(g);
        for (Car c : cars) c.draw(g);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Score: " + score, 20, 30);

        if (paused) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(new Color(0, 0, 0, 150));
            g2d.fillRect(0, 0, getWidth(), getHeight());

            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 48));
            String pausedText = "GAME PAUSED";
            int textWidth = g2d.getFontMetrics().stringWidth(pausedText);
            g2d.drawString(pausedText, (getWidth() - textWidth) / 2, getHeight() / 2 - 100);
        }

        // Draw "ESC to Pause" at top-right corner
g.setColor(Color.WHITE);
g.setFont(new Font("Arial", Font.PLAIN, 16));

String pauseText = "ESC to Pause";
int textWidth = g.getFontMetrics().stringWidth(pauseText);
g.drawString(pauseText, getWidth() - textWidth - 20, 30); // 20 px from the right edge

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (gameOver) return;

        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT -> movingLeft = true;
            case KeyEvent.VK_RIGHT -> movingRight = true;
            case KeyEvent.VK_UP -> movingUp = true;
            case KeyEvent.VK_DOWN -> movingDown = true;
            case KeyEvent.VK_ESCAPE -> {
                music.playSFX("click.wav");
                if (!paused) pauseGame();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT -> movingLeft = false;
            case KeyEvent.VK_RIGHT -> movingRight = false;
            case KeyEvent.VK_UP -> movingUp = false;
            case KeyEvent.VK_DOWN -> movingDown = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}
}
