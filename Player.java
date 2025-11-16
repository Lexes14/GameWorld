import greenfoot.*;

public class Player extends Actor
{
    private int lastMovementRotation = 0;
    private int speed = 4;
    private int shootTimer = 0;

    private boolean isImmune = false;
    private int immunityTimer = 0;
    private GreenfootImage playerImage;

    public Player()
    {
        // Load and scale the image
        playerImage = new GreenfootImage("player.png");
        playerImage.scale(50, 50);
        setImage(playerImage);
    }

    public void act()
    {
        handleMovement();
        handleShooting();
        checkCollisions();
        handleImmunity();
    }

    private void handleMovement()
    {
        int x = getX();
        int y = getY();

        int rotation = -1;
        int imageRotationOffset = -90;

        boolean up = Greenfoot.isKeyDown("w");
        boolean down = Greenfoot.isKeyDown("s");
        boolean left = Greenfoot.isKeyDown("a");
        boolean right = Greenfoot.isKeyDown("d");

        if (up && right) rotation = 315;
        else if (up && left) rotation = 225;
        else if (down && right) rotation = 45;
        else if (down && left) rotation = 135;
        else if (up) rotation = 270;
        else if (right) rotation = 0;
        else if (down) rotation = 90;
        else if (left) rotation = 180;

        if (rotation != -1)
        {
            lastMovementRotation = rotation;
            setRotation(rotation + imageRotationOffset);

            double radians = Math.toRadians(rotation);
            int dx = (int) Math.round(speed * Math.cos(radians));
            int dy = (int) Math.round(speed * Math.sin(radians));

            int newX = x + dx;
            int newY = y + dy;

            if (newX > 15 && newX < getWorld().getWidth() - 15) x = newX;
            if (newY > 15 && newY < getWorld().getHeight() - 50) y = newY;

            setLocation(x, y);
        }
    }

    private void handleShooting()
    {
        if (shootTimer > 0) shootTimer--;

        if (Greenfoot.isKeyDown("space") && shootTimer == 0)
        {
            int bulletOffset = 50;

            double rotation = Math.toRadians(lastMovementRotation);
            int bulletX = getX() + (int)(bulletOffset * Math.cos(rotation));
            int bulletY = getY() + (int)(bulletOffset * Math.sin(rotation));

            Bullet bullet = new Bullet();
            bullet.setRotation(lastMovementRotation);

            getWorld().addObject(bullet, bulletX, bulletY);
            Greenfoot.playSound("fire.mp3");

            shootTimer = 15;
        }
    }

    private void checkCollisions()
    {
        Star star = (Star) getOneIntersectingObject(Star.class);
        if (star != null)
        {
            Greenfoot.playSound("kuha.mp3");
            getWorld().removeObject(star);
            ((GameWorld) getWorld()).starCollected();
        }

        if (!isImmune)
        {
            Enemy enemy = (Enemy) getOneIntersectingObject(Enemy.class);
            if (enemy != null)
            {
                ((GameWorld) getWorld()).playerHit();
                // immunity is now started externally from GameWorld after respawn
            }
        }
    }

    private void handleImmunity()
    {
        if (isImmune)
        {
            immunityTimer--;

            if ((immunityTimer / 5) % 2 == 0)
            {
                setImage(playerImage); // visible
            }
            else
            {
                setImage(new GreenfootImage(50, 50)); // invisible
            }

            if (immunityTimer <= 0)
            {
                isImmune = false;
                setImage(playerImage); // restore image
            }
        }
    }

    // New method to start immunity externally
    public void startImmunity()
    {
        isImmune = true;
        immunityTimer = 180; // 3 seconds at 60 FPS
    }
}
