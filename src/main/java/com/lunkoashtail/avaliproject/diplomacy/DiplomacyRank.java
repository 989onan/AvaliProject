package com.lunkoashtail.avaliproject.diplomacy;

public enum DiplomacyRank {
    UNKNOWN(0, 1.0, 1.0),
    ACQUAINTED(100, 0.9, 1.1),
    RESPECTED(300, 0.75, 1.25),
    TRUSTED(600, 0.5, 1.5),
    LEGENDARY(1000, 0.25, 2.0);

    private final int minRenown;
    private final double recruitCostMultiplier;
    private final double trustGainMultiplier;

    DiplomacyRank(int minRenown, double recruitCostMultiplier, double trustGainMultiplier) {
        this.minRenown = minRenown;
        this.recruitCostMultiplier = recruitCostMultiplier;
        this.trustGainMultiplier = trustGainMultiplier;
    }

    public double recruitCostMultiplier() {
        return recruitCostMultiplier;
    }

    public double trustGainMultiplier() {
        return trustGainMultiplier;
    }

    public static DiplomacyRank forRenown(int renown) {
        DiplomacyRank result = UNKNOWN;
        for (DiplomacyRank rank : values()) {
            if (renown >= rank.minRenown)
                result = rank;
        }
        return result;
    }
}
