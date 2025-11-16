import greenfoot.*;

public class Bullet extends Actor
{
    private int speed = 8;

    public Bullet()
    {
        GreenfootImage img = new GreenfootImage(16, 6);
        img.setColor(Color.RED);
        img.fillOval(0, 0, 16, 6); // red oval bullet
        setImage(img);
    }

    public void act()
    {
        // Move bullet forward based on rotation
        double radians = Math.toRadians(getRotation());
        int dx = (int)(speed * Math.cos(radians));
        int dy = (int)(speed * Math.sin(radians));
        setLocation(getX() + dx, getY() + dy);

        // Check for collision with enemy
        Enemy enemy = (Enemy) getOneIntersectingObject(Enemy.class);
        if (enemy != null) {
            getWorld().removeObject(enemy);      // Remove enemy
            getWorld().removeObject(this);       // Remove bullet
            Greenfoot.playSound("hit.mp3");      // Play hit sound (make sure hit.mp3 is in sounds folder)
            return;                              // Exit act() early to avoid errors
        }

        // Remove bullet if it reaches edge of world
        if (isAtEdge()) {
            getWorld().removeObject(this);
        }
    }
}
