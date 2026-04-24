package com.loopsurvivors.combat;

import com.badlogic.gdx.math.MathUtils;
import com.loopsurvivors.loop.InputFrame;
import com.loopsurvivors.world.Enemy;
import com.loopsurvivors.world.Projectile;
import com.loopsurvivors.world.World;

public abstract class Weapon {

    /** 매 틱 호출. input이 null이면 입력 없는 상태(Ghost 정지 모드). */
    public abstract void tick(float x, float y, World world, InputFrame input);

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

    // ── SWORD: 플레이어 주위를 도는 궤도 검 ─────────────────────

    public static class SwordWeapon extends Weapon {

        private static final float ORBIT_RADIUS  = 55f;   // 플레이어 중심~검 거리 (px)
        private static final float ORBIT_SPEED   = 90f;   // 회전 속도 (도/초) — 4초에 1바퀴
        private static final float HIT_RADIUS    = 18f;   // 검 충돌 반경
        private static final float DAMAGE_PER_SEC = 25f;  // 초당 데미지

        /** 현재 검 위치 (Renderer에서 읽어 시각화) */
        public float swordX, swordY;
        private float angle = 90f;  // 시작: 12시 방향 (LibGDX 기준 90°)

        @Override
        public void tick(float x, float y, World world, InputFrame input) {
            // 각도 갱신 (시계 방향 = 각도 감소)
            angle -= ORBIT_SPEED / 60f;

            // 검 끝 위치 계산
            swordX = x + ORBIT_RADIUS * MathUtils.cosDeg(angle);
            swordY = y + ORBIT_RADIUS * MathUtils.sinDeg(angle);

            // 검에 닿는 적 데미지
            float dmg = DAMAGE_PER_SEC / 60f * world.getBonusSet().damageMultiplier;
            for (Enemy e : world.getEnemies()) {
                if (!e.alive) continue;
                if (dst(swordX, swordY, e.x, e.y) < HIT_RADIUS + 14f) {
                    e.takeDamage(dmg, world);
                }
            }
        }
    }

    // ── BOW: 가장 가까운 적 자동 조준 발사 ──────────────────────

    public static class BowWeapon extends Weapon {

        private static final float SPEED          = 400f;
        private static final float DAMAGE         = 10f;
        private static final int   COOLDOWN_TICKS = 30;   // 0.5초 간격 자동 발사

        private int cooldown = 0;

        @Override
        public void tick(float x, float y, World world, InputFrame input) {
            if (cooldown > 0) { cooldown--; return; }

            Enemy target = null;
            float minDist = Float.MAX_VALUE;
            for (Enemy e : world.getEnemies()) {
                if (!e.alive) continue;
                float d = dst(x, y, e.x, e.y);
                if (d < minDist) { minDist = d; target = e; }
            }
            if (target == null) return;

            float dx = target.x - x, dy = target.y - y;
            float d  = dst(x, y, target.x, target.y);
            int pierce  = world.getBonusSet().projectilePiercing;
            float mult  = world.getBonusSet().damageMultiplier;
            world.addProjectile(new Projectile(x, y, dx / d * SPEED, dy / d * SPEED, DAMAGE * mult, pierce));
            cooldown = COOLDOWN_TICKS;
        }
    }

    /** HEAL: 버프 오라 — FormationSynergy가 처리하므로 직접 공격 없음 */
    public static class HealWeapon extends Weapon {
        @Override
        public void tick(float x, float y, World world, InputFrame input) {}
    }
}
