package com.lunkoashtail.avaliproject.compat.rs;

import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

public class RefinedStorageCompat {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static void init() {
        LOGGER.info("Refined Storage detected - Nanoloom is accessible via External Storage/Importer through its vanilla item handler capability.");
    }
}
