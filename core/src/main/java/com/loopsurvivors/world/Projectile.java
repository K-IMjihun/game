package com.loopsurvivors.world;

public class Projectile {

    public float x, y;
    public final float vx, vy;
    public final float damage;
    public int piercing;
    public boolean active = true;

    public Projectile(float x, float y, float vx, float vy, float damage, int piercing) {
        this.x = x; this.y = y;
        this.vx = vx; this.vy = vy;
        this.damage = damage;
        this.piercing = piercing;
    }

    public void tick(World world) {
        x += vx / 60f;
        y += vy / 60f;

        if (x < -100 || x > 1380 || y < -100 || y > 820) {
            active = false;
            return;
        }

        for (Enemy e : world.getEnemies()) {
            if (!e.alive) continue;
            float dx = e.x - x, dy = e.y - y;
            if (Math.sqrt(dx * dx + dy * dy) < 16f) {
                e.takeDamage(damage);
                if (--piercing < 0) { active = false; return; }
            }
        }
    }
}
