package com.dune.game.core;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class BattleMap {
    private class Cell {
        private int cellX, cellY;
        private int resource;
        private float resourceRegenerationRate;
        private float resourceRegenerationTime;

        public Cell(int cellX, int cellY) {
            this.cellX = cellX;
            this.cellY = cellY;
            if(MathUtils.random() < 0.1f) {
                resource = MathUtils.random(1, 3);
            }
            resourceRegenerationRate = MathUtils.random(5.0f) - 4.5f;
            if (resourceRegenerationRate < 0.0f) {
                resourceRegenerationRate = 0.0f;
            } else {
                resourceRegenerationRate *= 20.0f;
                resourceRegenerationRate += 10.0f;
            }
        }

        private void update(float dt) {
            if (resourceRegenerationRate > 0.01f) {
                resourceRegenerationTime += dt;
                if (resourceRegenerationTime > resourceRegenerationRate) {
                    resourceRegenerationTime = 0.0f;
                    resource++;
                    if (resource > 5) {
                        resource = 5;
                    }
                }
            }
        }

        private void render(SpriteBatch batch) {
            if (resource > 0) {
                float scale = 0.5f + resource * 0.2f;
                batch.draw(resourceTexture, cellX * 80, cellY * 80, 40, 40, 80, 80, scale, scale, 0.0f);
            } else {
                if (resourceRegenerationRate > 0.01f) {
                    batch.draw(resourceTexture, cellX * 80, cellY * 80, 40, 40, 80, 80, 0.1f, 0.1f, 0.0f);
                }
            }
        }
    }

    public static final int COLUMNS_COUNT = 16;
    public static final int ROWS_COUNT = 9;
    public static final int CELL_SIZE = 80;

    private TextureRegion grassTexture;
    private TextureRegion resourceTexture;
    private Cell[][] cells;

    public BattleMap() {
        this.grassTexture = Assets.getInstance().getAtlas().findRegion("grass");
        this.resourceTexture = Assets.getInstance().getAtlas().findRegion("resource");
        this.cells = new Cell[COLUMNS_COUNT][ROWS_COUNT];
        for (int i = 0; i < COLUMNS_COUNT; i++) {
            for (int j = 0; j < ROWS_COUNT; j++) {
                cells[i][j] = new Cell(i, j);
            }
        }
    }

    public int getResourceCount(Tank harvester) {
        return cells[harvester.getCellX()][harvester.getCellY()].resource;
    }

    public int harvestResource(Tank harvester, int power) {
        int value = 0;
        if(cells[harvester.getCellX()][harvester.getCellY()].resource >= power) {
            value = power;
            cells[harvester.getCellX()][harvester.getCellY()].resource -= power;
        } else {
            value = cells[harvester.getCellX()][harvester.getCellY()].resource;
            cells[harvester.getCellX()][harvester.getCellY()].resource = 0;
        }
        return value;
    }

    public void render(SpriteBatch batch) {
        for (int i = 0; i < COLUMNS_COUNT; i++) {
            for (int j = 0; j < ROWS_COUNT; j++) {
                batch.draw(grassTexture, i * 80, j * 80);
                cells[i][j].render(batch);
            }
        }
    }

    public void update(float dt) {
        for (int i = 0; i < COLUMNS_COUNT; i++) {
            for (int j = 0; j < ROWS_COUNT; j++) {
                cells[i][j].update(dt);
            }
        }
    }
}
