import greenfoot.*;

public class BossWeakPoint extends Actor
{
    private Boss parentBoss;
    private int pointId;
    private int radius;
    private int angle;
    private boolean vulnerable = false;
    private boolean destroyed = false;
    
    private GreenfootImage vulnerableImage;
    private GreenfootImage invulnerableImage;
    
    public BossWeakPoint(Boss parent, int id, int radius, int angle)
    {
        this.parentBoss = parent;
        this.pointId = id;
        this.radius = radius;
        this.angle = angle;
        
        createImages();
        setImage(invulnerableImage);
    }
    
    private void createImages()
    {
        invulnerableImage = new GreenfootImage(30, 30);
        invulnerableImage.setColor(new Color(80, 80, 80));
        invulnerableImage.fillOval(0, 0, 30, 30);
        invulnerableImage.setColor(new Color(255, 0, 0, 150));
        invulnerableImage.fillOval(8, 8, 14, 14);
        
        vulnerableImage = new GreenfootImage(30, 30);
        vulnerableImage.setColor(new Color(255, 200, 0));
        vulnerableImage.fillOval(0, 0, 30, 30);
        vulnerableImage.setColor(new Color(255, 100, 0));
        vulnerableImage.fillOval(8, 8, 14, 14);
        vulnerableImage.setColor(Color.YELLOW);
        vulnerableImage.fillOval(12, 12, 6, 6);
    }
    
    public void act()
    {
        if (destroyed) return;
        
        checkBulletCollision();
        
        angle += 1;
        if (angle >= 360) angle = 0;
    }
    
    public void updatePosition()
    {
        if (destroyed || parentBoss == null || parentBoss.getWorld() == null) return;
        
        double radians = Math.toRadians(angle);
        int x = parentBoss.getX() + (int)(radius * Math.cos(radians));
        int y = parentBoss.getY() + (int)(radius * Math.sin(radians));
        
        setLocation(x, y);
    }
    
    private void checkBulletCollision()
    {
        if (!vulnerable) return;
        
        Bullet bullet = (Bullet) getOneIntersectingObject(Bullet.class);
        if (bullet != null)
        {
            getWorld().removeObject(bullet);
            destroyWeakPoint();
        }
    }
    
    private void destroyWeakPoint()
    {
        destroyed = true;
        Greenfoot.playSound("hit.mp3");
        
        if (parentBoss != null)
        {
            parentBoss.weakPointDestroyed();
        }
        
        getWorld().removeObject(this);
    }
    
    public void setVulnerable(boolean vulnerable)
    {
        this.vulnerable = vulnerable;
        
        if (vulnerable)
        {
            setImage(vulnerableImage);
        }
        else
        {
            setImage(invulnerableImage);
        }
    }
    
    public boolean isDestroyed()
    {
        return destroyed;
    }
}