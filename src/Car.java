import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;
import javax.imageio.ImageIO;

public class Car {
    public int x, y;
    private BufferedImage image;
    private static BufferedImage[] carImages;
    private static Random rand = new Random();

    // Static block to load all car images once
    static {
        String[] carFiles = {
            "Car1.png", 
            "Car2.png", 
            "Car3.png",
            "Car4.png",
            "Car5.png",
            "Car6.png",
            "Car7.png",
            "Car8.png",
            "Car9.png",
            "Car10.png",
            "Car11.png",
            "Car12.png",
            "Car13.png",
            "Car14.png",
            "Car15.png",
            "Car16.png",
            "Car17.png",
            "Car18.png"
        }; // actual files in assets folder
        carImages = new BufferedImage[carFiles.length];

        for (int i = 0; i < carFiles.length; i++) {
            try {
                // Updated path for nested folder
                java.net.URL imageUrl = Car.class.getResource("/assets/cars/" + carFiles[i]);
                if (imageUrl == null) {
                    System.err.println("Car image not found: " + carFiles[i]);
                } else {
                    carImages[i] = ImageIO.read(imageUrl);
                }
            } catch (IOException e) {
                System.err.println("Failed to load car image " + carFiles[i] + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public Car(int x, int y) {
        this.x = x;
        this.y = y;

        // Pick a random image with null check
        if (carImages != null && carImages.length > 0) {
            int index = rand.nextInt(carImages.length);
            image = carImages[index];
            // If image is null, try to use first available image
            if (image == null && carImages.length > 0) {
                for (BufferedImage img : carImages) {
                    if (img != null) {
                        image = img;
                        break;
                    }
                }
            }
        }
    }

    public void draw(Graphics g) {
        if (image != null) {
            g.drawImage(image, x, y, 100, 170, null); // Bigger size for better visibility
        } else {
            g.setColor(Color.RED);
            g.fillRect(x, y, 100, 170);
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, 80, 150); // Updated collision bounds for bigger cars
    }
}
