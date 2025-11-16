import greenfoot.*;

public class Star extends Actor
{
    private int rotation = 0;
    
    public Star()
    {
        // Create a yellow star
        GreenfootImage img = new GreenfootImage(20, 20);
        img.setColor(Color.YELLOW);
        img.fillOval(0, 0, 20, 20);
        // Add some star points
        img.setColor(Color.ORANGE);
        img.fillOval(8, 8, 4, 4);
        setImage(img);
    }
    
    public void act()
    {
        // Rotate the star for visual effect
        rotation += 2;
        setRotation(rotation);
    }
}