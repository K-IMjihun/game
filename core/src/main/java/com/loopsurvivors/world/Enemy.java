package com.loopsurvivors.world;

public class Enemy {

    public float x, y;
    public float hp;
    public final float maxHp;
    public final float speed;
    public boolean alive = true;

    // 넉백 속도 벡터 (px/s)
    private float knockbackVX = 0f, knockbackVY = 0f;
    private static final float KNOCKBACK_FORCE    = 300f;  // 초기 넉백 속도
    private static final float KNOCKBACK_FRICTION = 0.80f; // 틱당 감속 비율

    // 피격 무적 프레임 (0.1초 = 6틱)
    private int hitCooldown = 0;
    private static final int HIT_COOLDOWN_TICKS = 6;

    public Enemy(float x, float y, float hp, float speed) {
        this.x = x; this.y = y;
        this.hp = hp; this.maxHp = hp;
        this.speed = speed;
    }

    public void tick(World world) {
        // 피격 쿨다운 감소
        if (hitCooldown > 0) hitCooldown--;

        // 넉백 적용 후 감속
        x += knockbackVX / 60f;
        y += knockbackVY / 60f;
        knockbackVX *= KNOCKBACK_FRICTION;
        knockbackVY *= KNOCKBACK_FRICTION;

        // 플레이어 추적 (Ghost는 타겟 제외)
        Player player = world.getPlayer();
        if (!player.alive) return;

        float dx   = player.x - x;
        float dy   = player.y - y;
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

    /**
     * @param srcX 데미지 발생 위치 X (넉백 방향 계산용)
     * @param srcY 데미지 발생 위치 Y
     */
    public void takeDamage(float damage, float srcX, float srcY, World world) {
        if (hitCooldown > 0) return;  // 무적 프레임 중 무시

        hp -= damage;
        world.addDamageNumber(x, y + 24f, damage);

        // 넉백: 데미지 발생 위치 반대 방향으로 밀어냄
        float dx   = x - srcX;
        float dy   = y - srcY;
        float dist = (float) Math.sqrt(dx * dx + dy * dy);
        if (dist > 0) {
            knockbackVX = (dx / dist) * KNOCKBACK_FORCE;
            knockbackVY = (dy / dist) * KNOCKBACK_FORCE;
        }

        hitCooldown = HIT_COOLDOWN_TICKS;
        if (hp <= 0) alive = false;
    }
}
