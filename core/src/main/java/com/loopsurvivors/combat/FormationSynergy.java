package com.loopsurvivors.combat;

import com.loopsurvivors.world.Ghost;

import java.util.List;

public class FormationSynergy {

    /**
     * 잔상 구성을 분석해 보너스 세트 반환. 루프 시작 시 1회 계산.
     *
     * 전열 방패진 (SWORD 3+): 피격 무시 10%
     * 집중 사격   (BOW   3+): 투사체 관통 +1
     * 지원 진영   (HEAL  2+): 체력 재생 +0.5/s
     * 균형 편대   (각 직업 1 이상): 전체 데미지 +15%
     */
    public static BonusSet evaluate(List<Ghost> ghosts) {
        BonusSet bonus = new BonusSet();

        long swords = ghosts.stream().filter(g -> g.classType == ClassType.SWORD).count();
        long bows   = ghosts.stream().filter(g -> g.classType == ClassType.BOW).count();
        long heals  = ghosts.stream().filter(g -> g.classType == ClassType.HEAL).count();

        if (swords >= 3) bonus.hitIgnoreChance    = 0.10f;
        if (bows   >= 3) bonus.projectilePiercing += 1;
        if (heals  >= 2) bonus.healthRegenPerSec   = 0.5f;
        if (swords >= 1 && bows >= 1 && heals >= 1) bonus.damageMultiplier = 1.15f;

        return bonus;
    }
}
