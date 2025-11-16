import greenfoot.*;

public class TitleScreen extends World
{
    private GreenfootSound introMusic = new GreenfootSound("intro.mp3");

    public TitleScreen()
    {    
        super(800, 600, 1); // world size

        // load and scale background image
        GreenfootImage bg = new GreenfootImage("introbg.png");
        bg.scale(getWidth(), getHeight());
        setBackground(bg);

        // instructions text
        showText("🎮 STAR HUNTER 🎮", getWidth()/2, 120);
        showText("Instructions:", getWidth()/2, 180);
        showText("W - Move Up", getWidth()/2, 210);
        showText("A - Move Left", getWidth()/2, 230);
        showText("S - Move Down", getWidth()/2, 250);
        showText("D - Move Right", getWidth()/2, 270);
        showText("SPACE - Shoot enemies", getWidth()/2, 290);
        showText("⭐ Collect all stars to level up ⭐", getWidth()/2, 320);
        showText("⚠ Avoid enemies or you lose!", getWidth()/2, 350);
        showText("Press ENTER to Start", getWidth()/2, 420);
    }

    public void act() {
        if (Greenfoot.isKeyDown("enter")) {
            introMusic.stop(); // stop music when switching worlds
            Greenfoot.setWorld(new GameWorld());
        }
    }

    // called when this world becomes active
    public void started() {
        introMusic.playLoop();
    }

    // called when scenario is paused/stopped
    public void stopped() {
        introMusic.stop();
    }
}
