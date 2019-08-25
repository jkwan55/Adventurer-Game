/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adventurergame;


import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import static javax.imageio.ImageIO.read;

/**
 * @author anthony-pc
 */
public class GameWorld extends JPanel {


    public static final int SCREEN_WIDTH = 900;
    public static final int SCREEN_HEIGHT = 600;
    public static final int GAME_WIDTH = 2500;
    public static final int GAME_HEIGHT = 600;
    private BufferedImage world;
    private BufferedImage bg;
    private BufferedImage bg2;
    private BufferedImage wall;
    private BufferedImage wall_break;
    private BufferedImage floor;
    private BufferedImage base;
    private BufferedImage powerBlock;
    private BufferedImage deadBlock;
    private BufferedImage block;
    private BufferedImage power1;
    private BufferedImage power2;
    private BufferedImage boss;
    private BufferedImage bulletimg;
    private BufferedImage castImg;
    private BufferedImage t1img;
    private ArrayList<BufferedImage> runningImg = new ArrayList<>();
    private ArrayList<BufferedImage> jumpingImg = new ArrayList<>();
    private ArrayList<BufferedImage> mobImg = new ArrayList<>();
    private BufferedImage fallingImg;
    ArrayList<Walls> allWalls = new ArrayList<>();
    ArrayList<PowerUp> allPowers = new ArrayList<>();
    ArrayList<Boss> bosses = new ArrayList<>();
    private ArrayList<ArrayList<Integer>> map = new ArrayList<>();
    private Graphics2D buffer;
    private JFrame jf;
    private Player t1;
    private BufferedImage t2img;
    private boolean level1 = true;
    private boolean bossCheck = true;
    private boolean first = true;
    private ArrayList<Mob> mobs = new ArrayList<>();

    public static void main(String[] args) {
        Thread x;
        GameWorld trex = new GameWorld();
        trex.init();
        try {
            while (true) {

                trex.t1.update();
                trex.repaint();

                Thread.sleep(1000 / 144);
            }
        } catch (InterruptedException ignored) {

        }

    }

    private void init() {

        this.jf = new JFrame("Player Rotation");
        this.world = new BufferedImage(GameWorld.GAME_WIDTH, GameWorld.GAME_HEIGHT, BufferedImage.TYPE_INT_RGB);

        try {
            BufferedImage tmp;
            System.out.println(System.getProperty("user.dir"));
            /*
             * note class loaders read files from the out folder (build folder in netbeans) and not the
             * current working directory.
             */
            t1img = read(new File("resources/char/adventurer-idle-00.png"));
            runningImg.add(read(new File("resources/char/adventurer-run-00.png")));
            runningImg.add(read(new File("resources/char/adventurer-run-01.png")));
            runningImg.add(read(new File("resources/char/adventurer-run-02.png")));
            runningImg.add(read(new File("resources/char/adventurer-run-03.png")));
            runningImg.add(read(new File("resources/char/adventurer-run-04.png")));
            runningImg.add(read(new File("resources/char/adventurer-run-05.png")));
            jumpingImg.add(read(new File("resources/char/adventurer-jump-00.png")));
            jumpingImg.add(read(new File("resources/char/adventurer-jump-01.png")));
            jumpingImg.add(read(new File("resources/char/adventurer-jump-02.png")));
            jumpingImg.add(read(new File("resources/char/adventurer-jump-03.png")));
            fallingImg = read(new File("resources/char/adventurer-fall-00.png"));
            bulletimg = read(new File("resources/Weapon.png"));
            bg = read(new File("resources/world.png"));
            bg2 = read(new File("resources/castle.png"));
            wall = read(new File("resources/ground.png"));
            wall_break = read(new File("resources/grass.png"));
            floor = read(new File("resources/castleFloor.png"));
            base = read(new File("resources/castleBase.png"));
            powerBlock = read(new File("resources/PowerBlock.png"));
            deadBlock = read(new File("resources/DeadBlock.png"));
            block = read(new File("resources/Block.png"));
            power1 = read(new File("resources/power1.png"));
            power2 = read(new File("resources/power2.gif"));
            boss = read(new File("resources/Boss.png"));
            castImg = read(new File("resources/char/adventurer-cast-00.png"));
            mobImg.add(read(new File("resources/Mob1.png")));
            mobImg.add(read(new File("resources/Mob2.png")));


        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        t1 = new Player(20, 450, t1img, runningImg, jumpingImg, fallingImg, bulletimg, castImg);

        PlayerControls tc1 = new PlayerControls(t1, KeyEvent.VK_W, KeyEvent.VK_S, KeyEvent.VK_A, KeyEvent.VK_D, KeyEvent.VK_SPACE);

        this.jf.setLayout(new BorderLayout());
        this.setBackground(Color.black);
        this.jf.add(this);

        this.jf.addKeyListener(tc1);

        this.jf.setSize(GameWorld.SCREEN_WIDTH, GameWorld.SCREEN_HEIGHT);
        this.jf.setResizable(false);
        jf.setLocationRelativeTo(null);

        this.jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.jf.setVisible(true);

        readMap("resources/map.txt"); // initial map
    }

    private void drawBackGround() {      //draw sand background tiles
        int TileWidth = bg.getWidth();
        int TileHeight = bg.getHeight();

        int NumberX = (int) (GAME_WIDTH / TileWidth);
        int NumberY = (int) (GAME_HEIGHT / TileHeight);

        for (int i = -1; i <= NumberY; i++) {
            for (int j = 0; j <= NumberX; j++) {
                buffer.drawImage(bg, j * TileWidth,
                        i * TileHeight, TileWidth,
                        TileHeight, null);
            }
        }
    }

    private void showBullets(Moving obj) { //parent class has methods needed, this used for player and boss
        if (obj instanceof Boss) {
            if (!bosses.isEmpty()) {    //has to check if not empty first
                showB(obj);
            }
        } else {
            showB(obj);
        }
    }

    private void showB(Moving obj) {
        if (!obj.getBulletList().isEmpty()) {
            for (int i = 0; i < obj.getBulletList().size(); i++) {
                if ((obj.getBulletList().get(i)).getShow()) {
                    obj.getBulletList().get(i).update();
                } else {
                    obj.getBulletList().remove(i);
                }
            }
        }
    }

    private void readMap(String file) {        //read initial map, put into 2d array
        map.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String currentLine;
            while ((currentLine = br.readLine()) != null) {
                if (currentLine.isEmpty())
                    continue;
                ArrayList<Integer> rows = new ArrayList<>();
                String[] values = currentLine.trim().split(" ");
                for (String str : values) {
                    if (!str.isEmpty()) {
                        int num = Integer.parseInt(str);
                        rows.add(num);
                    }
                }
                map.add(rows);
            }
        } catch (IOException e) {
        }
    }

    public void mapUpdate() {    //print current map from 2d array, spawns in mobs and boss
        allWalls.clear();
        int TileWidth = wall.getWidth();
        int TileHeight = wall.getHeight();

        int width = map.get(0).size();
        int height = map.size();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (map.get(j).get(i) == 1) {
                    levelFloors(TileWidth, TileHeight, i, j, wall, base);
                } else if (map.get(j).get(i) == 2) {
                    levelFloors(TileWidth, TileHeight, i, j, wall_break, floor);
                }

                if (map.get(j).get(i) == 3) {
                    allWalls.add(new Walls(i * TileWidth, j * TileHeight, TileWidth, TileHeight, false, block, buffer, i, j));
                } else if (map.get(j).get(i) == 4) {
                    allWalls.add(new Walls(i * TileWidth, j * TileHeight, TileWidth, TileHeight, true, powerBlock, buffer, i, j));
                } else if (map.get(j).get(i) == 7) {
                    allWalls.add(new Walls(i * TileWidth, j * TileHeight, TileWidth, TileHeight, false, deadBlock, buffer, i, j));
                } else if (map.get(j).get(i) == 5) {
                    bosses.add(new Boss(i * TileWidth, j * TileHeight, boss, buffer, i, j, bulletimg));
                } else if (map.get(j).get(i) == 6) {
                    mobs.add(new Mob(i * TileWidth, j * TileHeight, mobImg, buffer));
                    mapChange(i, j, 0);
                }
            }
        }
    }

    private void levelFloors(int tileWidth, int tileHeight, int i, int j, BufferedImage wall, BufferedImage base) {
        if (level1) {
            allWalls.add(new Walls(i * tileWidth, j * tileHeight, tileWidth, tileHeight, false, wall, buffer, i, j));
        } else {
            allWalls.add(new Walls(i * tileWidth, j * tileHeight, tileWidth, tileHeight, false, base, buffer, i, j));

        }
    }

    private void collisionCheck(Moving obj) {
        for (int r = 0; r < allPowers.size(); r++) {                          // remove power ups after pickup
            if (t1.getRect().intersects(allPowers.get(r).getRect())) {
                t1.powerUp(allPowers.get(r).getPowerUp());
                allPowers.get(r).setPowerUp(0);
                allPowers.remove(r);
            }
        }
        if (!bosses.isEmpty()) {
            if (bosses.get(0).getRect().intersects(t1.getRect())) {
                Rectangle2D inter = t1.getRect().createIntersection(bosses.get(0).getRect());
                if ((inter.getMinX() >= obj.getRect().getMinX()) && (inter.getHeight() >= inter.getWidth())) {  // going right, push left..
                    int newX = (int) inter.getWidth() * -1;
                    t1.changeX(newX);
                }
            }
            for (int p = 0; p < t1.getBulletList().size(); p++) {                    //bullet into boss
                if (t1.getBulletList().get(p).getRect().intersects(bosses.get(0).getRect())) {
                    t1.getBulletList().remove(p);
                    bosses.get(0).damage();
                }
            }
            for (int d = 0; d < bosses.get(0).getBulletList().size(); d++) {    //bullet into player
                if (bosses.get(0).getBulletList().get(d).getRect().intersects(t1.getRect())) {
                    bosses.get(0).getBulletList().remove(d);
                    t1.damage();
                }
            }
        }
        if (!mobs.isEmpty()) {
            for (int m = 0; m < mobs.size(); m++) {
                if (mobs.get(m).getRect().intersects(t1.getRect())) {
                    Rectangle2D inter = t1.getRect().createIntersection(mobs.get(m).getRect());
                    if ((inter.getMinY() >= t1.getRect().getMinY()) && (inter.getHeight() <= inter.getWidth())) {  // jump on mob kills
                        int newY = (int) inter.getHeight() * -1;
                        obj.changeY(newY);
                        t1.toggleJump(true);
                        t1.setJumpCount(0);
                        t1.moveUp();
                        mobs.remove(m);
                    } else {                // mob hits player, player dies
                        mobs.remove(m);
                        t1.damage();
                    }
                }
            }
        }


        for (int i = 0; i < allWalls.size(); i++) {

            if (obj.getRect().intersects(allWalls.get(i).getRect())) {             //obj into wall
                Rectangle2D inter = obj.getRect().createIntersection(allWalls.get(i).getRect());
                if ((inter.getMaxX() < obj.getRect().getMaxX()) && (inter.getHeight() >= inter.getWidth())) { // going left, push right
                    if (obj instanceof Mob == false) {
                        obj.changeX((int) inter.getWidth());
                    }
                }
                if ((inter.getMinX() >= obj.getRect().getMinX()) && (inter.getHeight() >= inter.getWidth())) {  // going right, push left..
                    int newX = (int) inter.getWidth() * -1;
                    obj.changeX(newX);
                }
                if ((inter.getMaxY() < obj.getRect().getMaxY()) && (inter.getHeight() <= inter.getWidth())) {  // going up, push back down
                    obj.changeY((int) inter.getHeight());
                    if (allWalls.get(i).getBreak() == true) {   //random powerUp from block
                        Random rand = new Random();
                        int n = rand.nextInt(2);
                        if (n == 0) {
                            //Extra Life
                            allPowers.add(new PowerUp(allWalls.get(i).getArrayW() * wall.getWidth(), allWalls.get(i).getArrayH() * wall.getHeight() - power1.getHeight(), power1.getWidth(), power1.getHeight(), power1, buffer, n + 1));
                        } else {
                            //Increase Fire Rate
                            allPowers.add(new PowerUp(allWalls.get(i).getArrayW() * wall.getWidth(), allWalls.get(i).getArrayH() * wall.getHeight() - power1.getHeight(), power1.getWidth(), power1.getHeight(), power2, buffer, n + 1));
                        }
                        mapChange(allWalls.get(i).getArrayW(), allWalls.get(i).getArrayH(), 7);
                    }
                }
                if ((inter.getMinY() >= t1.getRect().getMinY()) && (inter.getHeight() <= inter.getWidth())) {  // going down, push back up..
                    int newY = (int) inter.getHeight() * -1;
                    obj.changeY(newY);
                    if (obj instanceof Player) {
                        t1.toggleJump(true);
                        t1.setJumpCount(0);
                    }
                }
            }

            for (int j = 0; j < t1.getBulletList().size(); j++) {           //bullet into wall
                if (t1.getBulletList().get(j).getRect().intersects(allWalls.get(i).getRect())) {
                    t1.getBulletList().remove(j);
                }
                for (int f = 0; f < mobs.size(); f++) {     //bullet into mobs
                    if (!mobs.isEmpty() && !t1.getBulletList().isEmpty() && mobs.get(f).getRect().intersects(t1.getBulletList().get(j).getRect())) {
                        t1.getBulletList().remove(j);
                        mobs.remove(f);
                    }
                }
            }

            if (!bosses.isEmpty()) {
                for (int v = 0; v < bosses.get(0).getBulletList().size(); v++) {  //boss bullet into wall
                    if (bosses.get(0).getBulletList().get(v).getRect().intersects(allWalls.get(i).getRect())) {
                        bosses.get(0).getBulletList().remove(v);
                    }
                }
            }
        }
    }

    public void mapChange(int arrayWidth, int arrayHeight, int change) {     //remove tile from map
        map.get(arrayHeight).set(arrayWidth, change);
    }

    private void printBullets() {
        for (int i = 0; i < t1.getBulletList().size(); i++) {
            t1.getBulletList().get(i).drawImage(buffer);
        }
        if (!bosses.isEmpty()) {
            for (int i = 0; i < bosses.get(0).getBulletList().size(); i++) {
                bosses.get(0).getBulletList().get(i).drawImage(buffer);
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {

        if ((t1.getHEALTH() == 0 && t1.getLives() == 0)) {
            g.setColor(Color.white);
            g.setFont(new Font("TimesRoman", Font.PLAIN, 50));
            g.drawString("GAME OVER", SCREEN_WIDTH / 2 - 125, SCREEN_HEIGHT / 2);
        } else if (!level1 && !bossCheck && bosses.isEmpty()) {
            g.setColor(Color.white);
            g.setFont(new Font("TimesRoman", Font.PLAIN, 50));
            g.drawString("VICTORY", SCREEN_WIDTH / 2 - 125, SCREEN_HEIGHT / 2);
        } else {
            Graphics2D g2 = (Graphics2D) g;
            g2.scale(1.5, 1.5);
            buffer = world.createGraphics();
            super.paintComponent(g2);
            collisionCheck(t1);

            showBullets(t1);
            if(!bosses.isEmpty()){
                showBullets(bosses.get(0));
            }

            drawBackGround();
            mapUpdate();
            this.t1.drawImage(buffer);
            for (int i = 0; i < mobs.size(); i++) {
                mobs.get(i).update();
                collisionCheck(mobs.get(i));
                if (!mobs.isEmpty() && mobs.get(i).checkBorder() == true) {
                    mobs.remove(i);
                }
            }
            for (int k = 0; k < allPowers.size(); k++) {
                allPowers.get(k).drawImage(buffer);
            }
            printBullets();
            g2.drawImage(t1.updateCam(world), 0, 0, null);                         //map for t1

            if (!bosses.isEmpty() && bosses.get(0).getHEALTH() == 0) {

                mapChange(bosses.get(0).getTileX(), bosses.get(0).getTileY(), 0);
                bosses.clear();
                if (!level1) {
                    bossCheck = false;
                }
            }

            if (t1.getY() >= 565) {
                bosses.clear();
                mobs.clear();
                allPowers.clear();
                t1.getBulletList().clear();
                readMap("resources/map2.txt");
                level1 = false;
                bg = bg2;
                t1.setY(20);
                t1.setX(20);
                t1.isFalling();
            }

            if (!bosses.isEmpty()) {
                if (!first || t1.getX() >= 1960) {
                    g2.setColor(Color.red);
                    g2.fillRect(40, 50, bosses.get(0).getHEALTH() * 2, 20);
                    bosses.get(0).shoot(t1.getX() - t1img.getWidth() / 2 + 16, t1.getY() - t1img.getHeight() / 2);
                    first = false;
                    t1.setSpawn(1960, 500);
                }
            }

            for (int i = 0; i < t1.getLives(); i++) {
                g2.setColor(Color.RED);
                g2.drawOval(30 * (i + 1), 10, 20, 20);
                g2.fillOval(30 * (i + 1), 10, 20, 20);
            }
        }
    }

}
