package com.dune.game.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class WorldRenderer {
    private SpriteBatch batch;
    private BitmapFont font32;
    private GameController gc;

    public WorldRenderer(SpriteBatch batch, GameController gc) {
        this.batch = batch;
        this.font32 = Assets.getInstance().getAssetManager().get("fonts/font32.ttf");
        this.gc = gc;
    }

    public void render() {
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        gc.getMap().render(batch);
        gc.getTanksController().render(batch);
        gc.getProjectilesController().render(batch);
        font32.draw(batch, "Dune Game 2020", 0, 680, 1280, 1, false);
        batch.end();
    }
}
