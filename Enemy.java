import greenfoot.*;

public class Enemy extends Actor
{
    private int speed = 2;
    private int direction = 0;
    private int changeDirectionTimer = 0;
    
    public Enemy()
    {
        setImage("enemy.png"); // replace with your actual file name
        GreenfootImage img = new GreenfootImage("enemy.png");
        img.scale(80, 80);  // or whatever size you need
        setImage(img);
  
        direction = Greenfoot.getRandomNumber(360);
    }
    
    public void act()
    {
        move();
        checkBoundaries();
        checkBulletCollision();
        
        changeDirectionTimer++;
        if (changeDirectionTimer > 60)
        {
            direction = Greenfoot.getRandomNumber(360);
            changeDirectionTimer = 0;
        }
    }
    
    private void move()
    {
        setRotation(direction);
        move(speed);
    }
    
    private void checkBoundaries()
    {
        int x = getX();
        int y = getY();
        
        if (x <= 15 || x >= getWorld().getWidth() - 15 || 
            y <= 15 || y >= getWorld().getHeight() - 50)
        {
            direction = direction + 180;
            if (direction >= 360) direction -= 360;
        }
    }
    
    private void checkBulletCollision()
    {
        Bullet bullet = (Bullet) getOneIntersectingObject(Bullet.class);
        if (bullet != null)
        {
            getWorld().removeObject(bullet);
            getWorld().removeObject(this);
        }
    }
}