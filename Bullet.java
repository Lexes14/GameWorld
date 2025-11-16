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
            Greenfoot.playSound("hit.mp3");      // Play hit sound
            return;                              // Exit act() early to avoid errors
        }
        
        // ✅ NEW: Check for collision with boss weak points
        BossWeakPoint weakPoint = (BossWeakPoint) getOneIntersectingObject(BossWeakPoint.class);
        if (weakPoint != null) {
            // The weak point will handle its own destruction
            // We just remove the bullet
            getWorld().removeObject(this);
            return;
        }
        
        // Remove bullet if it reaches edge of world
        if (isAtEdge()) {
            getWorld().removeObject(this);
        }
    }
}