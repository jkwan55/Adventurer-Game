package adventurergame;


import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * @author anthony-pc
 */
public class Player extends Moving{


    private int x;
    private int y;

    private final int R = 2;

    private BufferedImage img;
    private ArrayList<BufferedImage> runningImg;
    private ArrayList<BufferedImage> jumpingImg;
    private BufferedImage fallingImg;
    ArrayList<Bullet> allBullets = new ArrayList<>();
    private boolean UpPressed;
    private boolean DownPressed;
    private boolean RightPressed;
    private boolean LeftPressed;
    private boolean FirePressed;
    private Rectangle rect;
    private long lastItem;
    private int HEALTH;
    private int dmg;
    private int lives;
    private int spawnX;
    private int spawnY;
    private long cooldown = 1000;
    private long imgCD = 100;
    private long timer = System.currentTimeMillis();
    private int runClock = 0;
    private BufferedImage idle;
    private boolean rightFace = true;
    private boolean jumpCheck = true;
    private int jumpCount;
    private boolean fall;
    private long lastAttack;
    private BufferedImage imgBullet;
    private int angle;
    private BufferedImage castImg;
    private boolean cast;
    private int castDelay = 0;
    private int screenW;

    Player(int x, int y, BufferedImage img, ArrayList<BufferedImage> runningImg, ArrayList<BufferedImage> jumpingImg, BufferedImage fallingImg, BufferedImage imgBullet, BufferedImage castImg) {
        this.spawnX = x;
        this.spawnY = y;
        this.x = x;
        this.y = y;
        this.img = img;
        this.idle = img;
        this.runningImg = runningImg;
        this.jumpingImg = jumpingImg;
        this.fallingImg = fallingImg;
        rect = new Rectangle(this.x+16, this.y, img.getWidth()-32, img.getHeight());
        this.HEALTH = 100;
        this.dmg = 100;
        this.lives = 2;
        this.imgBullet = imgBullet;
        this.castImg = castImg;
    }


    void toggleUpPressed() {
        this.UpPressed = true;
    }

    void toggleDownPressed() {
        this.DownPressed = true;
    }

    void toggleRightPressed() {
        this.RightPressed = true;
    }

    void toggleLeftPressed() {
        this.LeftPressed = true;
    }

    void toggleFirePressed() {
        this.FirePressed = true;
    }

    void unToggleUpPressed() {
        this.UpPressed = false;
    }

    void unToggleDownPressed() {
        this.DownPressed = false;
    }

    void unToggleRightPressed() {
        this.RightPressed = false;
    }

    void unToggleLeftPressed() {
        this.LeftPressed = false;
    }

    void unToggleFirePressed() {
        this.FirePressed = false;
    }


    public void update() {
        if(!cast) {
            if (this.UpPressed) {
                this.moveUp();
            }
            if (this.DownPressed) {
                this.moveDown();
            }

            if (this.LeftPressed) {
                this.moveLeft();
                rightFace = false;
                angle = 180;
            }
            if (this.RightPressed) {
                this.moveRight();
                rightFace = true;
                angle = 0;
            }
            if (this.FirePressed) {
                this.shoot();
            }

            if (!jumpCheck) {
                this.moveUp();
            }
            if (!this.RightPressed && !this.LeftPressed && jumpCheck) {
                this.img = idle;
                runClock = 0;
                jumpCount = 0;
                fall = false;
            }
            if (jumpCheck && fall) {
                this.img = idle;
                jumpCount = 0;
                fall = false;
            }

            gravity();
        } else {
            this.img = castImg;
            if(castDelay >= 5) {
                cast = false;
                castDelay = 0;
            }
            castDelay++;
        }
        rect.x = x + 16;
        rect.y = y;
    }

    public void moveLeft() {
        x -= R;
        playerRun();
        checkBorder();
    }

    private void moveRight() {
        x += R;
        playerRun();
        checkBorder();
    }

    private void moveDown() {
        y += R;
    }

    public void moveUp() {
        if (jumpCheck) {
            this.img = jumpingImg.get(0);
            if (jumpCount <= 65) {
                y -= R;
                jumpCount++;
                jumpCheck = false;
            }
        }
        if (!jumpCheck && jumpCount <= 65) {
            fall = false;
            y -= R;
            jumpCount++;
        }
        if(jumpCount == 5){
            this.img = jumpingImg.get(1);
        }
        if(jumpCount == 15){
            this.img = jumpingImg.get(2);
        }
        if(jumpCount == 45){
            this.img = jumpingImg.get(3);
        }
        if(jumpCount == 65){
            fall = true;
        }
    }

    public void gravity() {
        y += R / 2;
        if(!jumpCheck && fall){
            this.img = fallingImg;
        }
    }


    public void checkBorder() {
        if (x < -5) {
            x = -5;
        }
        if (x >= GameWorld.GAME_WIDTH - 50) {
            x = GameWorld.GAME_WIDTH - 50;
        }
    }

    public void playerRun() {
        if (!fall && jumpCheck) {
            if (runClock == 0 && timer + imgCD < System.currentTimeMillis()) {
                this.img = runningImg.get(0);
                timer = System.currentTimeMillis();
                runClock++;
            }
            if (runClock == 1 && timer + imgCD < System.currentTimeMillis()) {
                this.img = runningImg.get(1);
                timer = System.currentTimeMillis();
                runClock++;
            }
            if (runClock == 2 && timer + imgCD < System.currentTimeMillis()) {
                this.img = runningImg.get(2);
                timer = System.currentTimeMillis();
                runClock++;
            }
            if (runClock == 3 && timer + imgCD < System.currentTimeMillis()) {
                this.img = runningImg.get(3);
                timer = System.currentTimeMillis();
                runClock++;
            }
            if (runClock == 4 && timer + imgCD < System.currentTimeMillis()) {
                this.img = runningImg.get(4);
                timer = System.currentTimeMillis();
                runClock++;
            }
            if (runClock == 5 && timer + imgCD < System.currentTimeMillis()) {
                this.img = runningImg.get(5);
                timer = System.currentTimeMillis();
                runClock = 0;
            }
        }
    }

    private void shoot(){       //shoot once per second
        long time = System.currentTimeMillis();
        if(time > lastAttack + cooldown) {
            cast = true;
            allBullets.add(new Bullet(x+img.getWidth()/2,y+img.getHeight()/2,angle,imgBullet));
            lastAttack = time;
        }
    }

    public void isFalling(){
        this.jumpCheck = false;
        this.fall = true;
    }

    public void setJumpCount(int count) {
        this.jumpCount = count;
    }

    public ArrayList<Bullet> getBulletList() {
        return allBullets;
    }

    public Rectangle getRect() {
        return rect;
    }

    public void setHEALTH(int hp) {
        this.HEALTH = hp;
    }

    public int getHEALTH() {
        return this.HEALTH;
    }


    public void damage() {                   // 25dmg per shot, 3 lives, respawn location
        this.HEALTH = this.HEALTH - dmg;
        if (this.HEALTH <= 0 && this.lives != 0) {
            this.HEALTH = 100;
            this.lives--;
            this.x = spawnX;
            this.y = spawnY;
            this.cooldown = 1000;
        }
    }

    public void toggleJump(boolean check) {
        jumpCheck = check;
    }

    public void setDmg(int dam) {
        this.dmg = dam;
    }

    public int getDmg() {
        return this.dmg;
    }

    public int getX(){
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getLives() {
        return this.lives;
    }

    public void setLives(int l) {
        this.lives = l;
    }

    public void powerUp(int power) {                    //cooldown for power ups due to bug
        long time = System.currentTimeMillis();
        long cd = 100;
        if (time > lastItem + cd) {
            if (power == 2) {
                this.cooldown = this.cooldown / 2;
            } else if (power == 1) {
                if (this.HEALTH == 100) {
                    lives++;
                } else {
                    this.HEALTH = 100;
                }
            }
            lastItem = time;
        }
    }


    public BufferedImage updateCam(BufferedImage screen) {       //screen tracking
        BufferedImage cam;
        screenW = x - GameWorld.SCREEN_WIDTH / 3;
        int screenH = y - GameWorld.SCREEN_HEIGHT/3;

        if (x - GameWorld.SCREEN_WIDTH / 3 <= 0) { //left portion
            screenW = 0;
        }
        if (x + GameWorld.SCREEN_WIDTH / 3 >= GameWorld.GAME_WIDTH) {
            screenW = GameWorld.GAME_WIDTH - GameWorld.SCREEN_WIDTH*2/3;
        }
        if (y - GameWorld.SCREEN_HEIGHT/3 <= 0) { //top portion
            screenH = 0;
        }
        if (y + GameWorld.SCREEN_HEIGHT/3 >= GameWorld.GAME_HEIGHT) {
            screenH = GameWorld.GAME_HEIGHT - GameWorld.SCREEN_HEIGHT*2/3;
        }
        cam = screen.getSubimage(screenW, screenH, GameWorld.SCREEN_WIDTH*2/3, GameWorld.SCREEN_HEIGHT*2/3);
        return cam;
    }

    public int getScreenW(){
        return screenW;
    }


    public void setX(int theX) {
        this.x = theX;
    }

    public void setY(int theY) {
        this.y = theY;
    }

    public void changeX(int valueX) {        //changeX based on wall
        this.x = this.x + valueX;
        this.rect.x = this.x +16;
    }

    public void changeY(int valueY) {        //changeY based on wall
        this.y = this.y + valueY;
        this.rect.y = this.y;
    }

    public void setSpawn(int x, int y){
        spawnX = x;
        spawnY = y;
    }


    @Override
    public String toString() {
        return "x=" + x + ", y=" + y;
    }


    public void drawImage(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        //g2d.drawRect(x+16, y, this.img.getWidth()-32, this.img.getHeight());
        if (rightFace) {
            g2d.drawImage(this.img, x,y,this.img.getWidth(),this.img.getHeight(),null);
        } else {
            g2d.drawImage(this.img, x + this.img.getWidth(), y, this.img.getWidth() * -1, this.img.getHeight(), null);
        }
    }

}
