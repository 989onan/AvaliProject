package com.lunkoashtail.avaliproject.compat.cc;

import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

public class ComputerCraftCompat {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static void init() {
        LOGGER.info("CC: Tweaked detected - peripheral scaffolding present, full driver pending CC API dependency.");
    }
}
