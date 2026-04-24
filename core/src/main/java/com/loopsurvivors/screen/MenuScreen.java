package com.loopsurvivors.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.ScreenAdapter;
import com.loopsurvivors.LoopSurvivorsGame;
import com.loopsurvivors.combat.ClassType;

public class MenuScreen extends ScreenAdapter {

    // 카드 레이아웃: 3장, 간격 60, 전체 960px → 좌우 여백 160
    private static final float CARD_W  = 280f;
    private static final float CARD_H  = 240f;
    private static final float CARD_Y  = 190f;
    private static final float SWORD_X = 160f;
    private static final float BOW_X   = 500f;
    private static final float HEAL_X  = 840f;

    private static final Color C_SWORD = new Color(0.90f, 0.30f, 0.30f, 1f);
    private static final Color C_BOW   = new Color(0.30f, 0.85f, 0.30f, 1f);
    private static final Color C_HEAL  = new Color(0.30f, 0.55f, 0.90f, 1f);

    private final LoopSurvivorsGame game;
    private final ShapeRenderer     shape;
    private final OrthographicCamera camera;
    private final BitmapFont        titleFont;
    private final BitmapFont        cardFont;
    private final BitmapFont        bodyFont;
    private final GlyphLayout       layout = new GlyphLayout();

    private final Rectangle swordRect = new Rectangle(SWORD_X, CARD_Y, CARD_W, CARD_H);
    private final Rectangle bowRect   = new Rectangle(BOW_X,   CARD_Y, CARD_W, CARD_H);
    private final Rectangle healRect  = new Rectangle(HEAL_X,  CARD_Y, CARD_W, CARD_H);

    private ClassType hovered = null;

    public MenuScreen(LoopSurvivorsGame game) {
        this.game = game;

        camera = new OrthographicCamera(1280, 720);
        camera.position.set(640, 360, 0);
        camera.update();

        shape = new ShapeRenderer();

        titleFont = new BitmapFont();
        titleFont.getData().setScale(5f);

        cardFont = new BitmapFont();
        cardFont.getData().setScale(2.8f);

        bodyFont = new BitmapFont();
        bodyFont.getData().setScale(1.6f);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.07f, 0.07f, 0.12f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // 마우스 → 월드 좌표
        Vector3 mouse = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));

        hovered = null;
        if (swordRect.contains(mouse.x, mouse.y))     hovered = ClassType.SWORD;
        else if (bowRect.contains(mouse.x, mouse.y))  hovered = ClassType.BOW;
        else if (healRect.contains(mouse.x, mouse.y)) hovered = ClassType.HEAL;

        if (Gdx.input.justTouched() && hovered != null) {
            ClassType chosen = hovered;
            game.setScreen(new GameScreen(game, chosen));
            return;
        }

        // ── ShapeRenderer: 카드 배경 + 테두리 ────────────────────────
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shape.setProjectionMatrix(camera.combined);

        shape.begin(ShapeRenderer.ShapeType.Filled);
        fillCard(swordRect, C_SWORD, hovered == ClassType.SWORD);
        fillCard(bowRect,   C_BOW,   hovered == ClassType.BOW);
        fillCard(healRect,  C_HEAL,  hovered == ClassType.HEAL);
        shape.end();

        shape.begin(ShapeRenderer.ShapeType.Line);
        borderCard(swordRect, C_SWORD, hovered == ClassType.SWORD);
        borderCard(bowRect,   C_BOW,   hovered == ClassType.BOW);
        borderCard(healRect,  C_HEAL,  hovered == ClassType.HEAL);
        shape.end();

        Gdx.gl.glDisable(GL20.GL_BLEND);

        // ── SpriteBatch: 텍스트 ───────────────────────────────────────
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();

        // 타이틀
        titleFont.setColor(1f, 1f, 1f, 1f);
        drawCentered(titleFont, "LOOP SURVIVORS", 640, 660);

        // 부제목
        bodyFont.setColor(0.60f, 0.60f, 0.65f, 1f);
        drawCentered(bodyFont, "Action roguelike - your past self fights beside you", 640, 600);

        // 직업 선택 안내
        bodyFont.setColor(0.90f, 0.85f, 0.50f, 1f);
        drawCentered(bodyFont, "Choose your class to begin", 640, 555);

        // 카드 내용
        renderCard(swordRect, ClassType.SWORD, "SWORD",
                "Melee swing (arc)",
                "High DPS  |  Close range");
        renderCard(bowRect, ClassType.BOW, "BOW",
                "Ranged projectile",
                "Safe distance  |  Back row");
        renderCard(healRect, ClassType.HEAL, "HEAL",
                "AoE buff aura",
                "Ally ATK boost");

        // 조작법
        bodyFont.setColor(0.38f, 0.38f, 0.42f, 1f);
        drawCentered(bodyFont, "Move: WASD / Arrow keys     Skill: SHIFT", 640, 88);

        game.batch.end();
    }

    // ── 카드 그리기 헬퍼 ─────────────────────────────────────────────

    private void fillCard(Rectangle r, Color c, boolean active) {
        float a = active ? 0.42f : 0.18f;
        shape.setColor(c.r, c.g, c.b, a);
        shape.rect(r.x, r.y, r.width, r.height);
    }

    private void borderCard(Rectangle r, Color c, boolean active) {
        float a = active ? 1.00f : 0.50f;
        shape.setColor(c.r, c.g, c.b, a);
        shape.rect(r.x, r.y, r.width, r.height);
    }

    private void renderCard(Rectangle card, ClassType type, String name, String attack, String feature) {
        Color c  = colorOf(type);
        float cx = card.x + card.width / 2f;

        // 직업명 (상단)
        cardFont.setColor(c.r, c.g, c.b, 1f);
        drawCentered(cardFont, name, cx, card.y + card.height - 38);

        // 구분선 대신 공격 유형
        bodyFont.setColor(0.90f, 0.90f, 0.90f, 1f);
        drawCentered(bodyFont, attack, cx, card.y + card.height - 100);

        // 특징
        bodyFont.setColor(0.65f, 0.65f, 0.65f, 1f);
        drawCentered(bodyFont, feature, cx, card.y + card.height - 140);

        // 호버 시 "클릭하여 시작" 표시
        if (hovered == type) {
            bodyFont.setColor(c.r, c.g, c.b, 1f);
            drawCentered(bodyFont, "[ Click to Start ]", cx, card.y + 38);
        }
    }

    private void drawCentered(BitmapFont font, String text, float cx, float y) {
        layout.setText(font, text);
        font.draw(game.batch, text, cx - layout.width / 2f, y);
    }

    private static Color colorOf(ClassType type) {
        return switch (type) {
            case SWORD -> C_SWORD;
            case BOW   -> C_BOW;
            case HEAL  -> C_HEAL;
        };
    }

    @Override
    public void dispose() {
        shape.dispose();
        titleFont.dispose();
        cardFont.dispose();
        bodyFont.dispose();
    }
}
