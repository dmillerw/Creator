package dmillerw.creator.client;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import dmillerw.creator.client.gui.GuiBetterCreative;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraftforge.client.event.GuiOpenEvent;

/**
 * @author dmillerw
 */
public class ClientEventHandler {

    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event) {
        if (event.gui instanceof GuiContainerCreative) {
            event.gui = new GuiBetterCreative(Minecraft.getMinecraft().thePlayer);
        }
    }
}
