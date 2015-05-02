package dmillerw.creator.client;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * @author dmillerw
 */
public class CreativeTab {

    public static CreativeTab create(CreativeTabs tab) {
        List<ItemStack> list = Lists.newArrayList();
        tab.displayAllReleventItems(list);
        return new CreativeTab(tab.getTabIndex(), tab.getTabLabel(), tab.getIconItemStack(), ImmutableList.copyOf(list));
    }

    public final int index;
    public final String label;
    public final ItemStack icon;
    public final ImmutableList<ItemStack> contents;

    public CreativeTab(int index, String label, ItemStack icon, ImmutableList<ItemStack> contents) {
        this.index = index;
        this.label = label;
        this.icon = icon;
        this.contents = contents;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CreativeTab that = (CreativeTab) o;

        if (!label.equals(that.label)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return label.hashCode();
    }

    @Override
    public String toString() {
        return "{tab: " + label + "}";
    }
}
