import greenfoot.*;

public class GameWorld extends World
{
    private int level = 1;
    private int lives = 3;
    private int score = 0;
    private int starsLeft = 0;
    private boolean gameOver = false;
    private boolean levelComplete = false;
    private int levelChangeTimer = 0;

    // ✅ Background and game over music
    private GreenfootSound bgMusic = new GreenfootSound("bgmusic.mp3");
    private GreenfootSound gameOverSound = new GreenfootSound("gameover.mp3");

    private boolean levelCompleteSoundPlayed = false;
    
    // ✅ BOSS SYSTEM VARIABLES
    private boolean bossLevel = false;
    private boolean bossDefeated = false;
    private Boss currentBoss = null;

    public GameWorld()
    {    
        super(800, 600, 1);
        GreenfootImage bg = new GreenfootImage("bg.png");  
        bg.scale(getWidth(), getHeight());  
        setBackground(bg);

        startLevel(1);

        // ✅ Start music immediately when GameWorld is created
        bgMusic.setVolume(60);
        bgMusic.playLoop();
    }

    public void act()
    {
        showText("Level: " + level + "  Lives: " + lives + "  Score: " + score + "  Stars Left: " + starsLeft, 400, 30);
    
        if (levelComplete)
        {
            if (!levelCompleteSoundPlayed)
            {
                Greenfoot.playSound("nextlevel.mp3");
                levelCompleteSoundPlayed = true;
            }

            levelChangeTimer++;
            showText("LEVEL COMPLETE! Next level in " + (60 - levelChangeTimer) + "...", 400, 300);

            if (levelChangeTimer >= 60)
            {
                levelChangeTimer = 0;
                levelComplete = false;
                levelCompleteSoundPlayed = false;

                if (level < 100)
                {
                    level++;
                    lives = 3;
                    startLevel(level);
                }
                else
                {
                    gameWon();
                }
            }
        }

        if (gameOver)
        {
            // ✅ Display game over message with score and level achieved
            showText("GAME OVER!", 400, 250);
            showText("Level Reached: " + level + "  |  Final Score: " + score, 400, 300);
            showText("Press R to restart", 400, 350);
            
            if (Greenfoot.isKeyDown("r"))
            {
                restartGame();
            }
        }

        // ✅ MODIFIED: Check level completion (stars collected AND boss defeated if boss level)
        if (starsLeft == 0 && !levelComplete && !gameOver)
        {
            if (bossLevel)
            {
                // Boss level - must defeat boss to complete
                if (bossDefeated)
                {
                    levelComplete = true;
                    score += 100 * level;
                }
            }
            else
            {
                // Normal level - just collect stars
                levelComplete = true;
                score += 100 * level;
            }
        }
    }

    // ✅ Runs when world gains focus (e.g. resume after pause)
    public void started()
    {
        if (!bgMusic.isPlaying()) {
            bgMusic.playLoop();
        }
    }

    // ✅ Runs when world is stopped or left
    public void stopped()
    {
        bgMusic.stop();
        gameOverSound.stop();
    }

    public void startLevel(int levelNum)
    {
        removeObjects(getObjects(null));

        // ✅ Check if this is a boss level (5, 10, 15, 20, 25...)
        bossLevel = (levelNum % 5 == 0);
        bossDefeated = false;

        addObject(new Player(), 100, 300);

        if (bossLevel)
        {
            // ✅ BOSS LEVEL - No enemies, fewer stars
            int numStars = 2 + (levelNum / 10);  // Fewer stars on boss levels
            starsLeft = numStars;
            for (int i = 0; i < numStars; i++)
            {
                int x = Greenfoot.getRandomNumber(600) + 150;
                int y = Greenfoot.getRandomNumber(400) + 100;
                addObject(new Star(), x, y);
            }

            // ✅ Spawn boss in center-right of screen
            currentBoss = new Boss(levelNum);
            addObject(currentBoss, 600, 300);

            showText("⚠️ BOSS LEVEL " + levelNum + " ⚠️", 400, 300);
            Greenfoot.delay(90);
            showText("", 400, 300);
        }
        else
        {
            // ✅ NORMAL LEVEL - Regular enemies and stars
            int numStars = 3 + levelNum;
            starsLeft = numStars;
            for (int i = 0; i < numStars; i++)
            {
                int x = Greenfoot.getRandomNumber(600) + 150;
                int y = Greenfoot.getRandomNumber(400) + 100;
                addObject(new Star(), x, y);
            }

            int numEnemies = levelNum;
            for (int i = 0; i < numEnemies; i++)
            {
                int x = Greenfoot.getRandomNumber(400) + 300;
                int y = Greenfoot.getRandomNumber(400) + 100;
                addObject(new Enemy(), x, y);
            }

            showText("LEVEL " + levelNum + " - Collect all stars!", 400, 300);
            Greenfoot.delay(60);
            showText("", 400, 300);
        }
    }

    public void starCollected()
    {
        starsLeft--;
        score += 10;
    }

    public boolean playerHit()
    {
        lives--;

        if (lives <= 0)
        {
            bgMusic.stop();                  
            gameOverSound.setVolume(70);    
            gameOverSound.playLoop();       
            gameOver = true;
            return false;
        }
        else
        {
            Greenfoot.playSound("respawn.mp3");   
            removeObjects(getObjects(Player.class));

            Player newPlayer = new Player();
            addObject(newPlayer, 100, 300);
            newPlayer.startImmunity();

            return true;
        }
    }

    public void gameWon()
    {
        showText("CONGRATULATIONS! YOU WON!", 400, 250);
        showText("Final Score: " + score + "  |  All 100 Levels Complete!", 400, 300);
        showText("Press R to play again", 400, 350);
        gameOver = true;
    }

    public void restartGame()
    {
        gameOverSound.stop();     

        // ✅ Clear all text from screen
        showText("", 400, 250);
        showText("", 400, 300);
        showText("", 400, 350);

        // ✅ Recreate the background music to ensure it starts fresh
        bgMusic = new GreenfootSound("bgmusic.mp3");
        bgMusic.setVolume(60);
        bgMusic.playLoop();       

        level = 1;
        lives = 3;
        score = 0;
        gameOver = false;
        levelComplete = false;
        levelChangeTimer = 0;
        startLevel(1);
    }
    
    // ✅ NEW METHOD: Called when boss is defeated
    public void bossDefeated(int bossLevel)
    {
        bossDefeated = true;
        
        // Award massive bonus score for defeating boss
        score += 500 * (bossLevel / 5);  // Level 5 boss = 500, Level 10 = 1000, etc.
        
        // Show victory message
        showText("🎉 BOSS DEFEATED! 🎉", 400, 250);
        Greenfoot.delay(60);
        showText("", 400, 250);
        
        currentBoss = null;
    }
}