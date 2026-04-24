package com.loopsurvivors.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.loopsurvivors.world.Ghost;
import com.loopsurvivors.world.World;

public class GhostRenderer {

    private final ShapeRenderer shape;
    private final OrthographicCamera camera;

    public GhostRenderer(SpriteBatch batch) {
        this.shape = new ShapeRenderer();
        this.camera = new OrthographicCamera(1280, 720);
        camera.position.set(640, 360, 0);
        camera.update();
    }

    public void render(World world) {
        if (world.getGhosts().isEmpty()) return;

        int totalLoops = world.getCurrentLoop();

        shape.setProjectionMatrix(camera.combined);
        shape.begin(ShapeRenderer.ShapeType.Filled);

        for (Ghost ghost : world.getGhosts()) {
            // 오래된 루프일수록 투명하게: 0.3 ~ 0.8
            float alpha = 0.3f + 0.5f * (ghost.loopIndex / (float) Math.max(1, totalLoops));
            Color tint = ghost.classType.tint();
            shape.setColor(tint.r, tint.g, tint.b, alpha);
            shape.rect(ghost.x - 12, ghost.y - 12, 24, 24);
        }

        shape.end();
    }

    public void dispose() {
        shape.dispose();
    }
}
