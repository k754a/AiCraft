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

    public static String pullChunkBlocks(ServerLevel overworld) {
        Level world = overworld;

        // get player coords (first player, no checks cause we live dangerously)
        double px = world.getServer().getPlayerList().getPlayers().get(0).getX();
        double pz = world.getServer().getPlayerList().getPlayers().get(0).getZ();
        double py = world.getServer().getPlayerList().getPlayers().get(0).getY();

        int chunkX = ((int) Math.floor(px)) >> 4;
        int chunkZ = ((int) Math.floor(pz)) >> 4;

        // get chunk - this is where all blocks, including player placed ones, live
        ChunkAccess chunk = world.getChunk(chunkX, chunkZ);

        return buildChunkBlockMapString(chunk, world);
    }

    public static String buildChunkBlockMapString(ChunkAccess chunk, Level world) {
        int cx = chunk.getPos().x;
        int cz = chunk.getPos().z;
        int height = world.getMaxBuildHeight();

        Map<String, List<BlockPos>> blockBuckets = new HashMap<>();

        // iterate LOCAL chunk coords 0-15 for X and Z
        for (int lx = 0; lx < 16; lx++) {
            for (int y = 0; y < height; y++) {
                for (int lz = 0; lz < 16; lz++) {

                    // get blockstate FROM CHUNK at local pos, not world
                    BlockPos localPos = new BlockPos(lx, y, lz);
                    BlockState state = chunk.getBlockState(localPos);

                    Block block = state.getBlock();
                    String name = BuiltInRegistries.BLOCK.getKey(block).toString();

                    // convert to world pos for output cause coords are easier to read
                    int wx = (cx << 4) + lx;
                    int wz = (cz << 4) + lz;
                    BlockPos worldPos = new BlockPos(wx, y, wz);

                    blockBuckets.computeIfAbsent(name, k -> new ArrayList<>()).add(worldPos);
                }
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Chunk [").append(cx).append(',').append(cz).append("] block dump:\n");
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
        return sb.toString();
    }
}
