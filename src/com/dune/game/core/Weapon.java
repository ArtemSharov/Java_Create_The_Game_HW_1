package com.dune.game.core;

public class Weapon {
    public enum Type {
        GROUND(0), HARVEST(1), AIR(2);

        public int getImageIndex() {
            return imageIndex;
        }

        int imageIndex;

        Type(int imageIndex) {
            this.imageIndex = imageIndex;
        }
    }

    private Type type;
    private float period;
    private float time;
    private float angle;
    private int power;

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public Type getType() {
        return type;
    }

    public float getUsageTimePercentage() {
        return time / period;
    }

    public Weapon(Type type, float period, int power) {
        this.type = type;
        this.period = period;
        this.power = power;
    }

    public void reset() {
        time = 0.0f;
    }

    public int use(float dt) {
        time += dt;
        if (time > period) {
            time = 0.0f;
            return power;
        }
        return -1;
    }
}
