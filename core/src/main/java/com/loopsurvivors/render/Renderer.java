package com.loopsurvivors.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.loopsurvivors.combat.Weapon;
import com.loopsurvivors.world.DamageNumber;
import com.loopsurvivors.world.Enemy;
import com.loopsurvivors.world.Ghost;
import com.loopsurvivors.world.Player;
import com.loopsurvivors.world.Projectile;
import com.loopsurvivors.world.World;

public class Renderer {

    // ── 몬스터 애니메이션 상수 ───────────────────────────────────
    private static final int   ENEMY_DRAW_SIZE = 48;
    private static final int   FRAME_COLS      = 2;
    private static final int   FRAME_ROWS      = 2;
    private static final float FRAME_DURATION  = 0.2f;

    private final SpriteBatch  batch;
    private final ShapeRenderer shape;
    private final OrthographicCamera camera;

    // 텍스처
    private final Texture background;
    private final Texture monsterSheet;
    private final Texture clockHandTex;

    // 사전 계산된 TextureRegion
    private final TextureRegion clockHandRegion;

    // 몬스터 애니메이션
    private final Animation<TextureRegion> monsterAnim;
    private float stateTime = 0f;

    // 데미지 숫자 폰트
    private final BitmapFont  dmgFont;
    private final GlyphLayout glyphLayout = new GlyphLayout();

    // 시계 바늘: 1초마다 1칸(6°), 60초 = 1회전
    private float clockElapsed = 0f;
    private int   clockTick    = 0;  // 0~59

    public Renderer(SpriteBatch batch) {
        this.batch = batch;
        this.shape = new ShapeRenderer();
        this.camera = new OrthographicCamera(1280, 720);
        camera.position.set(640, 360, 0);
        camera.update();

        background    = new Texture(Gdx.files.internal("background/clock/BG_clock.png"));
        clockHandTex  = new Texture(Gdx.files.internal("background/clock/BG_clock_hand.png"));
        clockHandRegion = new TextureRegion(clockHandTex);

        monsterSheet = new Texture(Gdx.files.internal("monster/monster_01.png"));
        int frameW = monsterSheet.getWidth()  / FRAME_COLS;
        int frameH = monsterSheet.getHeight() / FRAME_ROWS;
        TextureRegion[][] grid   = TextureRegion.split(monsterSheet, frameW, frameH);
        TextureRegion[]   frames = {grid[0][0], grid[0][1], grid[1][0], grid[1][1]};
        monsterAnim = new Animation<>(FRAME_DURATION, frames);
        monsterAnim.setPlayMode(Animation.PlayMode.LOOP);

        dmgFont = new BitmapFont();
        dmgFont.getData().setScale(1.8f);
    }

    public void render(World world) {
        float delta = Gdx.graphics.getDeltaTime();
        stateTime += delta;

        // ── 시계 바늘: 1초마다 1칸 스냅, 60초 = 1회전 ───────────
        clockElapsed += delta;
        if (clockElapsed >= 1.0f) {
            clockElapsed -= 1.0f;
            clockTick = (clockTick + 1) % 60;
        }

        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // ── SpriteBatch 렌더링 ────────────────────────────────────
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        // 1. 배경 (시계판 없는 버전)
        batch.setColor(Color.WHITE);
        batch.draw(background, 0, 0, 1280, 720);

        // 2. 시계 바늘

        // 3. 몬스터
        TextureRegion monsterFrame = monsterAnim.getKeyFrame(stateTime);
        float half = ENEMY_DRAW_SIZE / 2f;
        for (Enemy e : world.getEnemies()) {
            if (e.alive) batch.draw(monsterFrame, e.x - half, e.y - half, ENEMY_DRAW_SIZE, ENEMY_DRAW_SIZE);
        }

        // 4. 데미지 숫자
        for (DamageNumber d : world.getDamageNumbers()) {
            if (!d.active) continue;
            dmgFont.setColor(1f, 0.9f, 0.2f, d.alpha());   // 노란색, 시간 지날수록 투명
            glyphLayout.setText(dmgFont, d.text);
            dmgFont.draw(batch, d.text,
                d.x - glyphLayout.width / 2f,   // 가로 중앙 정렬
                d.y);
        }

        batch.end();

        // ── ShapeRenderer 렌더링 ─────────────────────────────────
        shape.setProjectionMatrix(camera.combined);
        shape.begin(ShapeRenderer.ShapeType.Filled);

        // 플레이어
        Player player = world.getPlayer();
        if (player.alive) {
            shape.setColor(Color.WHITE);
            shape.rect(player.x - 12, player.y - 12, 24, 24);

            // 궤도 검 (SWORD)
            drawOrbitSword(shape, player.getWeapon(), player.x, player.y, 1.0f);
        }

        // 잔상의 궤도 검
        for (Ghost ghost : world.getGhosts()) {
            float alpha = 0.3f + 0.5f * (ghost.loopIndex / (float) Math.max(1, world.getCurrentLoop()));
            drawOrbitSword(shape, ghost.getWeapon(), ghost.x, ghost.y, alpha);
        }

        // 투사체
        shape.setColor(Color.YELLOW);
        for (Projectile p : world.getProjectiles()) {
            if (p.active) shape.circle(p.x, p.y, 6);
        }

        shape.end();
    }

    private void drawOrbitSword(ShapeRenderer sr, Weapon weapon, float ownerX, float ownerY, float alpha) {
        if (!(weapon instanceof Weapon.SwordWeapon sword)) return;
        sr.setColor(0.9f, 0.8f, 0.3f, alpha);          // 금빛
        sr.rectLine(ownerX, ownerY, sword.swordX, sword.swordY, 3f);  // 자루~검 끝 선
        sr.circle(sword.swordX, sword.swordY, 8f);      // 검 끝 원형 표시
    }

    public void resize(int width, int height) {
        camera.viewportWidth  = width;
        camera.viewportHeight = height;
        camera.update();
    }

    public void dispose() {
        shape.dispose();
        background.dispose();
        clockHandTex.dispose();
        monsterSheet.dispose();
        dmgFont.dispose();
    }
}
