package com.dune.game.core;

import com.badlogic.gdx.math.Vector2;
import com.dune.game.core.units.AbstractUnit;
import com.dune.game.core.units.BattleTank;

import java.util.List;

public class Collider {
    private GameController gc;
    private Vector2 tmp;

    public Collider(GameController gc) {
        this.gc = gc;
        this.tmp = new Vector2();
    }

    public void checkCollisions() {
        List<AbstractUnit> units = gc.getUnitsController().getUnits();
        for (int i = 0; i < units.size() - 1; i++) {
            AbstractUnit u1 = units.get(i);
            for (int j = i + 1; j < units.size(); j++) {
                AbstractUnit u2 = units.get(j);
                float dst = u1.getPosition().dst(u2.getPosition());
                if (dst < 30 + 30) {
                    float colLengthD2 = (60 - dst) / 2;
                    tmp.set(u2.getPosition()).sub(u1.getPosition()).nor().scl(colLengthD2);
                    u2.moveBy(tmp);
                    tmp.scl(-1);
                    u1.moveBy(tmp);
                }
            }
        }
        for (int i = 0; i < gc.getProjectilesController().activeSize(); i++) {
            Projectile p = gc.getProjectilesController().getActiveList().get(i);
            for (int j = 0; j < gc.getUnitsController().getUnits().size(); j++) {
                AbstractUnit u = gc.getUnitsController().getUnits().get(j);
                if (p.getOwner() != u && p.getPosition().dst(u.getPosition()) < 30) {
                    p.deactivate();
                    u.takeDamage(5);
                }
            }
        }
    }


}
