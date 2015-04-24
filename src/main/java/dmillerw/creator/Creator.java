package dmillerw.creator;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import dmillerw.creator.client.ClientEventHandler;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;

/**
 * @author dmillerw
 */

@Mod(modid = Creator.ID, name = Creator.NAME, version = Creator.VERSION)
public class Creator {

    public static final String ID = "Creator";
    public static final String NAME = "Creator";
    public static final String VERSION = "%MOD_VERSION%";

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new ClientEventHandler());

        // Dummy tabs
        CreativeTabs tab1 = new CreativeTabs("potato") {
            @Override
            public Item getTabIconItem() {
                return Items.diamond;
            }
        };

        CreativeTabs tab3 = new CreativeTabs("potato1") {
            @Override
            public Item getTabIconItem() {
                return Items.coal;
            }
        };

        CreativeTabs tab2 = new CreativeTabs("potato2") {
            @Override
            public Item getTabIconItem() {
                return Items.carrot_on_a_stick;
            }
        };
    }
}
