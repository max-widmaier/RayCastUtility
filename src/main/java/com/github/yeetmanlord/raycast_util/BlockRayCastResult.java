package com.github.yeetmanlord.raycast_util;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class BlockRayCastResult extends RayCastResult {

    private Block block;

    private BlockFace face;

    public BlockRayCastResult(ResultType type, Block block, BlockFace face) {
        super(type, block);
        this.block = block;
        this.face = face;
    }

    public Block getBlock() {
        return block;
    }

    public BlockFace getFace() {
        return face;
    }

    @Override
    public String toString() {
        return "BlockRayTraceResult{" +
                "block: " + block +
                ", face: " + face +
                '}';
    }
}
