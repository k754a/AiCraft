package net.kallens.Command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PullChunkData {

    public static String pullChunkBlocks(ServerLevel overworld, BlockPos center, int radiusY, int maxBlocks) {
        Level world = overworld;

        int chunkX = center.getX() >> 4;
        int chunkZ = center.getZ() >> 4;

        // get chunk - this is where all blocks, including player placed ones, live
        ChunkAccess chunk = world.getChunk(chunkX, chunkZ);

        return buildChunkBlockMapString(chunk, world, center.getY(), radiusY, maxBlocks);
    }

    public static String buildChunkBlockMapString(ChunkAccess chunk, Level world, int centerY, int radiusY, int maxBlocks) {
        int cx = chunk.getPos().x;
        int cz = chunk.getPos().z;
        int minY = Math.max(world.getMinBuildHeight(), centerY - radiusY);
        int maxY = Math.min(world.getMaxBuildHeight() - 1, centerY + radiusY);

        Map<String, List<BlockPos>> blockBuckets = new HashMap<>();


        int totalBlocks = 0;
        for (int lx = 0; lx < 16; lx++) {
            for (int y = minY; y <= maxY; y++) {
                for (int lz = 0; lz < 16; lz++) {


                    BlockPos localPos = new BlockPos(lx, y, lz);
                    BlockState state = chunk.getBlockState(localPos);

                    if (state.isAir()) {
                        continue;
                    }

                    Block block = state.getBlock();
                    String name = BuiltInRegistries.BLOCK.getKey(block).toString();


                    int wx = (cx << 4) + lx;
                    int wz = (cz << 4) + lz;
                    BlockPos worldPos = new BlockPos(wx, y, wz);

                    blockBuckets.computeIfAbsent(name, k -> new ArrayList<>()).add(worldPos);
                    totalBlocks++;
                    if (totalBlocks >= maxBlocks) {
                        break;
                    }
                }
                if (totalBlocks >= maxBlocks) {
                    break;
                }
            }
            if (totalBlocks >= maxBlocks) {
                break;
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Chunk [").append(cx).append(',').append(cz).append("] block dump (y ")
                .append(minY).append(".. ").append(maxY).append("):\n");
        for (Map.Entry<String, List<BlockPos>> entry : blockBuckets.entrySet()) {
            sb.append('[').append(entry.getKey()).append(':');
            List<BlockPos> list = entry.getValue();
            for (int i = 0; i < list.size(); i++) {
                BlockPos p = list.get(i);
                sb.append('(').append(p.getX()).append(',').append(p.getY()).append(',').append(p.getZ()).append(')');
                if (i < list.size() - 1) sb.append(',');
            }
            sb.append(']').append('\n');
        }
        if (totalBlocks >= maxBlocks) {
            sb.append("[truncated: reached max block count ").append(maxBlocks).append("]\n");
        }
        return sb.toString();
    }
}
