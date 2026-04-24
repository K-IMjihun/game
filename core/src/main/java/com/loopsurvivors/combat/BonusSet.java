package com.loopsurvivors.combat;

public class BonusSet {

    /** 전체 데미지 배율 (1.0 = 기본) */
    public float damageMultiplier = 1.0f;

    /** 초당 체력 재생 */
    public float healthRegenPerSec = 0f;

    /** 투사체 관통 추가치 */
    public int projectilePiercing = 0;

    /** 피격 무시 확률 (0.0 ~ 1.0) */
    public float hitIgnoreChance = 0f;
}
