package com.loopsurvivors.world;

import com.loopsurvivors.combat.ClassType;
import com.loopsurvivors.combat.Weapon;
import com.loopsurvivors.loop.GhostReplayer;
import com.loopsurvivors.loop.InputFrame;
import com.loopsurvivors.loop.LoopRecording;

public class Ghost {

    public float x, y;
    public final ClassType classType;
    public final int loopIndex;
    public final boolean invincible = true;

    private final GhostReplayer replayer;
    private final Weapon weapon;
    private int attackCooldown = 0;
    private static final int ATTACK_CD_TICKS = 20;
    private static final float MOVE_SPEED = 150f;

    public Ghost(float startX, float startY, ClassType classType, int loopIndex, LoopRecording recording) {
        this.x = startX; this.y = startY;
        this.classType = classType;
        this.loopIndex = loopIndex;
        this.replayer = new GhostReplayer(recording);
        this.weapon = Weapon.forClass(classType);
    }

    public void tick(World world) {
        if (attackCooldown > 0) attackCooldown--;

        InputFrame frame = replayer.next();
        if (frame != null) {
            x += frame.moveDir().x * MOVE_SPEED / 60f;
            y += frame.moveDir().y * MOVE_SPEED / 60f;
            if (frame.attackPressed() && attackCooldown == 0) {
                weapon.attack(x, y, world);
                attackCooldown = ATTACK_CD_TICKS;
            }
        } else {
            // 정지 모드: 근거리 적만 공격
            if (attackCooldown == 0) {
                weapon.attackNearest(x, y, world);
                attackCooldown = ATTACK_CD_TICKS;
            }
        }
    }
}
