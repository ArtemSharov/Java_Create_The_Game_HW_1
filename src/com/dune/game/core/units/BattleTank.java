package com.dune.game.core.units;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.dune.game.core.Assets;
import com.dune.game.core.GameController;
import com.dune.game.core.Targetable;
import com.dune.game.core.Weapon;

// класс боевого танка, наследуется от AbstractUnit реализует боевой танк со своими свойствами

public class BattleTank extends AbstractUnit {
    public BattleTank(GameController gc) { // конструктор боевого танка
        super(gc);
        this.textures = Assets.getInstance().getAtlas().findRegion("tankcore").split(64, 64)[0]; // картинка танка
        this.weaponTexture = Assets.getInstance().getAtlas().findRegion("turret"); //картинка оружия
        this.minDstToActiveTarget = 240.0f; // расстояние до цели
        this.speed = 120.0f; //скорость
        this.hpMax = 100; // здоровье танка
        this.weapon = new Weapon(1.5f, 1); // оружие
        this.containerCapacity = 50; // вместимость контейнера
        this.unitType = UnitType.BATTLE_TANK; // определение типа танка, как боевой танк
    }

    @Override
    public void setup(Owner ownerType, float x, float y) { // отпределение абстрактного метода класса родителя
        this.position.set(x, y);
        this.ownerType = ownerType;
        this.hp = this.hpMax;
        this.destination = new Vector2(position);
    }

    public void updateWeapon(float dt) { // мотод обновления оружия
        if (target != null) {
            if (!((AbstractUnit) target).isActive()) {
                target = null;
                return;
            }
            float angleTo = tmp.set(target.getPosition()).sub(position).angle();
            weapon.setAngle(rotateTo(weapon.getAngle(), angleTo, 180.0f, dt));
            int power = weapon.use(dt);
            if (power > -1) {
                gc.getProjectilesController().setup(this, position, weapon.getAngle());
            }
        }
        if (target == null) {
            weapon.setAngle(rotateTo(weapon.getAngle(), angle, 180.0f, dt));
        }
    }

    @Override
    public void commandAttack(Targetable target) { // реализация метода родителя, который задает цель для атаки
        if (target.getType() == TargetType.UNIT && ((AbstractUnit) target).getOwnerType() != this.ownerType) {
            this.target = target;
        } else {
            commandMoveTo(target.getPosition());
        }
    }

    @Override
    public void renderGui(SpriteBatch batch) {
        super.renderGui(batch);
    }
}