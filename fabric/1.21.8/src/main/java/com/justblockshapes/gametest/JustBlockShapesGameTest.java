package com.justblockshapes.gametest;

import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;

public class JustBlockShapesGameTest {

    @GameTest
    public void allBlocksAreRegistered(GameTestHelper helper) {
        JustBlockShapesGameTestLogic.allBlocksAreRegistered(helper);
    }

    @GameTest
    public void registeredBlocksHaveCorrectTypes(GameTestHelper helper) {
        JustBlockShapesGameTestLogic.registeredBlocksHaveCorrectTypes(helper);
    }

    @GameTest
    public void variantBlockIdFormat(GameTestHelper helper) {
        JustBlockShapesGameTestLogic.variantBlockIdFormat(helper);
    }

    @GameTest
    public void blocksCanBePlaced(GameTestHelper helper) {
        JustBlockShapesGameTestLogic.blocksCanBePlaced(helper);
    }
}
