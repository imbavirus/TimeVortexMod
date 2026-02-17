package za.co.infernos.vortexmod.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import za.co.infernos.vortexmod.entities.custom.TardisEntity;
import za.co.infernos.vortexmod.mapdata.DisruptorMapData;
import za.co.infernos.vortexmod.mapdata.LocationMapData;
import za.co.infernos.vortexmod.mapdata.SecurityMapData;
import za.co.infernos.vortexmod.worldgen.dimension.ModDimensions;

import java.util.UUID;

public class DeleteTardisCommand {
    public DeleteTardisCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("tardis")
            .then(Commands.literal("delete")
                .requires(source -> source.hasPermission(2))
                .executes(context -> deleteTardis(context.getSource()))
            )
        );
    }

    private int deleteTardis(CommandSourceStack source) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        double range = 64.0;
        Vec3 eyePos = player.getEyePosition();
        Vec3 viewVec = player.getViewVector(1.0F);
        Vec3 endPos = eyePos.add(viewVec.x * range, viewVec.y * range, viewVec.z * range);
        AABB searchBox = player.getBoundingBox().expandTowards(viewVec.scale(range)).inflate(1.0, 1.0, 1.0);

        EntityHitResult hitResult = ProjectileUtil.getEntityHitResult(
            player, 
            eyePos, 
            endPos, 
            searchBox, 
            entity -> entity instanceof TardisEntity, 
            range * range
        );

        if (hitResult == null || !(hitResult.getEntity() instanceof TardisEntity tardis)) {
            source.sendFailure(Component.literal("You must be looking at a TARDIS entity to delete it."));
            return 0;
        }

        UUID tardisUUID = tardis.getUUID();
        UUID ownerUUID = tardis.getOwnerID();
        String tardisUUIDStr = tardisUUID.toString();

        MinecraftServer server = source.getServer();
        ServerLevel overworld = server.getLevel(net.minecraft.world.level.Level.OVERWORLD);
        ServerLevel tardisDim = server.getLevel(ModDimensions.tardisDIM_LEVEL_KEY);

        LocationMapData locationData = LocationMapData.get(overworld);
        SecurityMapData securityData = SecurityMapData.get(overworld);
        DisruptorMapData disruptorData = DisruptorMapData.get(overworld);

        BlockPos interiorPos = locationData.getDataMap().get(tardisUUIDStr);
        
        if (interiorPos == null && ownerUUID != null) {
            // Try owner UUID fallback if entity UUID wasn't used
            interiorPos = locationData.getDataMap().get(ownerUUID.toString());
        }

        // 1. Clear Interior
        if (interiorPos != null && tardisDim != null) {
            final BlockPos finalInteriorPos = interiorPos;
            int radius = 16; // 33x33x33 area
            for (int x = -radius; x <= radius; x++) {
                for (int y = -radius; y <= radius; y++) {
                    for (int z = -radius; z <= radius; z++) {
                        tardisDim.setBlockAndUpdate(finalInteriorPos.offset(x, y, z), Blocks.AIR.defaultBlockState());
                    }
                }
            }
            source.sendSuccess(() -> Component.literal("Cleared TARDIS interior at " + finalInteriorPos.toShortString()).withStyle(ChatFormatting.YELLOW), true);
        } else {
            source.sendSuccess(() -> Component.literal("No interior mapping found for this TARDIS. Skipping interior clearing.").withStyle(ChatFormatting.GRAY), true);
        }

        // 2. Scrub Metadata (UUID-specific only)
        if (locationData.getDataMap().containsKey(tardisUUIDStr)) {
            locationData.getDataMap().remove(tardisUUIDStr);
            locationData.setDirty();
        }
        
        // Scrub ownership data from maps if they are keyed by TARDIS UUID
        // Note: Some maps might be keyed by OwnerUUID for whitelists etc.
        // But the user said "no other tardisses owned by the same player".
        // So we only remove entries specifically linked to this TARDIS entity if possible.
        
        // Remove from disruptors if keyed by UUID
        if (disruptorData.getDataMap().containsKey(tardisUUIDStr)) {
            disruptorData.getDataMap().remove(tardisUUIDStr);
            disruptorData.setDirty();
        }

        // 3. Remove Entity
        tardis.discard();

        source.sendSuccess(() -> Component.literal("TARDIS " + tardisUUIDStr + " has been successfully deleted and unlinked.")
            .withStyle(ChatFormatting.GREEN), true);

        return 1;
    }
}
