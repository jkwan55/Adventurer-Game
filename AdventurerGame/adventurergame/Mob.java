package adventurergame;


import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * @author anthony-pc
 */
public class Mob extends Moving{


    private int x;
    private int y;

    private final int R = 2;

    private BufferedImage img;
    private ArrayList<BufferedImage> mobImg;
    private Rectangle rect;
    private int HEALTH;
    private long imgCD = 100;
    private long timer = System.currentTimeMillis();
    private int runClock = 0;
    private Graphics2D buffer;

    Mob(int x, int y, ArrayList<BufferedImage> mobImg, Graphics2D buffer) {
        this.x = x;
        this.y = y;
        this.mobImg = mobImg;
        rect = new Rectangle(this.x, this.y, mobImg.get(0).getWidth(), mobImg.get(0).getHeight());
        this.HEALTH = 100;
        this.buffer = buffer;
    }

    public void update() {
        moveLeft();
        gravity();
        drawImage(buffer);
        rect.x = this.x;
        rect.y = this.y;
    }

    public void moveLeft() {
        x -= R/2;
        playerRun();
    }

    public void gravity() {
        y += R / 2;
    }

    public boolean checkBorder() {
        if (x < -5) {
            return true;
        }
        if(y > 565) {
            return true;
        }
        return false;
    }

    public void playerRun() {

            if (runClock == 0 && timer + imgCD < System.currentTimeMillis()) {
                this.img = mobImg.get(0);
                timer = System.currentTimeMillis();
                runClock++;
            }
            if (runClock == 1 && timer + imgCD < System.currentTimeMillis()) {
                this.img = mobImg.get(1);
                timer = System.currentTimeMillis();
                runClock = 0;
            }
        }

    public Rectangle getRect() {
        return this.rect;
    }

    public void setHEALTH(int hp) {
        this.HEALTH = hp;
    }

    public int getHEALTH() {
        return this.HEALTH;
    }

    public void changeX(int valueX) {        //changeX based on wall
        this.x = this.x + valueX;
        this.rect.x = this.x;
    }

    public void changeY(int valueY) {        //changeY based on wall
        this.y = this.y + valueY;
        this.rect.y = this.y;
    }

    @Override
    public String toString() {
        return "x=" + x + ", y=" + y;
    }


    public void drawImage(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.drawImage(this.img, x,y,this.mobImg.get(0).getWidth(),this.mobImg.get(0).getHeight(),null);
    }

}
