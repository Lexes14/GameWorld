import greenfoot.*;
import java.util.ArrayList;

public class Boss extends Actor
{
    // Boss stats that scale with level
    private int bossLevel;
    private int speed;
    private int bulletSpeed;
    private int bulletDamage;
    private double moveSpeed;
    
    // Movement
    private int direction;
    private int changeDirectionTimer = 0;
    
    // Shooting
    private int shootTimer = 0;
    private int shootDelay;
    
    // Mortal mode system
    private boolean isVulnerable = false;
    private int modeTimer = 0;
    private int invulnerableTime = 900;  // 15 seconds at 60 FPS
    private int vulnerableTime = 1200;    // 20 seconds at 60 FPS
    
    // Weak points
    private ArrayList<BossWeakPoint> weakPoints;
    private int weakPointsRemaining = 3;
    
    // Visual
    private GreenfootImage bossImage;
    private GreenfootImage redBoss;
    private GreenfootImage greenBoss;
    
    public Boss(int level)
    {
        this.bossLevel = level;
        
        // Scale stats based on boss level
        int bossIteration = level / 5;
        this.speed = 1 + bossIteration;
        this.moveSpeed = 1.0 + (bossIteration * 0.3);
        this.bulletSpeed = 5 + bossIteration;
        this.bulletDamage = 1;
        this.shootDelay = Math.max(30, 90 - (bossIteration * 5));
        
        // Create boss images
        createBossImages();
        setImage(redBoss);
        
        direction = Greenfoot.getRandomNumber(360);
        weakPoints = new ArrayList<BossWeakPoint>();
    }
    
    private void createBossImages()
    {
        try {
            bossImage = new GreenfootImage("boss.png");
            bossImage.scale(100, 100);
            
            redBoss = new GreenfootImage(bossImage);
            redBoss.setTransparency(255);
            redBoss.setColor(new Color(255, 0, 0, 100));
            redBoss.fill();
            
            greenBoss = new GreenfootImage(bossImage);
            greenBoss.setTransparency(255);
            greenBoss.setColor(new Color(0, 255, 0, 100));
            greenBoss.fill();
        } catch (Exception e) {
            redBoss = new GreenfootImage(100, 100);
            redBoss.setColor(Color.RED);
            redBoss.fillRect(0, 0, 100, 100);
            redBoss.setColor(Color.DARK_GRAY);
            redBoss.drawRect(0, 0, 99, 99);
            
            greenBoss = new GreenfootImage(100, 100);
            greenBoss.setColor(Color.GREEN);
            greenBoss.fillRect(0, 0, 100, 100);
            greenBoss.setColor(Color.DARK_GRAY);
            greenBoss.drawRect(0, 0, 99, 99);
        }
    }
    
    protected void addedToWorld(World world)
    {
        createWeakPoints();
    }
    
    private void createWeakPoints()
    {
        int[] angles = {0, 120, 240};
        int radius = 60;
        
        for (int i = 0; i < 3; i++)
        {
            BossWeakPoint wp = new BossWeakPoint(this, i, radius, angles[i]);
            weakPoints.add(wp);
            getWorld().addObject(wp, getX(), getY());
        }
    }
    
    public void act()
    {
        if (weakPointsRemaining <= 0)
        {
            defeatBoss();
            return;
        }
        
        handleMortalMode();
        move();
        shoot();
        checkPlayerCollision();
        updateWeakPointPositions();
    }
    
    private void handleMortalMode()
    {
        modeTimer++;
        
        if (!isVulnerable)
        {
            if (modeTimer >= invulnerableTime)
            {
                isVulnerable = true;
                setImage(greenBoss);
                modeTimer = 0;
                Greenfoot.playSound("respawn.mp3");
                
                for (BossWeakPoint wp : weakPoints)
                {
                    if (!wp.isDestroyed())
                    {
                        wp.setVulnerable(true);
                    }
                }
            }
        }
        else
        {
            if (modeTimer >= vulnerableTime)
            {
                isVulnerable = false;
                setImage(redBoss);
                modeTimer = 0;
                
                for (BossWeakPoint wp : weakPoints)
                {
                    if (!wp.isDestroyed())
                    {
                        wp.setVulnerable(false);
                    }
                }
            }
        }
    }
    
    private void move()
    {
        changeDirectionTimer++;
        
        if (changeDirectionTimer > 90)
        {
            direction = Greenfoot.getRandomNumber(360);
            changeDirectionTimer = 0;
        }
        
        setRotation(direction);
        double radians = Math.toRadians(direction);
        int dx = (int)(moveSpeed * Math.cos(radians));
        int dy = (int)(moveSpeed * Math.sin(radians));
        
        int newX = getX() + dx;
        int newY = getY() + dy;
        
        if (newX <= 50 || newX >= getWorld().getWidth() - 50)
        {
            direction = 180 - direction;
            if (direction < 0) direction += 360;
        }
        if (newY <= 50 || newY >= getWorld().getHeight() - 100)
        {
            direction = 360 - direction;
        }
        
        newX = Math.max(50, Math.min(getWorld().getWidth() - 50, newX));
        newY = Math.max(50, Math.min(getWorld().getHeight() - 100, newY));
        
        setLocation(newX, newY);
    }
    
    private void shoot()
    {
        shootTimer++;
        
        if (shootTimer >= shootDelay)
        {
            int randomDirection = Greenfoot.getRandomNumber(360);
            
            BossBullet bullet = new BossBullet(bulletSpeed, bulletDamage);
            bullet.setRotation(randomDirection);
            
            double radians = Math.toRadians(randomDirection);
            int bulletX = getX() + (int)(60 * Math.cos(radians));
            int bulletY = getY() + (int)(60 * Math.sin(radians));
            
            getWorld().addObject(bullet, bulletX, bulletY);
            Greenfoot.playSound("fire.mp3");
            
            shootTimer = 0;
        }
    }
    
    private void checkPlayerCollision()
    {
        Player player = (Player) getOneIntersectingObject(Player.class);
        if (player != null)
        {
            ((GameWorld) getWorld()).playerHit();
        }
    }
    
    private void updateWeakPointPositions()
    {
        for (BossWeakPoint wp : weakPoints)
        {
            wp.updatePosition();
        }
    }
    
    public void weakPointDestroyed()
    {
        weakPointsRemaining--;
        
        if (weakPointsRemaining <= 0)
        {
            defeatBoss();
        }
    }
    
    private void defeatBoss()
    {
        for (BossWeakPoint wp : weakPoints)
        {
            if (!wp.isDestroyed())
            {
                getWorld().removeObject(wp);
            }
        }
        
        Greenfoot.playSound("kuha.mp3");
        ((GameWorld) getWorld()).bossDefeated(bossLevel);
        getWorld().removeObject(this);
    }
    
    public boolean isVulnerable()
    {
        return isVulnerable;
    }
    
    public int getBossLevel()
    {
        return bossLevel;
    }
}