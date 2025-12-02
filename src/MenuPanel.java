import java.awt.*;
import java.awt.event.*;
import java.io.InputStream;
import javax.swing.*;

public class MenuPanel extends JPanel implements KeyListener {

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

    // Chicken animation fields
    private Image chickenImage;
    private int chickenX = 50;  // Starting position on left
    private int chickenY = 500; // Y position (moved down lower)
    private boolean chickenWalking = false;
    private boolean chickenReacting = false;
    private int reactionFrame = 0;
    private int targetChickenX = 550; // Target position on right
    private Timer chickenTimer;
    private Timer reactionTimer;
    private boolean buttonsTransparent = false;

    public MenuPanel(GameFrame frame, MusicManager music) {
        this.frame = frame;
        this.music = music;

        setLayout(null);
        
        // Make panel focusable for keyboard input
        setFocusable(true);
        addKeyListener(this);
        
        loadCustomFont();

        // Load the background image
        ImageIcon icon = new ImageIcon(getClass().getResource("/assets/menu-bg.png"));
        bgImage = icon.getImage();

        // Load + SCALE title image ONCE with faster scaling
        ImageIcon titleIcon = new ImageIcon(getClass().getResource("/assets/title.png"));
        titleImage = titleIcon.getImage().getScaledInstance(titleWidth, titleHeight, Image.SCALE_FAST);

        // Load chicken image - bigger size like in the road
        ImageIcon chickenIcon = new ImageIcon(getClass().getResource("/assets/chicken0.png"));
        chickenImage = chickenIcon.getImage().getScaledInstance(90, 90, Image.SCALE_FAST); // Use FAST instead of SMOOTH

        // Initialize chicken animation timer with slower refresh for better performance
        chickenTimer = new Timer(75, e -> updateChickenAnimation()); // Increased from 50ms to 75ms

        // Start menu music
        this.music.stopMusic();
        this.music.playMusic("menu-music.wav", true);

        // Create Play button with faster scaling
        ImageIcon playIcon = new ImageIcon(getClass().getResource("/assets/play_button.png"));
        Image scaledPlayImg = playIcon.getImage().getScaledInstance(200, 90, Image.SCALE_FAST);

        ImageIcon exitIcon = new ImageIcon(getClass().getResource("/assets/exit.png"));
        Image scaledExitImg = exitIcon.getImage().getScaledInstance(200, 90, Image.SCALE_FAST);

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
            startChickenWalk();
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

        SwingUtilities.invokeLater(() -> {
            centerComponents();
            requestFocusInWindow(); // Request focus for keyboard input
        });
    }

    public void resetChicken() {
        chickenX = 50;
        chickenWalking = false;
        chickenReacting = false;
        reactionFrame = 0;
        buttonsTransparent = false; // Reset button transparency
        if (chickenTimer != null) {
            chickenTimer.stop();
        }
        if (reactionTimer != null) {
            reactionTimer.stop();
        }
        requestFocusInWindow(); // Ensure keyboard focus is maintained
        repaint();
    }

    private void centerComponents() {
        int panelWidth = getWidth();
        int panelHeight = getHeight();

        int spacing = 10;
        int startY = (panelHeight / 2) + 50;

        playBtn.setLocation((panelWidth - playBtn.getWidth()) / 2, startY);
        exitBtn.setLocation((panelWidth - exitBtn.getWidth()) / 2, startY + playBtn.getHeight() + spacing);
    }

    private void startChickenWalk() {
        if (!chickenWalking) {
            chickenWalking = true;
            buttonsTransparent = true; // Make buttons 50% transparent
            repaint(); // Refresh to show transparency effect
            chickenTimer.start();
        }
    }

    private void updateChickenAnimation() {
        if (chickenWalking && !chickenReacting) {
            chickenX += 12; // Move chicken 1.5x faster (8 * 1.5 = 12)
            repaint();

            // Check if chicken reached the target
            if (chickenX >= targetChickenX) {
                chickenWalking = false;
                chickenReacting = true;
                chickenTimer.stop();
                startSurpriseReaction();
            }
        }
    }
    
    private void startSurpriseReaction() {
        reactionFrame = 0;
        reactionTimer = new Timer(200, e -> { // Slower reaction timer to reduce CPU load
            reactionFrame++;
            repaint();
            
            // After 5 frames (about 1 second), start the game
            if (reactionFrame >= 5) {
                reactionTimer.stop();
                Timer gameStartTimer = new Timer(400, evt -> startGame());
                gameStartTimer.setRepeats(false);
                gameStartTimer.start();
            }
        });
        reactionTimer.start();
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

    public static int getHighestScore() {
        return highestScore;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Enable hardware acceleration hints
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        // Draw background image
        if (bgImage != null) {
            g2d.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
        }

        int panelWidth = getWidth();

        // Draw centered title image
        int titleX = (panelWidth - titleWidth) / 2;
        int titleY = 20;
        g2d.drawImage(titleImage, titleX, titleY, this);

        // Draw cute chicken with reaction effects
        if (chickenImage != null) {
            // Add bouncing effect during reaction
            int drawY = chickenY;
            if (chickenReacting) {
                // Bounce up and down
                drawY = chickenY + (int)(Math.sin(reactionFrame * 2) * 10);
                
                // Draw surprise effects around chicken
                g2d.setColor(Color.YELLOW);
                g2d.setFont(new Font("Arial", Font.BOLD, 24));
                
                // Exclamation marks that appear and fade
                if (reactionFrame <= 3) {
                    g2d.drawString("!", chickenX + 95, chickenY + 20);
                    g2d.drawString("!", chickenX + 110, chickenY + 35);
                }
                
                // Stars effect
                if (reactionFrame >= 2) {
                    g2d.setColor(Color.WHITE);
                    g2d.drawString("✦", chickenX - 20, chickenY + 30);
                    g2d.drawString("✦", chickenX + 100, chickenY + 60);
                    g2d.drawString("✧", chickenX - 10, chickenY + 70);
                }
            }
            
            g2d.drawImage(chickenImage, chickenX, drawY, this);
        }

        // Highest score text
        if (customFont != null) {
            g2d.setFont(customFont);
        } else {
            g2d.setFont(new Font("Arial", Font.PLAIN, 24));
        }
        g2d.setColor(Color.WHITE);
        String scoreText = "Highest Score: " + highestScore;
        int scoreWidth = g2d.getFontMetrics().stringWidth(scoreText);

        g2d.drawString(scoreText, (panelWidth - scoreWidth) / 2, 225);

        // Add instruction text when chicken is walking
        if (chickenWalking) {
            g2d.setFont(new Font("Arial", Font.BOLD, 20));
            g2d.setColor(Color.YELLOW);
            String walkText = "Get ready to play!";
            int walkWidth = g2d.getFontMetrics().stringWidth(walkText);
            g2d.drawString(walkText, (panelWidth - walkWidth) / 2, 260);
        }
        
        // Apply 50% transparency to buttons when clicked
        if (buttonsTransparent) {
            AlphaComposite transparent = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
            g2d.setComposite(transparent);
            
            // Draw semi-transparent overlay on button areas
            g2d.setColor(new Color(128, 128, 128, 128)); // Gray with 50% alpha
            if (playBtn != null) {
                g2d.fillRect(playBtn.getX(), playBtn.getY(), playBtn.getWidth(), playBtn.getHeight());
            }
            if (exitBtn != null) {
                g2d.fillRect(exitBtn.getX(), exitBtn.getY(), exitBtn.getWidth(), exitBtn.getHeight());
            }
            
            // Reset composite
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
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
            customFont = customFont.deriveFont(Font.PLAIN, 24f);  // set initial size
        } catch (Exception e) {
            e.printStackTrace();
            // fallback to Arial if loading fails
            customFont = new Font("Arial", Font.PLAIN, 24);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // Handle spacebar press to trigger PLAY button
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (!chickenWalking && !chickenReacting) {
                music.playSFX("click.wav");
                startChickenWalk();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // Not needed for this functionality
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Not needed for this functionality
    }
}
