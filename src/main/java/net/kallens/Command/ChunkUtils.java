package net.kallens.Command;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.storage.ChunkSerializer;

public class ChunkUtils {
    private static ServerLevel world;
    private static int chunkX, chunkZ;


    public static String PullChunk(ServerLevel w, int x, int z) {
        world = w;
        chunkX = x;
        chunkZ = z;


        LevelChunk chunk = world.getChunk(chunkX, chunkZ);


        CompoundTag nbt = ChunkSerializer.write(world, chunk);

        return nbt.toString();
    }
}
