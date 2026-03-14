package com.justblockshapes.gametest;

import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;

public class JustBlockShapesGameTest implements FabricGameTest {

    @GameTest(template = EMPTY_STRUCTURE)
    public void allBlocksAreRegistered(GameTestHelper helper) {
        JustBlockShapesGameTestLogic.allBlocksAreRegistered(helper);
    }

    @GameTest(template = EMPTY_STRUCTURE)
    public void registeredBlocksHaveCorrectTypes(GameTestHelper helper) {
        JustBlockShapesGameTestLogic.registeredBlocksHaveCorrectTypes(helper);
    }

    @GameTest(template = EMPTY_STRUCTURE)
    public void variantBlockIdFormat(GameTestHelper helper) {
        JustBlockShapesGameTestLogic.variantBlockIdFormat(helper);
    }

    @GameTest(template = EMPTY_STRUCTURE)
    public void blocksCanBePlaced(GameTestHelper helper) {
        JustBlockShapesGameTestLogic.blocksCanBePlaced(helper);
    }
}
