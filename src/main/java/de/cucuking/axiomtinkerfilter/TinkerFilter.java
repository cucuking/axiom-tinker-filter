package de.cucuking.axiomtinkerfilter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.BlockHitResult;

public final class TinkerFilter {
    private static final Path CONFIG = FabricLoader.getInstance().getConfigDir().resolve("axiom-tinker-filter.properties");
    private static boolean woolEnabled = false;
    private static boolean concreteEnabled = false;

    static {
        load();
    }

    private TinkerFilter() {}

    public static boolean woolEnabled() {
        return woolEnabled;
    }

    public static boolean concreteEnabled() {
        return concreteEnabled;
    }

    public static void toggleWool() {
        woolEnabled = !woolEnabled;
        save();
    }

    public static void toggleConcrete() {
        concreteEnabled = !concreteEnabled;
        save();
    }

    public static boolean shouldSkipTinker() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || !(minecraft.hitResult instanceof BlockHitResult hit)) {
            return false;
        }

        Identifier id = BuiltInRegistries.BLOCK.getKey(minecraft.level.getBlockState(hit.getBlockPos()).getBlock());
        if (id == null || !"minecraft".equals(id.getNamespace())) {
            return false;
        }
        String path = id.getPath();
        return (!woolEnabled && path.endsWith("_wool"))
            || (!concreteEnabled && path.endsWith("_concrete"));
    }

    private static void load() {
        if (!Files.isRegularFile(CONFIG)) return;
        Properties properties = new Properties();
        try (InputStream in = Files.newInputStream(CONFIG)) {
            properties.load(in);
            woolEnabled = Boolean.parseBoolean(properties.getProperty("woolEnabled", "false"));
            concreteEnabled = Boolean.parseBoolean(properties.getProperty("concreteEnabled", "false"));
        } catch (IOException ignored) {
            // Keep the safe defaults if the configuration cannot be read.
        }
    }

    private static void save() {
        Properties properties = new Properties();
        properties.setProperty("woolEnabled", Boolean.toString(woolEnabled));
        properties.setProperty("concreteEnabled", Boolean.toString(concreteEnabled));
        try {
            Files.createDirectories(CONFIG.getParent());
            try (OutputStream out = Files.newOutputStream(CONFIG)) {
                properties.store(out, "Axiom Tinker Filter");
            }
        } catch (IOException ignored) {
            // The switches still work for the current game session.
        }
    }
}
