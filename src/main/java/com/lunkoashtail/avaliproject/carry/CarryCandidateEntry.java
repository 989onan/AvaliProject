package com.lunkoashtail.avaliproject.carry;

import java.util.UUID;

public record CarryCandidateEntry(int entityId, UUID uuid, String name, boolean isPlayer, double distance) {
}
