package com.loopsurvivors.spawn;

import com.loopsurvivors.world.Enemy;
import com.loopsurvivors.world.World;

public class WaveDirector {

    private float spawnTimer = 0f;
    private static final float BASE_INTERVAL = 2.0f; // 초
    private static final float HP_SCALE_PER_LOOP = 1.3f;
    private static final float SPEED_BASE = 60f;

    public void tick(World world) {
        spawnTimer += 1f / 60f;

        // 루프 번호에 따라 스폰 간격 감소
        float interval = BASE_INTERVAL / (1f + world.getCurrentLoop() * 0.2f);
        if (spawnTimer >= interval) {
            spawnTimer = 0f;
            spawnEnemy(world);
        }
    }

    private void spawnEnemy(World world) {
        float hp = 30f * (float) Math.pow(HP_SCALE_PER_LOOP, world.getCurrentLoop());
        float speed = SPEED_BASE + world.getCurrentLoop() * 5f;

        // 화면 가장자리 랜덤 스폰
        float x, y;
        if (Math.random() < 0.5) {
            x = (float) (Math.random() < 0.5 ? -30 : 1310);
            y = (float) (Math.random() * 720);
        } else {
            x = (float) (Math.random() * 1280);
            y = (float) (Math.random() < 0.5 ? -30 : 750);
        }

        world.addEnemy(new Enemy(x, y, hp, speed));
    }
}
