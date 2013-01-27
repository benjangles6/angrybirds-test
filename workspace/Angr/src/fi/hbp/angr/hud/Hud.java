package fi.hbp.angr.hud;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import fi.hbp.angr.G;

/**
 * Drawable HUD
 */
public class Hud {
    private BitmapFont font = new BitmapFont();
    private SpriteBatch hudBatch;
    private ArrayList<HudActor> actors = new ArrayList<HudActor>();

    /**
     * Constructor for Hud.
     */
    public Hud() {
        hudBatch = new SpriteBatch();
        font.setColor(Color.RED);
        font.setScale(1.0f);
    }

    /**
     * Adds the specified HudActor to the list of actors that will
     * be drawn on the screen.
     * @param actor element that is added to the list.
     */
    public void addActor(HudActor actor) {
        actors.add(actor);
    }

    /**
     * Draw this HUD.
     */
    public void draw() {
        hudBatch.begin();
        if (G.DEBUG)
            font.draw(hudBatch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 10, 30);
        for (HudActor actor : actors) {
            actor.draw(hudBatch);
        }
        hudBatch.end();
    }
}