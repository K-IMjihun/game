package com.loopsurvivors.world;

import com.loopsurvivors.combat.BonusSet;
import com.loopsurvivors.combat.ClassType;
import com.loopsurvivors.combat.FormationSynergy;
import com.loopsurvivors.loop.InputFrame;
import com.loopsurvivors.loop.LoopRecorder;
import com.loopsurvivors.loop.LoopRecording;

import java.util.ArrayList;
import java.util.List;

public class World {

    private Player player;
    private final List<Ghost> ghosts = new ArrayList<>();
    private final List<Enemy> enemies = new ArrayList<>();
    private final List<Projectile> projectiles = new ArrayList<>();

    private final LoopRecorder recorder = new LoopRecorder();
    private BonusSet bonusSet = new BonusSet();
    private int tickCount = 0;
    private int currentLoop = 0;

    public World(ClassType classType) {
        this.player = new Player(classType);
    }

    /** 고정 타임스텝 1틱 진행. GameScreen이 매 FIXED_STEP마다 호출. */
    public void tick(InputFrame input) {
        tickCount++;

        // 입력 기록 (잔상 재생을 위해 매 틱 저장)
        recorder.record(input.tickIndex(), input.moveDir(), input.attackPressed(), input.skillPressed());

        player.tick(input, this);
        for (Ghost g : new ArrayList<>(ghosts)) g.tick(this);
        for (Enemy e : new ArrayList<>(enemies)) e.tick(this);
        projectiles.removeIf(p -> !p.active);
        for (Projectile p : new ArrayList<>(projectiles)) p.tick(this);
        enemies.removeIf(e -> !e.alive);
    }

    /**
     * 사망 → 다음 루프 시작.
     * 직전 루프를 Ghost로 변환하고 새 Player로 초기화.
     */
    public void startNextLoop(ClassType nextClass) {
        LoopRecording recording = recorder.finishAndReset();
        Ghost ghost = new Ghost(640f, 360f, player.classType, currentLoop, recording);
        ghosts.add(ghost);
        bonusSet = FormationSynergy.evaluate(ghosts);

        currentLoop++;
        tickCount = 0;
        player = new Player(nextClass);
        player.x = 640f;
        player.y = 360f;
    }

    public void addProjectile(Projectile p) { projectiles.add(p); }
    public void addEnemy(Enemy e)           { enemies.add(e); }

    public Player            getPlayer()      { return player; }
    public List<Ghost>       getGhosts()      { return ghosts; }
    public List<Enemy>       getEnemies()     { return enemies; }
    public List<Projectile>  getProjectiles() { return projectiles; }
    public BonusSet          getBonusSet()    { return bonusSet; }
    public int               getTickCount()   { return tickCount; }
    public int               getCurrentLoop() { return currentLoop; }
}
