package com.loopsurvivors.progression;

import com.loopsurvivors.combat.ClassType;

import java.util.EnumMap;
import java.util.Map;

public class SoulCurrency {

    private final Map<ClassType, Integer> souls = new EnumMap<>(ClassType.class);

    public SoulCurrency() {
        for (ClassType ct : ClassType.values()) souls.put(ct, 0);
    }

    public void add(ClassType classType, int amount) {
        souls.merge(classType, amount, Integer::sum);
    }

    public int get(ClassType classType) {
        return souls.getOrDefault(classType, 0);
    }

    public boolean spend(ClassType classType, int cost) {
        int current = get(classType);
        if (current < cost) return false;
        souls.put(classType, current - cost);
        return true;
    }

    /** 루프 종료 시 처치 수·생존 틱 기반으로 소울 지급 */
    public void reward(ClassType classType, int kills, int survivedTicks) {
        int earned = kills * 2 + survivedTicks / 600; // 예시 공식
        add(classType, earned);
    }
}
