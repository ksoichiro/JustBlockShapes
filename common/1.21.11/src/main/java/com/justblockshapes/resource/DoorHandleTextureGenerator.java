package com.justblockshapes.resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

/**
 * Generates door handle textures by extracting the dominant color
 * from a vanilla block texture and darkening it.
 * Relies on java.awt which is available in client environments.
 * Returns null on failure so callers can fall back to the fixed texture.
 */
public class DoorHandleTextureGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger("JustBlockShapes");
    private static final float BRIGHTNESS_FACTOR = 0.5f;
    private static final float MIN_SATURATION = 0.15f;
    private static final int TEXTURE_SIZE = 16;

    /**
     * Generates a 16x16 door handle PNG from the given vanilla texture.
     *
     * @param texture ResourceLocation-style texture path (e.g. "minecraft:block/stone")
     * @return PNG bytes, or null if generation fails
     */
    public static byte[] generate(String texture) {
        try {
            String classpathPath = toClasspathPath(texture);
            BufferedImage image = readTexture(classpathPath);
            if (image == null) {
                LOGGER.warn("Failed to read vanilla texture for door handle: {}", texture);
                return null;
            }

            int dominantColor = extractDominantColor(image);
            if (dominantColor == -1) {
                LOGGER.warn("No opaque pixels found in texture for door handle: {}", texture);
                return null;
            }

            int darkened = darken(dominantColor);
            return generatePng(darkened);
        } catch (Exception e) {
            LOGGER.warn("Failed to generate door handle texture for {}: {}", texture, e.getMessage());
            return null;
        }
    }

    /**
     * Converts "minecraft:block/stone" to "/assets/minecraft/textures/block/stone.png".
     */
    static String toClasspathPath(String texture) {
        int colonIndex = texture.indexOf(':');
        String namespace = texture.substring(0, colonIndex);
        String path = texture.substring(colonIndex + 1);
        return "/assets/" + namespace + "/textures/" + path + ".png";
    }

    private static BufferedImage readTexture(String classpathPath) {
        try (InputStream is = DoorHandleTextureGenerator.class.getResourceAsStream(classpathPath)) {
            if (is == null) return null;
            return ImageIO.read(is);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Extracts the dominant color by quantizing RGB to 16 levels and finding
     * the most frequent color group, then averaging original pixels in that group.
     */
    static int extractDominantColor(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        // Count quantized color frequencies
        // key: quantized color, value: [count, totalR, totalG, totalB]
        Map<Integer, int[]> colorGroups = new HashMap<>();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int argb = image.getRGB(x, y);
                int alpha = (argb >> 24) & 0xFF;
                if (alpha < 128) continue;

                int r = (argb >> 16) & 0xFF;
                int g = (argb >> 8) & 0xFF;
                int b = argb & 0xFF;

                int qr = r / 16;
                int qg = g / 16;
                int qb = b / 16;
                int quantized = (qr << 16) | (qg << 8) | qb;

                int[] group = colorGroups.computeIfAbsent(quantized, k -> new int[4]);
                group[0]++;
                group[1] += r;
                group[2] += g;
                group[3] += b;
            }
        }

        if (colorGroups.isEmpty()) return -1;

        // Find most frequent group
        int[] best = null;
        for (int[] group : colorGroups.values()) {
            if (best == null || group[0] > best[0]) {
                best = group;
            }
        }

        int avgR = best[1] / best[0];
        int avgG = best[2] / best[0];
        int avgB = best[3] / best[0];
        return (avgR << 16) | (avgG << 8) | avgB;
    }

    /**
     * Darkens a color by reducing its HSB brightness.
     * For colors with some hue, ensures a minimum saturation so that
     * pale blocks (quartz, sandstone, end stone) retain their tint
     * instead of becoming plain gray after darkening.
     */
    static int darken(int rgb) {
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;

        float[] hsb = Color.RGBtoHSB(r, g, b, null);
        if (hsb[1] > 0.01f) {
            hsb[1] = Math.max(hsb[1], MIN_SATURATION);
        }
        hsb[2] *= BRIGHTNESS_FACTOR;
        // HSBtoRGB returns 0xFFRRGGBB; strip alpha here, generatePng() adds it back
        return Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]) & 0x00FFFFFF;
    }

    /**
     * Creates a 16x16 single-color PNG (matches standard Minecraft texture size
     * to avoid mipmap level warnings).
     */
    private static byte[] generatePng(int rgb) throws Exception {
        BufferedImage img = new BufferedImage(TEXTURE_SIZE, TEXTURE_SIZE, BufferedImage.TYPE_INT_ARGB);
        int argb = 0xFF000000 | rgb;
        for (int y = 0; y < TEXTURE_SIZE; y++) {
            for (int x = 0; x < TEXTURE_SIZE; x++) {
                img.setRGB(x, y, argb);
            }
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(img, "png", baos);
        return baos.toByteArray();
    }
}
