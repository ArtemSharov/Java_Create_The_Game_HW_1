package com.dune.game.core.units;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dune.game.core.*;

public abstract class AbstractUnit extends GameObject implements Poolable, Targetable {
    protected UnitType unitType;
    protected Owner ownerType;
    protected Weapon weapon;

    protected Vector2 destination;
    protected TextureRegion[] textures;
    protected TextureRegion weaponTexture;

    protected TextureRegion progressbarTexture;
    protected int hp;
    protected int hpMax;
    protected float angle;
    protected float speed;
    protected float rotationSpeed;

    protected float moveTimer;
    protected float lifeTime;
    protected float timePerFrame;
    protected int container;
    protected int containerCapacity;

    protected Targetable target;
    protected float minDstToActiveTarget;

    @Override
    public TargetType getType() {
        return TargetType.UNIT;
    }

    public boolean takeDamage(int damage) {
        if (!isActive()) {
            return false;
        }
        hp -= damage;
        if (hp <= 0) {
            return true;
        }
        return false;
    }

    public UnitType getUnitType() {
        return unitType;
    }

    public Weapon getWeapon() {
        return weapon;
    }

    public void moveBy(Vector2 value) {
        boolean stayStill = false;
        if (position.dst(destination) < 3.0f) {
            stayStill = true;
        }
        position.add(value);
        if (stayStill) {
            destination.set(position);
        }
    }

    public Owner getOwnerType() {
        return ownerType;
    }

    @Override
    public boolean isActive() {
        return hp > 0;
    }

    public AbstractUnit(GameController gc) {
        super(gc);
        this.progressbarTexture = Assets.getInstance().getAtlas().findRegion("progressbar");
        this.timePerFrame = 0.08f;
        this.rotationSpeed = 90.0f;
    }

    public abstract void setup(Owner ownerType, float x, float y);

    private int getCurrentFrameIndex() {
        return (int) (moveTimer / timePerFrame) % textures.length;
    }

    public void update(float dt) {
        lifeTime += dt;
        // Если у танка есть цель, он пытается ее атаковать
        if (target != null) {
            destination.set(target.getPosition());
            if (position.dst(target.getPosition()) < minDstToActiveTarget) {
                destination.set(position);
            }
        }
        // Если танку необходимо доехать до какой-то точки, он работает в этом условии
        if (position.dst(destination) > 3.0f) {
            float angleTo = tmp.set(destination).sub(position).angle();
            angle = rotateTo(angle, angleTo, rotationSpeed, dt);
            moveTimer += dt;
            tmp.set(speed, 0).rotate(angle);
            position.mulAdd(tmp, dt);
            if (position.dst(destination) < 120.0f && Math.abs(angleTo - angle) > 10) {
                position.mulAdd(tmp, -dt);
            }
        }
        updateWeapon(dt);
        checkBounds();
    }

    public void commandMoveTo(Vector2 point) {
        destination.set(point);
        target = null;
    }

    public abstract void commandAttack(Targetable target);

    public abstract void updateWeapon(float dt);

    public void checkBounds() {
        if (position.x < 40) {
            position.x = 40;
        }
        if (position.y < 40) {
            position.y = 40;
        }
        if (position.x > 1240) {
            position.x = 1240;
        }
        if (position.y > 680) {
            position.y = 680;
        }
    }

    public void render(SpriteBatch batch) {
        float c = 1.0f;
        float r = 0.0f;
        if (gc.isUnitSelected(this)) {
            c = 0.7f + (float) Math.sin(lifeTime * 8.0f) * 0.3f;
        }
        if (ownerType == Owner.AI) {
            r = 0.4f;
        }
        batch.setColor(c, c - r, c - r, 1.0f);
        batch.draw(textures[getCurrentFrameIndex()], position.x - 40, position.y - 40, 40, 40, 80, 80, 1, 1, angle);

        batch.draw(weaponTexture, position.x - 40, position.y - 40, 40, 40, 80, 80, 1, 1, weapon.getAngle());

        batch.setColor(1, 1, 1, 1);
        renderGui(batch);
    }

    public void renderGui(SpriteBatch batch) {
        if (hp < hpMax) {
            batch.setColor(0.2f, 0.2f, 0.0f, 1.0f);
            batch.draw(progressbarTexture, position.x - 32, position.y + 30, 64, 12);
            batch.setColor(0.0f, 1.0f, 0.0f, 1.0f);
            float percentage = (float) hp / hpMax;
            batch.draw(progressbarTexture, position.x - 30, position.y + 32, 60 * percentage, 8);
            batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        }
    }

    public float rotateTo(float srcAngle, float angleTo, float rSpeed, float dt) {
        if (Math.abs(srcAngle - angleTo) > 3.0f) {
            if ((srcAngle > angleTo && Math.abs(srcAngle - angleTo) <= 180.0f) || (srcAngle < angleTo && Math.abs(srcAngle - angleTo) > 180.0f)) {
                srcAngle -= rSpeed * dt;
            } else {
                srcAngle += rSpeed * dt;
            }
        }
        if (srcAngle < 0.0f) {
            srcAngle += 360.0f;
        }
        if (srcAngle > 360.0f) {
            srcAngle -= 360.0f;
        }
        return srcAngle;
    }
}