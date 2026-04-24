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
import com.loopsurvivors.world.Enemy;
import com.loopsurvivors.world.Player;
import com.loopsurvivors.world.Projectile;
import com.loopsurvivors.world.World;

public class Renderer {

    private static final int   ENEMY_DRAW_SIZE  = 48;
    private static final int   FRAME_COLS       = 2;
    private static final int   FRAME_ROWS       = 2;
    private static final float FRAME_DURATION   = 0.2f;

    private final SpriteBatch batch;
    private final ShapeRenderer shape;
    private final OrthographicCamera camera;

    private final Texture background;
    private final Texture monsterSheet;
    private final Animation<TextureRegion> monsterAnim;
    private float stateTime = 0f;

    public Renderer(SpriteBatch batch) {
        this.batch = batch;
        this.shape = new ShapeRenderer();
        this.camera = new OrthographicCamera(1280, 720);
        camera.position.set(640, 360, 0);
        camera.update();

        background   = new Texture(Gdx.files.internal("background/clock/BG_clock.png"));
        monsterSheet = new Texture(Gdx.files.internal("monster/monster_01.png"));
        int frameW = monsterSheet.getWidth()  / FRAME_COLS;
        int frameH = monsterSheet.getHeight() / FRAME_ROWS;
        TextureRegion[][] grid   = TextureRegion.split(monsterSheet, frameW, frameH);
        TextureRegion[]   frames = {grid[0][0], grid[0][1], grid[1][0], grid[1][1]};
        monsterAnim = new Animation<>(FRAME_DURATION, frames);
        monsterAnim.setPlayMode(Animation.PlayMode.LOOP);
    }

    public void render(World world) {
        stateTime += Gdx.graphics.getDeltaTime();

        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // ── 스프라이트 렌더링 (SpriteBatch) ──────────────────────
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        // 배경
        batch.setColor(Color.WHITE);
        batch.draw(background, 0, 0, 1280, 720);

        TextureRegion monsterFrame = monsterAnim.getKeyFrame(stateTime);
        float half = ENEMY_DRAW_SIZE / 2f;
        for (Enemy e : world.getEnemies()) {
            if (e.alive) batch.draw(monsterFrame, e.x - half, e.y - half, ENEMY_DRAW_SIZE, ENEMY_DRAW_SIZE);
        }

        batch.end();

        // ── 도형 렌더링 (ShapeRenderer) ──────────────────────────
        shape.setProjectionMatrix(camera.combined);
        shape.begin(ShapeRenderer.ShapeType.Filled);

        // 플레이어
        Player player = world.getPlayer();
        if (player.alive) {
            shape.setColor(Color.WHITE);
            shape.rect(player.x - 12, player.y - 12, 24, 24);
        }

        // 투사체
        shape.setColor(Color.YELLOW);
        for (Projectile p : world.getProjectiles()) {
            if (p.active) shape.circle(p.x, p.y, 6);
        }

        shape.end();
    }

    public void resize(int width, int height) {
        camera.viewportWidth  = width;
        camera.viewportHeight = height;
        camera.update();
    }

    public void dispose() {
        shape.dispose();
        background.dispose();
        monsterSheet.dispose();
    }
}
