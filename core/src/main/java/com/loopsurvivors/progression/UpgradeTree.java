package com.loopsurvivors.progression;
import com.loopsurvivors.combat.ClassType;

import java.util.ArrayList;
import java.util.List;

public class UpgradeTree {

    private static final String SAVE_PATH = ".loopsurvivors/progress.json";

    private final List<UpgradeNode> nodes = new ArrayList<>();
    private final SoulCurrency currency = new SoulCurrency();

    public UpgradeTree() {
        initNodes();
    }

    private void initNodes() {
        // SWORD 브랜치 (공격/방어/유틸 각 5단계)
        addBranch(ClassType.SWORD, "attack",  new String[]{"검격 강화 I","검격 강화 II","광역 강화 I","광역 강화 II","폭발 검격"}, new int[]{3,5,8,12,20});
        addBranch(ClassType.SWORD, "defense", new String[]{"강인함 I","강인함 II","방패막기 I","방패막기 II","불굴"}, new int[]{3,5,8,12,20});
        addBranch(ClassType.SWORD, "util",    new String[]{"이동속도 I","이동속도 II","쿨감 I","쿨감 II","신속"}, new int[]{3,5,8,12,20});

        // BOW 브랜치
        addBranch(ClassType.BOW, "attack",  new String[]{"화살 강화 I","화살 강화 II","관통 I","관통 II","저격"}, new int[]{3,5,8,12,20});
        addBranch(ClassType.BOW, "defense", new String[]{"회피 I","회피 II","재빠름 I","재빠름 II","도주 달인"}, new int[]{3,5,8,12,20});
        addBranch(ClassType.BOW, "util",    new String[]{"조준 I","조준 II","발사속도 I","발사속도 II","속사"}, new int[]{3,5,8,12,20});

        // HEAL 브랜치
        addBranch(ClassType.HEAL, "attack",  new String[]{"오라 강화 I","오라 강화 II","광역 오라 I","광역 오라 II","성스러운 오라"}, new int[]{3,5,8,12,20});
        addBranch(ClassType.HEAL, "defense", new String[]{"체력 증가 I","체력 증가 II","재생 강화 I","재생 강화 II","불사"}, new int[]{3,5,8,12,20});
        addBranch(ClassType.HEAL, "util",    new String[]{"시너지 확대 I","시너지 확대 II","소울 증가 I","소울 증가 II","마스터"}, new int[]{3,5,8,12,20});
    }

    private void addBranch(ClassType ct, String branch, String[] descs, int[] costs) {
        for (int i = 0; i < descs.length; i++) {
            nodes.add(new UpgradeNode(ct.name() + "_" + branch + "_" + (i + 1), ct, branch, i + 1, costs[i], descs[i]));
        }
    }

    public boolean unlock(UpgradeNode node) {
        if (node.unlocked) return false;
        if (!currency.spend(node.classType, node.cost)) return false;
        node.unlocked = true;
        return true;
    }

    public List<UpgradeNode> getNodes() { return nodes; }
    public SoulCurrency getCurrency()   { return currency; }

    // ── LibGDX Json 저장/로드 ──────────────────────────────────

    public void save() {
        // TODO: LibGDX FileHandle + Json 직렬화
        // Json json = new Json();
        // FileHandle file = Gdx.files.external(SAVE_PATH);
        // file.writeString(json.toJson(buildSaveData()), false);
    }

    public void load() {
        // TODO: 역직렬화 후 nodes.unlocked 복원
    }

    public void loadAndApply() {
        load();
        // TODO: 해금된 노드의 스탯 보너스를 플레이어 스탯에 반영
    }
}
