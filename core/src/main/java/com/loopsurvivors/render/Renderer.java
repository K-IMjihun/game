package com.loopsurvivors.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.loopsurvivors.world.Enemy;
import com.loopsurvivors.world.Player;
import com.loopsurvivors.world.Projectile;
import com.loopsurvivors.world.World;

public class Renderer {

    private final SpriteBatch batch;
    private final ShapeRenderer shape;
    private final OrthographicCamera camera;

    public Renderer(SpriteBatch batch) {
        this.batch = batch;
        this.shape = new ShapeRenderer();
        this.camera = new OrthographicCamera(1280, 720);
        camera.position.set(640, 360, 0);
        camera.update();
    }

    public void render(World world) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        shape.setProjectionMatrix(camera.combined);
        shape.begin(ShapeRenderer.ShapeType.Filled);

        // 플레이어
        Player player = world.getPlayer();
        if (player.alive) {
            shape.setColor(Color.WHITE);
            shape.rect(player.x - 12, player.y - 12, 24, 24);
        }

        // 적
        shape.setColor(Color.RED);
        for (Enemy e : world.getEnemies()) {
            if (e.alive) shape.circle(e.x, e.y, 14);
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
    }
}
