package com.justblockshapes.neoforge.gametest;

import com.justblockshapes.JustBlockShapes;
import com.justblockshapes.gametest.JustBlockShapesGameTestLogic;
import net.minecraft.gametest.framework.GameTestGenerator;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.world.level.block.Rotation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

@EventBusSubscriber(modid = JustBlockShapes.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class JustBlockShapesGameTestNeoForge {

    private static final String BATCH = "justblockshapes";
    private static final String TEMPLATE = "justblockshapes:empty";

    @SubscribeEvent
    public static void registerTests(RegisterGameTestsEvent event) {
        event.register(JustBlockShapesGameTestNeoForge.class);
    }

    @GameTestGenerator
    public static Collection<TestFunction> generateTests() {
        return List.of(
            createTest("allBlocksAreRegistered", JustBlockShapesGameTestLogic::allBlocksAreRegistered),
            createTest("registeredBlocksHaveCorrectTypes", JustBlockShapesGameTestLogic::registeredBlocksHaveCorrectTypes),
            createTest("variantBlockIdFormat", JustBlockShapesGameTestLogic::variantBlockIdFormat),
            createTest("blocksCanBePlaced", JustBlockShapesGameTestLogic::blocksCanBePlaced)
        );
    }

    private static TestFunction createTest(String name, Consumer<GameTestHelper> testFunc) {
        return new TestFunction(BATCH, name, TEMPLATE, Rotation.NONE, 100, 0L, true, testFunc);
    }
}
