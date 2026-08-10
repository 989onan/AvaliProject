package com.lunkoashtail.avaliproject.pack;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PackSavedData extends SavedData {
    private static final String DATA_NAME = "avaliproject_packs";

    private final Map<UUID, PackRecord> packsByLeader = new HashMap<>();

    public static PackSavedData get(ServerLevel level) {
        DimensionDataStorage storage = level.getServer().overworld().getDataStorage();
        return storage.computeIfAbsent(new SavedData.Factory<>(PackSavedData::new, PackSavedData::load, null), DATA_NAME);
    }

    public Map<UUID, PackRecord> getPacksByLeader() {
        return packsByLeader;
    }

    public PackRecord getOrCreate(UUID leaderUUID, String defaultName) {
        PackRecord record = packsByLeader.get(leaderUUID);
        if (record == null) {
            record = new PackRecord(defaultName);
            packsByLeader.put(leaderUUID, record);
            setDirty();
        }
        return record;
    }

    public PackRecord get(UUID leaderUUID) {
        return packsByLeader.get(leaderUUID);
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        ListTag list = new ListTag();
        for (Map.Entry<UUID, PackRecord> entry : packsByLeader.entrySet()) {
            CompoundTag entryTag = new CompoundTag();
            entryTag.putUUID("Leader", entry.getKey());
            entryTag.putString("Name", entry.getValue().getName());
            ListTag members = new ListTag();
            for (UUID member : entry.getValue().getMemberUUIDs()) {
                CompoundTag memberTag = new CompoundTag();
                memberTag.putUUID("Id", member);
                members.add(memberTag);
            }
            entryTag.put("Members", members);
            list.add(entryTag);
        }
        tag.put("Packs", list);
        return tag;
    }

    public static PackSavedData load(CompoundTag tag, HolderLookup.Provider registries) {
        PackSavedData data = new PackSavedData();
        ListTag list = tag.getList("Packs", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag entryTag = list.getCompound(i);
            UUID leader = entryTag.getUUID("Leader");
            PackRecord record = new PackRecord(entryTag.getString("Name"));
            ListTag members = entryTag.getList("Members", Tag.TAG_COMPOUND);
            for (int j = 0; j < members.size(); j++) {
                record.getMemberUUIDs().add(members.getCompound(j).getUUID("Id"));
            }
            data.packsByLeader.put(leader, record);
        }
        return data;
    }
}
