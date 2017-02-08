package battle;

import asteroids.GameState;
import asteroids.Ship;
import math.Vector2d;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.Random;

import static asteroids.Constants.*;
import static java.awt.Color.black;
import static battle.BattleGameParameters.*;

public class BattleView extends JComponent {
    public static boolean trace = false;
    static int offset = 0;
    int scale;
    // static int carSize = 5;
    SimpleBattleState game;

    static Color[] shipColors = {Color.blue,
            Color.green};
    // Font font;

    static Random rand = new Random();

    static double viewScale = 1.0;

    public static Color randColor() {
        // Color color = Color.getHSBColor(rand.nextFloat(), 1, 1);
        Color color = Color.getHSBColor(0.9f, 1, 1);

        return color;
    }

    static Color bg = randColor();

    public BattleView(SimpleBattleState game) {
        this.game = game;
        scale = size.width - 2 * offset;
    }

    public void paintComponent(Graphics gx) {
        Graphics2D g = (Graphics2D) gx;
        g.setColor(bg);
        g.fillRect(0, 0, size.width, size.height);
        AffineTransform at = g.getTransform();
        g.translate((1 - viewScale) * width / 2, (1-viewScale)*height / 2);


        // this was an experiment to turn it into a side-scroller
        // but it produces a weird moving screen effect
        // needs more logic in the drawing process
        // to wrap the asteroids that have been projected off the screen
        // g.translate(-(game.ship.s.x - width/2), 0);

        g.scale(viewScale, viewScale);

        drawGame(g);
        g.setTransform(at);
        paintState(g);

//        g.setFont(font);
//        g.drawString("Hello", 100, 100);
    }

    static Vector2d tmp = new Vector2d();
    private void drawGame(Graphics2D g) {
        // draw the damage radius before the ship, so that each ship can be seen over the top of it
        for (int i=0; i<game.ships.length; i++) {
            SimpleShip ss = game.ships[i];
            g.setColor(shipColors[i]);
            tmp.set(ss.s);
            tmp.add(ss.d, SimpleBattleState.projection);
            // now do a fill-oval
            int r = (int) damageRadius;
            g.fillOval((int) tmp.x-r, (int) tmp.y-r, 2*r, 2*r);
        }
        // now the ships
        for (int i=0; i<game.ships.length; i++) {
            SimpleShip ss = game.ships[i];
            g.setColor(shipColors[i]);
            ss.draw(g);
        }
    }


    public void paintState(Graphics2D g) {
        g.setColor(Color.black);
        g.setFont(font);
        String str = "" + game.score[0] + " : " + game.score[1] + " : " + game.nTicks;
        // FontMetrics fm = font.
        g.drawString(str, 10, 20);

    }

    public Dimension getPreferredSize() {
        return size;
    }


}
