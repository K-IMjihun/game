package com.loopsurvivors.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.math.Vector2;
import com.loopsurvivors.LoopSurvivorsGame;
import com.loopsurvivors.combat.ClassType;
import com.loopsurvivors.loop.InputFrame;
import com.loopsurvivors.render.GhostRenderer;
import com.loopsurvivors.render.Renderer;
import com.loopsurvivors.spawn.WaveDirector;
import com.loopsurvivors.world.World;

public class GameScreen extends ScreenAdapter {

    private static final float FIXED_STEP = 1f / 60f;

    private final LoopSurvivorsGame game;
    private World world;
    private final Renderer renderer;
    private final GhostRenderer ghostRenderer;
    private final WaveDirector waveDirector;
    private float accumulator = 0f;

    public GameScreen(LoopSurvivorsGame game, ClassType classType) {
        this.game = game;
        this.world = new World(classType);
        this.renderer = new Renderer(game.batch);
        this.ghostRenderer = new GhostRenderer(game.batch);
        this.waveDirector = new WaveDirector();
    }

    @Override
    public void render(float delta) {
        accumulator += Math.min(delta, 0.25f);
        while (accumulator >= FIXED_STEP) {
            InputFrame input = pollInput();
            world.tick(input);
            waveDirector.tick(world);
            accumulator -= FIXED_STEP;
        }

        if (!world.getPlayer().alive) {
            onPlayerDeath();
        }

        renderer.render(world);
        ghostRenderer.render(world);
    }

    private InputFrame pollInput() {
        float dx = 0, dy = 0;
        if (Gdx.input.isKeyPressed(Keys.W) || Gdx.input.isKeyPressed(Keys.UP))    dy += 1;
        if (Gdx.input.isKeyPressed(Keys.S) || Gdx.input.isKeyPressed(Keys.DOWN))  dy -= 1;
        if (Gdx.input.isKeyPressed(Keys.A) || Gdx.input.isKeyPressed(Keys.LEFT))  dx -= 1;
        if (Gdx.input.isKeyPressed(Keys.D) || Gdx.input.isKeyPressed(Keys.RIGHT)) dx += 1;
        boolean attack = false; // SWORD는 자동 공격 — Space 입력 불필요
        boolean skill  = Gdx.input.isKeyPressed(Keys.SHIFT_LEFT);
        Vector2 dir = new Vector2(dx, dy);
        if (dir.len2() > 0) dir.nor();
        return new InputFrame(world.getTickCount(), dir, attack, skill);
    }

    private void onPlayerDeath() {
        if (world.getCurrentLoop() >= 9) {
            // 10루프 완료 → 게임 종료 or 결과 화면
            game.setScreen(new MenuScreen(game));
            return;
        }
        // 직업 선택 후 다음 루프 — 여기서는 같은 직업으로 계속
        world.startNextLoop(world.getPlayer().classType);
    }

    @Override
    public void resize(int width, int height) {
        renderer.resize(width, height);
    }

    @Override
    public void dispose() {
        renderer.dispose();
    }
}
