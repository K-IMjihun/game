package com.loopsurvivors.world;

public class Enemy {

    public float x, y;
    public float hp;
    public final float maxHp;
    public final float speed;
    public boolean alive = true;

    public Enemy(float x, float y, float hp, float speed) {
        this.x = x; this.y = y;
        this.hp = hp; this.maxHp = hp;
        this.speed = speed;
    }

    public void tick(World world) {
        // Ghost를 타겟으로 삼지 않고 현재 플레이어만 추적
        Player player = world.getPlayer();
        if (!player.alive) return;

        float dx = player.x - x;
        float dy = player.y - y;
        float dist = (float) Math.sqrt(dx * dx + dy * dy);
        if (dist > 0) {
            x += (dx / dist) * speed / 60f;
            y += (dy / dist) * speed / 60f;
        }

        // 접촉 피해
        if (dist < 20f) {
            float roll = (float) Math.random();
            if (roll >= world.getBonusSet().hitIgnoreChance) {
                player.hp -= 10f / 60f;
            }
            if (player.hp <= 0) player.alive = false;
        }
    }

    public void takeDamage(float damage) {
        hp -= damage;
        if (hp <= 0) alive = false;
    }
}
