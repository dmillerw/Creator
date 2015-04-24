package dmillerw.creator.client;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import cpw.mods.fml.common.registry.GameData;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * @author dmillerw
 */
public class ColorSorter {

    private static class HSV {
        public float h;
        public float s;
        public float v;
        public HSV(float h, float s, float v) {
            this.h = h;
            this.s = s;
            this.v = v;
        }
    }

    private static final Map<Integer, HSV> stackColorMap = Maps.newHashMap();

    private static int stackToKey(ItemStack itemStack) {
        int result = GameData.getItemRegistry().getId(itemStack.getItem());
        result = 31 * result + itemStack.getItemDamage();
        if (itemStack.hasTagCompound())
            result = 31 * result + itemStack.getTagCompound().hashCode();
        return result;
    }

    private static boolean hasColor(ItemStack itemStack) {
        return stackColorMap.containsKey(stackToKey(itemStack));
    }

    private static void setColor(ItemStack itemStack, HSV color) {
        stackColorMap.put(stackToKey(itemStack), color);
    }

    private static HSV getColor(ItemStack itemStack) {
        return stackColorMap.get(stackToKey(itemStack));
    }

    private static final Map<Color, Integer> itemStackCountMap = Maps.newHashMap();
    private static final Map<Color, Integer> imageCountMap = Maps.newHashMap();

    private static final int TOLERANCE = 50;

    public static int compare(ItemStack o1, ItemStack o2) {
        HSV o1hsv = null;
        HSV o2hsv = null;

        if (!hasColor(o1)) {
            Color o1c = getItemStackAverageColor(o1);
            float[] hsv = Color.RGBtoHSB(o1c.getRed(), o1c.getGreen(), o1c.getBlue(), new float[3]);
            o1hsv = new HSV(hsv[0], hsv[1], hsv[2]);
            setColor(o1, o1hsv);
        } else {
            o1hsv = getColor(o1);
        }

        if (!hasColor(o2)) {
            Color o2c = getItemStackAverageColor(o1);
            float[] hsv = Color.RGBtoHSB(o2c.getRed(), o2c.getGreen(), o2c.getBlue(), new float[3]);
            o2hsv = new HSV(hsv[0], hsv[1], hsv[2]);
            setColor(o2, o2hsv);
        } else {
            o2hsv = getColor(o2);
        }

        if (o1hsv.h < o2hsv.h)
            return -1;
        if (o1hsv.h > o2hsv.h)
            return 1;
        if (o1hsv.s < o2hsv.s)
            return -1;
        if (o1hsv.s > o2hsv.s)
            return 1;
        if (o1hsv.v < o2hsv.v)
            return -1;
        if (o1hsv.v > o2hsv.v)
            return 1;
        return 0;
    }

    public static Color getItemStackAverageColor(ItemStack itemStack) {
        itemStackCountMap.clear();
        if (itemStack.getItem() instanceof ItemBlock) {
            return getBlockAverageColor(Block.getBlockFromItem(itemStack.getItem()), itemStack.getItemDamage());
        } else {
            return getItemAverageColor(itemStack.getItem(), itemStack.getItemDamage());
        }
    }

    private static Color getBlockAverageColor(Block block, int damage) {
        Set<String> icons = Sets.newHashSet();

        for (int i=0; i<6; i++) {
            IIcon icon = block.getIcon(i, damage);

            if (icon == null)
                continue;

            icons.add(icon.getIconName());
        }

        for (String name : icons) {
            ResourceLocation resourceLocation = null;

            if (name.contains(":")) {
                String[] split = name.split(":");
                resourceLocation = new ResourceLocation(split[0] + ":textures/blocks/" + split[1] + ".png");
            } else {
                resourceLocation = new ResourceLocation("textures/blocks/" + name + ".png");
            }

            try {
                BufferedImage bufferedImage = ImageIO.read(Minecraft.getMinecraft().getResourceManager().getResource(resourceLocation).getInputStream());

                if (bufferedImage == null)
                    continue;

                Color average = getImageAverageColor(bufferedImage);
                if (itemStackCountMap.containsKey(average)) {
                    itemStackCountMap.put(average, itemStackCountMap.get(average) + 1);
                } else {
                    itemStackCountMap.put(average, 1);
                }
            } catch (IOException ignore) {}
        }

        int max = 0;
        Color dom = null;
        for (Map.Entry<Color, Integer> entry : itemStackCountMap.entrySet()) {
            if (entry.getValue() > max) {
                dom = entry.getKey();
            }
        }

        if (dom == null)
            return Color.black;

        Set<Color> fin = Sets.newHashSet();
        fin.add(dom);

        for (Map.Entry<Color, Integer> entry : itemStackCountMap.entrySet()) {
            if (compare(dom, entry.getKey())) {
                fin.add(entry.getKey());
            }
        }

        int r = 0;
        int g = 0;
        int b = 0;

        for (Color rgb : fin) {
            r += rgb.getRed();
            g += rgb.getGreen();
            b += rgb.getBlue();
        }

        r /= fin.size();
        g /= fin.size();
        b /= fin.size();

        return new Color(r, g, b);
    }

    private static Color getItemAverageColor(Item item, int damage) {
        IIcon icon = item.getIconFromDamage(damage);
        if (icon == null)
            return Color.BLACK;

        String name = icon.getIconName();
        ResourceLocation resourceLocation = null;

        if (name.contains(":")) {
            String[] split = name.split(":");
            resourceLocation = new ResourceLocation(split[0] + ":textures/items/" + split[1] + ".png");
        } else {
            resourceLocation = new ResourceLocation("textures/items/" + name + ".png");
        }

        try {
            BufferedImage bufferedImage = ImageIO.read(Minecraft.getMinecraft().getResourceManager().getResource(resourceLocation).getInputStream());

            if (bufferedImage != null) {
                Color average = getImageAverageColor(bufferedImage);
                if (itemStackCountMap.containsKey(average)) {
                    itemStackCountMap.put(average, itemStackCountMap.get(average) + 1);
                } else {
                    itemStackCountMap.put(average, 1);
                }
            }
        } catch (IOException ignore) {}

        int max = 0;
        Color dom = null;
        for (Map.Entry<Color, Integer> entry : itemStackCountMap.entrySet()) {
            if (entry.getValue() > max) {
                dom = entry.getKey();
            }
        }

        if (dom == null)
            return Color.black;

        Set<Color> fin = Sets.newHashSet();
        fin.add(dom);

        for (Map.Entry<Color, Integer> entry : itemStackCountMap.entrySet()) {
            if (compare(dom, entry.getKey())) {
                fin.add(entry.getKey());
            }
        }

        int r = 0;
        int g = 0;
        int b = 0;

        for (Color rgb : fin) {
            r += rgb.getRed();
            g += rgb.getGreen();
            b += rgb.getBlue();
        }

        r /= fin.size();
        g /= fin.size();
        b /= fin.size();

        return new Color(r, g, b);
    }

    private static Color getImageAverageColor(BufferedImage bufferedImage) {
        imageCountMap.clear();

        for (int w=0; w<bufferedImage.getWidth(); w++) {
            for (int h=0; h<bufferedImage.getHeight(); h++) {
                Color rgb = new Color(bufferedImage.getRGB(w, h));
                if (imageCountMap.containsKey(rgb)) {
                    imageCountMap.put(rgb, imageCountMap.get(rgb) + 1);
                } else {
                    imageCountMap.put(rgb, 1);
                }
            }
        }

        int max = 0;
        Color dom = null;
        for (Map.Entry<Color, Integer> entry : imageCountMap.entrySet()) {
            if (entry.getValue() > max) {
                dom = entry.getKey();
            }
        }

        Set<Color> fin = Sets.newHashSet();
        fin.add(dom);

        for (Map.Entry<Color, Integer> entry : imageCountMap.entrySet()) {
            if (compare(dom, entry.getKey())) {
                fin.add(entry.getKey());
            }
        }

        int r = 0;
        int g = 0;
        int b = 0;

        for (Color rgb : fin) {
            r += rgb.getRed();
            g += rgb.getGreen();
            b += rgb.getBlue();
        }

        r /= fin.size();
        g /= fin.size();
        b /= fin.size();

        return new Color(r, g, b);
    }

    public static boolean compare(Color color1, Color color2) {
        if (Math.abs(color1.getRed() - color2.getRed()) > TOLERANCE) {
            return false;
        }

        if (Math.abs(color1.getGreen() - color2.getGreen()) > TOLERANCE) {
            return false;
        }

        if (Math.abs(color1.getBlue() - color2.getBlue()) > TOLERANCE) {
            return false;
        }

        return true;
    }
}
