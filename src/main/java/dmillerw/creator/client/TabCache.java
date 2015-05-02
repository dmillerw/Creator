package dmillerw.creator.client;

import com.google.common.collect.Lists;
import net.minecraft.creativetab.CreativeTabs;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author dmillerw
 */
public class TabCache {

    public static List<String> labels = Lists.newLinkedList();
    private static List<CreativeTab> creativeTabs = Lists.newLinkedList();

    public static void build() {
        for (CreativeTabs tab : CreativeTabs.creativeTabArray) {
            register(CreativeTab.create(tab));
        }

        Collections.sort(creativeTabs, new Comparator<CreativeTab>() {
            @Override
            public int compare(CreativeTab o1, CreativeTab o2) {
                return Integer.compare(o1.index, o2.index);
            }
        });

        for (CreativeTab tab : creativeTabs) {
            labels.add(tab.label);
        }
    }

    public static int count() {
        return creativeTabs.size();
    }

    public static void register(CreativeTab creativeTab) {
        if (creativeTabs.contains(creativeTab))
            return;

        if (creativeTab.label.equals("search") || creativeTab.label.equals("inventory"))
            return;

        creativeTabs.add(creativeTab);
    }

    public static CreativeTab get(String label) {
        for (CreativeTab tab : creativeTabs) {
            if (tab.label.equals(label)) {
                return tab;
            }
        }
        return null;
    }
}
