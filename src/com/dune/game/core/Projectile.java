package com.dune.game.core;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Projectile {
    private Vector2 position;
    private Vector2 velocity;
    private TextureRegion[] textures;
    public Vector2 getPosition() {
        return position;
    }
    public TextureRegion getTexture() {return textures[0];}
    private boolean init = false;



    public boolean getInit(){
        return init;

    }


    public Projectile(TextureAtlas atlas, Vector2 position){
        this.textures = new TextureRegion(atlas.findRegion("bullet")).split(16, 16)[0];
        this.position = new Vector2();
        this.velocity = new Vector2();
        this.position.set(position);
    }

    public void setup(Vector2 startPosition, float angle) {
        velocity.set(100.0f * MathUtils.cosDeg(angle), 100.0f * MathUtils.sinDeg(angle));
        position.set(startPosition.x - 8+(40.0f * MathUtils.cosDeg(angle)),startPosition.y - 8 + (40.0f * MathUtils.sinDeg(angle)));
        init = true;
    }

    public void update(float dt) {
        position.mulAdd(velocity, dt);
        if(position.x < 0 || position.x > 1280 || position.y < 0 || position.y > 720) {
            init = false;
        }
    }

}
