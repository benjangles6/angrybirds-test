package fi.hbp.angr.screens;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;

import fi.hbp.angr.G;
import fi.hbp.angr.GameStage;
import fi.hbp.angr.ItemDestructor;
import fi.hbp.angr.Preloadable;
import fi.hbp.angr.logic.ModelContactListener;
import fi.hbp.angr.logic.ScoreCounter;
import fi.hbp.angr.models.BodyFactory;
import fi.hbp.angr.models.Destructible;
import fi.hbp.angr.models.Hud;
import fi.hbp.angr.models.Level;
import fi.hbp.angr.models.items.Grenade;

public class GameScreen implements Screen, Preloadable {
    private InputMultiplexer inputMultiplexer = new InputMultiplexer();
    private World world;
    private Stage stage;
    private String levelName;
    private BodyFactory bdf;
    ItemDestructor itdes = new ItemDestructor();
    private ScoreCounter score = new ScoreCounter();
    private Hud hud = new Hud();

    public GameScreen(String levelName) {
        this.levelName = levelName;
        score.loadAssets();
        hud.addActor(score);
    }

    @Override
    public void preload() {
        G.getAssetManager().load("data/" + levelName + ".png", Texture.class);
        BodyFactory.preload();
    }

    @Override
    public void unload() {
        // TODO Iterate and remove from list
        G.getAssetManager().unload("data/" + levelName + ".png");
    }

    @Override
    public void render(float delta) {
        /* Remove destroyed actors/items and add points to a score counter. */
        if (!itdes.isEmpty()) {
            Iterator<Actor> it = itdes.getIterator();
            while(it.hasNext()) {
                Actor a = it.next();
                score.addPoints(((Destructible)a).getDatamageModel().getPoints());
                ((Destructible)a).getBody().setUserData(null);
                world.destroyBody(((Destructible)a).getBody());
                a.remove();
            }
            itdes.clear();
        }

        world.step(delta, 30, 30);
        Gdx.gl.glClearColor(0, 0.75f, 1, 1);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
        hud.draw();
    }

    @Override
    public void resize(int width, int height) {
        // TODO Auto-generated method stub
    }

    @Override
    public void show() {
        world = new World(new Vector2(0, -8), true);
        int xsize = Gdx.graphics.getWidth() * 4;
        int ysize = Gdx.graphics.getHeight() * 4;
        stage = new GameStage(xsize, ysize, false, world);
        Gdx.input.setInputProcessor(inputMultiplexer);
        inputMultiplexer.addProcessor(stage);

        ModelContactListener mcl = new ModelContactListener();
        world.setContactListener(mcl);
        bdf = new BodyFactory(inputMultiplexer, itdes);

        // Add map/level actor
        Level level = new Level(levelName, world);
        score.clear();
        score.init(level.getHighScore(), level.getStarScale());
        stage.addActor(level);

        for (int i = 0; i < 10; i++) {
            Actor box = bdf.createBox(world, 1000 + i * 40, 800, 0);
            stage.addActor(box);
        }

        // Add player
        Actor grenade = bdf.createGrenade(stage, world, 1000, 1500, 0);
        stage.addActor(grenade);

        for (int i = 0; i < 11; i++) {
            Actor grenade2 = bdf.createGrenade(stage, world, 1000 + i * 110, 1000, 90);
            ((Grenade)grenade2).body.setLinearVelocity(new Vector2(0, 100));
            ((Grenade)grenade2).body.applyAngularImpulse(50);
            stage.addActor(grenade2);
        }
    }

    @Override
    public void hide() {
        // TODO Auto-generated method stub
    }

    @Override
    public void pause() {
        // TODO Auto-generated method stub
    }

    @Override
    public void resume() {
        // TODO Auto-generated method stub
    }

    @Override
    public void dispose() {
        stage.dispose();
        this.unload();
    }
}