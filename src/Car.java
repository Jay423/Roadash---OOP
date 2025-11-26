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
            "blackCar1.png", 
            "blackCar2.png", 
            "blackCar3.png",
            "purpleCar1.png",
            "purpleCar2.png",
            "purpleCar3.png",
            "blueCar1.png",
            "blueCar2.png",
            "blueCar3.png",
            "redCar1.png",
            "redCar2.png",
            "redCar3.png",
            "policeCar.png",
            "taxiCar.png"
        }; // your actual files
        carImages = new BufferedImage[carFiles.length];

        for (int i = 0; i < carFiles.length; i++) {
            try {
                // Updated path for nested folder
                carImages[i] = ImageIO.read(Car.class.getResource("/assets/cars/" + carFiles[i]));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Car(int x, int y) {
        this.x = x;
        this.y = y;

        // Pick a random image
        image = carImages[rand.nextInt(carImages.length)];
    }

    public void draw(Graphics g) {
        if (image != null) {
            g.drawImage(image, x, y, 60, 120, null); // adjust width/height if needed
        } else {
            g.setColor(Color.RED);
            g.fillRect(x, y, 60, 120);
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, 40, 105);
    }
}
