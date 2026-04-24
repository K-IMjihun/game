package com.loopsurvivors.progression;

import com.loopsurvivors.combat.ClassType;

public class UpgradeNode {

    public final String id;
    public final ClassType classType;
    public final String branch;   // "attack" | "defense" | "util"
    public final int tier;        // 1~5
    public final int cost;
    public final String description;
    public boolean unlocked = false;

    public UpgradeNode(String id, ClassType classType, String branch, int tier, int cost, String description) {
        this.id = id;
        this.classType = classType;
        this.branch = branch;
        this.tier = tier;
        this.cost = cost;
        this.description = description;
    }
}
