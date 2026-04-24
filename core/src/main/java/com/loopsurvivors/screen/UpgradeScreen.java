package com.loopsurvivors.screen;

import com.badlogic.gdx.ScreenAdapter;
import com.loopsurvivors.LoopSurvivorsGame;
import com.loopsurvivors.progression.UpgradeTree;

public class UpgradeScreen extends ScreenAdapter {

    private final LoopSurvivorsGame game;
    private final UpgradeTree upgradeTree;

    public UpgradeScreen(LoopSurvivorsGame game, UpgradeTree upgradeTree) {
        this.game = game;
        this.upgradeTree = upgradeTree;
    }

    @Override
    public void render(float delta) {
        // TODO: 직업별 업그레이드 트리 UI (소울 소모 → 노드 해금)
    }

    @Override
    public void dispose() {}
}
