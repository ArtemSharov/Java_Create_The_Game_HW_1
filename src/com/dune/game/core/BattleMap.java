package com.dune.game.core;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import java.util.Random;

import static com.badlogic.gdx.math.MathUtils.random;

public class BattleMap {
    private TextureRegion grassTexture;
    private TextureRegion fuelTexture;
    private int fuel = 1;
    private int grass =0;
    private int[][] arrTextures = new int[16][9];


    public BattleMap() {

        this.grassTexture = Assets.getInstance().getAtlas().findRegion("grass");
        this.fuelTexture = Assets.getInstance().getAtlas().findRegion("fuel");
        generateMap();

    }
    public void generateMap() {
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 9; j++) {
                if(random.nextInt(10) == 1){
                    arrTextures[i][j] = fuel;
                }
            }
        }
    }
    public void fuelCheck(Vector2 position) {
        int posX = (int) position.x / 80;
        int posY = (int) position.y / 80;
        if (arrTextures[posX][posY] == fuel) {
            arrTextures[posX][posY] = grass;
        }
    }



    public void render(SpriteBatch batch) {
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 9; j++) {
                if (arrTextures[i][j] == fuel){
                    batch.draw(fuelTexture, i * 80, j * 80);
                }else{
                    batch.draw(grassTexture, i * 80, j * 80);
            }
        }

            }

        }
    }

