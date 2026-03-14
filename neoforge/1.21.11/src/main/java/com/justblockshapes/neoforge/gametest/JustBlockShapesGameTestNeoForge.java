package com.justblockshapes.neoforge.gametest;

import com.justblockshapes.JustBlockShapes;
import com.justblockshapes.gametest.JustBlockShapesGameTestLogic;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.FunctionGameTestInstance;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestData;
import net.minecraft.gametest.framework.TestEnvironmentDefinition;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * NeoForge GameTests for 1.21.11 using the registry-based GameTest system.
 * Uses Identifier instead of ResourceLocation (renamed in 1.21.11).
 */
public class JustBlockShapesGameTestNeoForge {

    private static final Identifier EMPTY_STRUCTURE =
        Identifier.fromNamespaceAndPath(JustBlockShapes.MOD_ID, "empty");

    public static final DeferredRegister<Consumer<GameTestHelper>> TEST_FUNCTIONS =
        DeferredRegister.create(BuiltInRegistries.TEST_FUNCTION, JustBlockShapes.MOD_ID);

    private static final List<RegisteredTest> REGISTERED_TESTS = new ArrayList<>();

    private static boolean testInstancesRegistered = false;

    private record RegisteredTest(
        DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> holder,
        int timeoutTicks
    ) {}

    static {
        registerTest("all_blocks_are_registered", JustBlockShapesGameTestLogic::allBlocksAreRegistered, 100);
        registerTest("registered_blocks_have_correct_types", JustBlockShapesGameTestLogic::registeredBlocksHaveCorrectTypes, 100);
        registerTest("variant_block_id_format", JustBlockShapesGameTestLogic::variantBlockIdFormat, 100);
        registerTest("blocks_can_be_placed", JustBlockShapesGameTestLogic::blocksCanBePlaced, 100);
    }

    private static void registerTest(String name, Consumer<GameTestHelper> test, int timeoutTicks) {
        var holder = TEST_FUNCTIONS.register(name, () -> test);
        REGISTERED_TESTS.add(new RegisteredTest(holder, timeoutTicks));
    }

    public static void register(IEventBus modEventBus) {
        TEST_FUNCTIONS.register(modEventBus);
        modEventBus.addListener(JustBlockShapesGameTestNeoForge::onRegisterGameTests);
    }

    @SubscribeEvent
    public static void onRegisterGameTests(RegisterGameTestsEvent event) {
        if (testInstancesRegistered) {
            return;
        }
        testInstancesRegistered = true;

        Holder<TestEnvironmentDefinition> defaultEnv =
            event.registerEnvironment(
                Identifier.fromNamespaceAndPath(JustBlockShapes.MOD_ID, "default")
            );

        for (var test : REGISTERED_TESTS) {
            var holder = test.holder();
            if (!holder.isBound()) {
                continue;
            }
            var testData = new TestData<>(
                defaultEnv,
                EMPTY_STRUCTURE,
                test.timeoutTicks(),
                0,
                true
            );
            var instance = new FunctionGameTestInstance(holder.getKey(), testData);
            event.registerTest(holder.getId(), instance);
        }
    }
}
