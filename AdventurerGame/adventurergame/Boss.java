package adventurergame;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Boss extends Moving {

    private int x;
    private int y;

    private final int R = 2;

    private BufferedImage img;
    ArrayList<Bullet> allBullets = new ArrayList<>();
    private Rectangle rect;
    private int HEALTH;
    private int dmg;
    private long cooldown = 1000;
    private int TileX;
    private int TileY;
    private int shootX;
    private int shootY;
    private long lastAttack;
    private BufferedImage imgBullet;

    Boss(int x, int y, BufferedImage img, Graphics2D buffer, int i, int j, BufferedImage imgBullet) {
        this.x = x;
        this.y = y;
        this.img = img;
        rect = new Rectangle(this.x+20, this.y+30, img.getWidth()-40, img.getHeight());
        this.HEALTH = 250;
        this.dmg = 25;
        drawImage(buffer);
        TileX = i;
        TileY = j;
        shootX = this.x+img.getWidth()/2-40;
        shootY = this.y+img.getHeight()/2+10;
        this.imgBullet = imgBullet;
    }

    public void shoot(int playerX, int playerY){
        double triangleW = shootX - playerX;
        double triangleH = playerY - shootY;
        double tan = triangleW/triangleH;
        int angle = (int) Math.toDegrees(Math.atan(tan));
        long time = System.currentTimeMillis();
        if(time > lastAttack + cooldown) {
            if(angle > 0) {
                allBullets.add(new Bullet(shootX, shootY, angle +90, imgBullet));
            } else {
                allBullets.add(new Bullet(shootX, shootY, angle -90,imgBullet));
            }
            lastAttack = time;
        }
    }

    public void gravity(){
        y += R/2;
    }

    public Rectangle getRect(){
        return this.rect;
    }

    public int getHEALTH(){
        return this.HEALTH;
    }

    public void damage(){
        this.HEALTH = this.HEALTH - this.dmg;
    }

    public int getTileX(){
        return TileX;
    }

    public int getTileY(){
        return TileY;
    }

    public ArrayList<Bullet> getBulletList(){
        return allBullets;
    }

    public void drawImage(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            //g2d.drawRect(x + 20, y + 30, this.img.getWidth() - 40, this.img.getHeight());
            g2d.drawImage(this.img, x, y, this.img.getWidth(), this.img.getHeight(), null);
            g2d.setColor(Color.white);
    }
}
