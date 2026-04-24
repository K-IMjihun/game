package com.loopsurvivors.world;

import com.loopsurvivors.combat.ClassType;
import com.loopsurvivors.combat.Weapon;
import com.loopsurvivors.loop.InputFrame;

public class Player {

    public float x = 640f, y = 360f;
    public float hp = 100f;
    public boolean alive = true;
    public final ClassType classType;

    private final Weapon weapon;
    private static final float MOVE_SPEED = 150f;

    public Player(ClassType classType) {
        this.classType = classType;
        this.weapon = Weapon.forClass(classType);
    }

    public void tick(InputFrame input, World world) {
        if (!alive) return;

        x += input.moveDir().x * MOVE_SPEED / 60f;
        y += input.moveDir().y * MOVE_SPEED / 60f;

        // 체력 재생
        hp = Math.min(100f, hp + world.getBonusSet().healthRegenPerSec / 60f);

        // 무기 틱 (공격 입력 없이 매 틱 자동 처리)
        weapon.tick(x, y, world, input);
    }

    public Weapon getWeapon() { return weapon; }
}
