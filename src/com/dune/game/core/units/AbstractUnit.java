package com.dune.game.core.units;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dune.game.core.*;

// Абстрактный класс, который описывает типовые свойства юнитов в игре, например, сборщик, боевой танк.
// Наследуется от класса GameObject и реализует интерфейсы Poolable, Targetable

public abstract class AbstractUnit extends GameObject implements Poolable, Targetable {
    protected UnitType unitType; //тип юнита
    protected Owner ownerType;  // тип управления Игрок или Бот
    protected Weapon weapon; // тип оружия

    protected Vector2 destination; // вектор растояния
    protected TextureRegion[] textures; // текстура юнита
    protected TextureRegion weaponTexture; // текстура оружия

    protected TextureRegion progressbarTexture; // текстура прогресс бара
    protected int hp; // здоровье юнита
    protected int hpMax; // максимальное здоровье
    protected float angle; // угол
    protected float speed; // скорость
    protected float rotationSpeed; // скорость поворота

    protected float moveTimer; //время движения
    protected float lifeTime; //время существования
    protected float timePerFrame;
    protected int container; //
    protected int containerCapacity;

    protected Targetable target; // цель юнита
    protected float minDstToActiveTarget; // минимальное расстояние до заданной цели

    @Override
    public TargetType getType() {
        return TargetType.UNIT;
    } // переопределение метода интерфейса Targetable, возвращает тип UNIT

    public boolean takeDamage(int damage) { // метод определяющий получение урона, с входящем параметром урона,
        // если юнит не активен, то возвращает false, уменьшает здоровье юнита на количество урона, если здоровье меньше 0, то возвращает true,
        // иначе false
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
    } // геттер на тип юнита

    public Weapon getWeapon() {
        return weapon;
    } // геттер на тип оружия

    public void moveBy(Vector2 value) { // метод дижения с входящем параметром в виде вектора назначения
        // не совсем понимаю реализацию, если расстояние от позиции до
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
    } //геттер на того кто управляет танком

    @Override
    public boolean isActive() {
        return hp > 0;
    } // реализация метода интерфейса Poolable, юнит активен, пока здоровье больше 0

    public AbstractUnit(GameController gc) { // конструктор
        super(gc);
        this.progressbarTexture = Assets.getInstance().getAtlas().findRegion("progressbar");
        this.timePerFrame = 0.08f;
        this.rotationSpeed = 90.0f;
    }

    public abstract void setup(Owner ownerType, float x, float y); // абстрактный метод сетап, с входными параметрами владельца и координатами х и у

    private int getCurrentFrameIndex() {
        return (int) (moveTimer / timePerFrame) % textures.length;
    }

    public void update(float dt) { // метод по которому юнит обновляется  с параметром дельты времени
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
        updateWeapon(dt); // обновляет оружие
        checkBounds(); // контроль выхода за окно
    }

    public void commandMoveTo(Vector2 point) { // команда двигаться к точке, метод обнуляет цель
        destination.set(point);
        target = null;
    }

    public abstract void commandAttack(Targetable target); //абстрактный метод атаки

    public abstract void updateWeapon(float dt); // абстрактный метод обновления оружия

    public void checkBounds() { // контроль выхода за окно
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

    public void render(SpriteBatch batch) { // метод отрисовки юнита
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

    public void renderGui(SpriteBatch batch) { // метод, который отрисовывает шкалу здоровья
        if (hp < hpMax) {
            batch.setColor(0.2f, 0.2f, 0.0f, 1.0f);
            batch.draw(progressbarTexture, position.x - 32, position.y + 30, 64, 12);
            batch.setColor(0.0f, 1.0f, 0.0f, 1.0f);
            float percentage = (float) hp / hpMax;
            batch.draw(progressbarTexture, position.x - 30, position.y + 32, 60 * percentage, 8);
            batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        }
    }

    public float rotateTo(float srcAngle, float angleTo, float rSpeed, float dt) { // метод поворота
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