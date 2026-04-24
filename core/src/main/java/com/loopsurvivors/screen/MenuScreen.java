package com.loopsurvivors.screen;

import com.badlogic.gdx.ScreenAdapter;
import com.loopsurvivors.LoopSurvivorsGame;
import com.loopsurvivors.combat.ClassType;

public class MenuScreen extends ScreenAdapter {

    private final LoopSurvivorsGame game;

    public MenuScreen(LoopSurvivorsGame game) {
        this.game = game;
    }

    @Override
    public void render(float delta) {
        // TODO: 직업 선택 UI 렌더링
        // 임시: 바로 SWORD로 게임 시작
        game.setScreen(new GameScreen(game, ClassType.SWORD));
    }

    @Override
    public void dispose() {}
}
