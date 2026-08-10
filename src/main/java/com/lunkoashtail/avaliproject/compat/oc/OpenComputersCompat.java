package com.lunkoashtail.avaliproject.compat.oc;

import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

public class OpenComputersCompat {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static void init() {
        LOGGER.info("OpenComputers detected - peripheral scaffolding present, full driver pending OC API dependency.");
    }
}
