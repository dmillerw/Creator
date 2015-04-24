package dmillerw.creator.client.data;

import cpw.mods.fml.common.registry.GameData;
import dmillerw.creator.client.ColorSorter;
import net.minecraft.item.ItemStack;

import java.util.Comparator;

/**
 * @author dmillerw
 */
public enum SortingMode implements Comparator<ItemStack> {

    DEFAULT {
        @Override
        public int compare(ItemStack o1, ItemStack o2) {
            return 0;
        }
    },
    A_TO_Z {
        @Override
        public int compare(ItemStack o1, ItemStack o2) {
            return o1.getDisplayName().compareTo(o2.getDisplayName());
        }
    },
    Z_TO_A {
        @Override
        public int compare(ItemStack o1, ItemStack o2) {
            return o2.getDisplayName().compareTo(o1.getDisplayName());
        }
    },
    ZERO_TO_MAX {
        @Override
        public int compare(ItemStack o1, ItemStack o2) {
            final int o1ID = GameData.getItemRegistry().getId(o1.getItem());
            final int o2ID = GameData.getItemRegistry().getId(o2.getItem());
            return Integer.compare(o1ID, o2ID);
        }
    },
    MAX_TO_ZERO {
        @Override
        public int compare(ItemStack o1, ItemStack o2) {
            final int o1ID = GameData.getItemRegistry().getId(o1.getItem());
            final int o2ID = GameData.getItemRegistry().getId(o2.getItem());
            return Integer.compare(o2ID, o1ID);
        }
    },
    RGB {
        @Override
        public int compare(ItemStack o1, ItemStack o2) {
            return ColorSorter.compare(o1, o2);
        }
    }
}
