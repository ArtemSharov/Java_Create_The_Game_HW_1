package com.dune.game.core;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class TanksController extends ObjectPool<Tank> {
    private GameController gc;

    @Override
    protected Tank newObject() {
        return new Tank(gc);
    }

    public TanksController(GameController gc) {
        this.gc = gc;
    }

    public void render(SpriteBatch batch) {
        for (int i = 0; i < activeList.size(); i++) {
            activeList.get(i).render(batch);
        }
    }

    public void setup(float x, float y, Tank.Owner ownerType) {
        Tank t = getActiveElement();
        t.setup(ownerType, x, y);
    }

    public void update(float dt) {
        checkCollision();
        for (int i = 0; i < activeList.size(); i++) {
            getActiveList().get(i).selected();

            activeList.get(i).update(dt);}


        checkPool();

    }
    public void checkCollision(){
        for (int i = 0; i < activeList.size(); i++) {
            for (int j = 0; j < activeList.size(); j++) {
                if (i != j) {
                    if ((getActiveList().get(i).position.x + 80) >= (getActiveList().get(j).position.x)
                            && (getActiveList().get(i).position.x <= (getActiveList().get(j).position.x + 80))
                            && (getActiveList().get(i).position.y + 80) >= (getActiveList().get(j).position.y)
                            && (getActiveList().get(i).position.y <= (getActiveList().get(j).position.y + 80))) {
                        getActiveList().get(i).stopMove();
                    }
                }
            }
        }
    }
}
