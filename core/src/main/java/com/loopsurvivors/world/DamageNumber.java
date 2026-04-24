package com.loopsurvivors.world;

public class DamageNumber {

    public float x, y;
    public final String text;
    public float lifetime = 1.0f;   // 남은 표시 시간 (초)
    public boolean active = true;

    private static final float RISE_SPEED = 45f;  // 위로 올라가는 속도 (px/s)

    public DamageNumber(float x, float y, float damage) {
        this.x = x;
        this.y = y;
        this.text = String.valueOf((int) Math.ceil(damage));
    }

    public void tick() {
        y += RISE_SPEED / 60f;
        lifetime -= 1f / 60f;
        if (lifetime <= 0f) active = false;
    }

    /** 0~1 범위의 현재 불투명도 (끝으로 갈수록 희미해짐) */
    public float alpha() {
        return Math.max(0f, lifetime);
    }
}
