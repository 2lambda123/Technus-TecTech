package com.github.technus.tectech.mechanics.structure;

import net.minecraft.world.World;

@Deprecated
public interface IBlockPosConsumer {
    void consume(World world, int x, int y, int z);
}
