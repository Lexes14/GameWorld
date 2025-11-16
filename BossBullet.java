import greenfoot.*;

public class BossBullet extends Actor
{
    private int speed;
    private int damage;
    
    public BossBullet(int speed, int damage)
    {
        this.speed = speed;
        this.damage = damage;
        
        GreenfootImage img = new GreenfootImage(20, 8);
        img.setColor(new Color(150, 0, 200));
        img.fillOval(0, 0, 20, 8);
        img.setColor(new Color(100, 0, 150));
        img.drawOval(0, 0, 19, 7);
        setImage(img);
    }
    
    public void act()
    {
        move();
        checkPlayerCollision();
        
        if (isAtEdge())
        {
            getWorld().removeObject(this);
        }
    }
    
    private void move()
    {
        double radians = Math.toRadians(getRotation());
        int dx = (int)(speed * Math.cos(radians));
        int dy = (int)(speed * Math.sin(radians));
        setLocation(getX() + dx, getY() + dy);
    }
    
    private void checkPlayerCollision()
    {
        Player player = (Player) getOneIntersectingObject(Player.class);
        if (player != null)
        {
            ((GameWorld) getWorld()).playerHit();
            getWorld().removeObject(this);
        }
    }
    
    public int getDamage()
    {
        return damage;
    }
}