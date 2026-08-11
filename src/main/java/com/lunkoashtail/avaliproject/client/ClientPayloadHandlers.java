package com.lunkoashtail.avaliproject.client;

import com.lunkoashtail.avaliproject.network.AvaliRecruitProposalPayload;
import com.lunkoashtail.avaliproject.network.AvaliTrustSyncPayload;
import com.lunkoashtail.avaliproject.network.DressingDepletedPayload;
import com.lunkoashtail.avaliproject.network.PackDataSyncPayload;
import com.lunkoashtail.avaliproject.screen.custom.AvaliInteractionScreen;
import com.lunkoashtail.avaliproject.screen.custom.AvaliRecruitProposalScreen;
import com.lunkoashtail.avaliproject.screen.custom.DressingMinigameScreen;
import com.lunkoashtail.avaliproject.screen.custom.PackScreen;
import net.minecraft.client.Minecraft;

/**
 * Client-only landing point for network payload handlers that touch Screen classes.
 * Payload record classes are loaded on both dists (for protocol registration), so any
 * direct reference to a Screen subclass inside their handle() body causes NeoForge's
 * dist validator to reject loading net.minecraft.client.gui.screens.Screen on a
 * dedicated server. Keeping those references confined to this client-only class means
 * this class is only ever loaded when its methods are actually invoked, which only
 * happens on the receiving client for playToClient payloads.
 */
public final class ClientPayloadHandlers {

    private ClientPayloadHandlers() {}

    public static void handleDressingDepleted(DressingDepletedPayload payload) {
        if (Minecraft.getInstance().screen instanceof DressingMinigameScreen screen) {
            screen.onDressingDepleted();
        }
    }

    public static void handleTrustSync(AvaliTrustSyncPayload payload) {
        AvaliInteractionScreen.onTrustSync(payload);
    }

    public static void handlePackDataSync(PackDataSyncPayload payload) {
        PackScreen.onDataSync(payload);
    }

    public static void handleRecruitProposal(AvaliRecruitProposalPayload payload) {
        Minecraft.getInstance().setScreen(new AvaliRecruitProposalScreen(payload.entityId()));
    }

    public static void openExpieInteractionScreen(int entityId) {
        Minecraft.getInstance().setScreen(new com.lunkoashtail.avaliproject.screen.custom.ExpieInteractionScreen(entityId));
    }
}
