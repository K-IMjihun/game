package com.loopsurvivors.world;

import com.badlogic.gdx.math.Vector2;
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
    private static final float MOVE_SPEED = 150f;

    // 입력 없는 상태를 나타내는 재사용 프레임 (정지 모드용)
    private static final InputFrame IDLE_FRAME =
        new InputFrame(0, new Vector2(0, 0), false, false);

    public Ghost(float startX, float startY, ClassType classType, int loopIndex, LoopRecording recording) {
        this.x = startX; this.y = startY;
        this.classType = classType;
        this.loopIndex = loopIndex;
        this.replayer = new GhostReplayer(recording);
        this.weapon = Weapon.forClass(classType);
    }

    public void tick(World world) {
        InputFrame frame = replayer.next();

        if (frame != null) {
            x += frame.moveDir().x * MOVE_SPEED / 60f;
            y += frame.moveDir().y * MOVE_SPEED / 60f;
            weapon.tick(x, y, world, frame);
        } else {
            // 정지 모드: 제자리에서 무기만 계속 동작
            weapon.tick(x, y, world, IDLE_FRAME);
        }
    }

    public Weapon getWeapon() { return weapon; }
}
