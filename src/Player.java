import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Player {
    public int x, y;
    private BufferedImage image;

    public Player(int x, int y) {
        this.x = x;
        this.y = y;

        try {
            // Load image from assets folder
            image = ImageIO.read(getClass().getResource("/assets/chicken.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void draw(Graphics g) {
        if (image != null) {
            g.drawImage(image, x, y, 50, 50, null); // 50x80 size (adjust if needed)
        } else {
            g.setColor(Color.BLUE); // fallback
            g.fillRect(x, y, 50, 80);
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, 50, 50);
    }
}
