package adventurergame;

import java.awt.*;
import java.util.ArrayList;

public abstract class Moving {
    public void update(){}
    public void moveLeft(){}
    public void gravity(){}
    public void playerRun(){}
    public Rectangle getRect(){ return null; }
    public int getHEALTH(){ return 0; }
    public void changeX(int x){}
    public void changeY(int y){}
    public ArrayList<Bullet> getBulletList(){return null;}

}
