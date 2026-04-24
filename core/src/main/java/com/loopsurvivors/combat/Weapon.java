package com.loopsurvivors.combat;

import com.loopsurvivors.world.Enemy;
import com.loopsurvivors.world.Projectile;
import com.loopsurvivors.world.World;

public abstract class Weapon {

    public abstract void attack(float x, float y, World world);

    /** 정지 모드: 사거리 내 가장 가까운 적에게만 공격 */
    public void attackNearest(float x, float y, World world) {
        Enemy nearest = null;
        float minDist = 80f;
        for (Enemy e : world.getEnemies()) {
            if (!e.alive) continue;
            float d = dst(x, y, e.x, e.y);
            if (d < minDist) { minDist = d; nearest = e; }
        }
        if (nearest != null) nearest.takeDamage(8f * world.getBonusSet().damageMultiplier);
    }

    protected float dst(float x1, float y1, float x2, float y2) {
        float dx = x2 - x1, dy = y2 - y1;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    public static Weapon forClass(ClassType classType) {
        return switch (classType) {
            case SWORD -> new SwordWeapon();
            case BOW   -> new BowWeapon();
            case HEAL  -> new HealWeapon();
        };
    }

    // ── 직업별 구현 ──────────────────────────────────────────

    public static class SwordWeapon extends Weapon {
        private static final float RANGE  = 60f;
        private static final float DAMAGE = 15f;

        @Override
        public void attack(float x, float y, World world) {
            float mult = world.getBonusSet().damageMultiplier;
            for (Enemy e : world.getEnemies()) {
                if (!e.alive) continue;
                if (dst(x, y, e.x, e.y) < RANGE) e.takeDamage(DAMAGE * mult);
            }
        }
    }

    public static class BowWeapon extends Weapon {
        private static final float SPEED  = 400f;
        private static final float DAMAGE = 10f;

        @Override
        public void attack(float x, float y, World world) {
            Enemy target = null;
            float minDist = Float.MAX_VALUE;
            for (Enemy e : world.getEnemies()) {
                if (!e.alive) continue;
                float d = dst(x, y, e.x, e.y);
                if (d < minDist) { minDist = d; target = e; }
            }
            if (target == null) return;

            float dx = target.x - x, dy = target.y - y;
            float d = dst(x, y, target.x, target.y);
            int pierce = world.getBonusSet().projectilePiercing;
            float mult  = world.getBonusSet().damageMultiplier;
            world.addProjectile(new Projectile(x, y, dx / d * SPEED, dy / d * SPEED, DAMAGE * mult, pierce));
        }
    }

    /** HEAL은 MVP에서 버프 오라로 기능. 직접 공격 없음 — FormationSynergy가 보너스 적용. */
    public static class HealWeapon extends Weapon {
        @Override
        public void attack(float x, float y, World world) {}
    }
}
