package dmillerw.creator.client.gui;

import com.google.common.collect.Lists;
import dmillerw.creator.client.data.SortingMode;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author dmillerw
 */
public class GuiBetterCreative extends InventoryEffectRenderer {

    public static final InventoryBasic INVENTORY = new InventoryBasic("temporary", true, 80);

    private static final ResourceLocation TEXTURE = new ResourceLocation("creator:textures/gui/creative.png");

    public static CreativeTabs[] tabCache;
    public static List[] tabItemCache;
    public static int selectedTab = 0;
    public static int sortingMode = 0;

    public static int scrollOffset = 0;

    private static final int TAB_COUNT = 9;
    private static final int TAB_START_X = 10;
    private static final int TAB_START_Y = 16;

    private static final int SCROLL_BUTTON_WIDTH = 22;
    private static final int SCROLL_BUTTON_HEIGHT = 6;

    private static final int SCROLL_UP_X = 7;
    private static final int SCROLL_UP_Y = 7;
    private static final int SCROLL_DOWN_X = 7;
    private static final int SCROLL_DOWN_Y = 195;

    private static final int MODE_X = 181;
    private static final int MODE_Y = 5;
    private static final int MODE_WIDTH = 14;
    private static final int MODE_HEIGHT = 11;

    private int sidebarScrollIndex = 0;

    public GuiBetterCreative(EntityPlayer entityPlayer) {
        super(new Container(entityPlayer));

        this.xSize = 202;
        this.ySize = 208;

        tabCache = new CreativeTabs[CreativeTabs.creativeTabArray.length - 2];
        int index = 0;
        for (int i=0; i<CreativeTabs.creativeTabArray.length; i++) {
            final CreativeTabs tab = CreativeTabs.creativeTabArray[i];
            if (tab.getTabLabel().equals("inventory") || tab.getTabLabel().equals("search")) {
                continue;
            }
            tabCache[index] = tab;
            index++;
        }

        tabItemCache = new List[tabCache.length];
        for (int i=0; i<tabCache.length; i++) {
            final CreativeTabs tab = tabCache[i];

            List<ItemStack> list = Lists.newArrayList();
            tab.displayAllReleventItems(list);
            tabItemCache[i] = list;
        }

        ((Container)inventorySlots).update(tabItemCache[selectedTab], 0F);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partial) {
        super.drawScreen(mouseX, mouseY, partial);

        final SortingMode mode = SortingMode.values()[sortingMode];
        if (inBounds(mouseX, mouseY, guiLeft + MODE_X, guiTop + MODE_Y, MODE_WIDTH, MODE_HEIGHT)) {
            drawCreativeTabHoveringText(I18n.format(mode.name().toLowerCase()), mouseX, mouseY);
        }

        for (int i= sidebarScrollIndex; i<Math.min(tabCache.length, sidebarScrollIndex + TAB_COUNT); i++) {
            int checkX = guiLeft + TAB_START_X;
            int checkY = guiTop + TAB_START_Y + (20 * (i - sidebarScrollIndex));

            final CreativeTabs tab = tabCache[i];

            RenderHelper.enableGUIStandardItemLighting();
            drawItemStack(tab.getIconItemStack(), checkX, checkY, "");

            if (mouseX >= checkX && mouseX <= checkX + 16 && mouseY >= checkY && mouseY <= checkY + 20) {
                drawCreativeTabHoveringText(I18n.format(tab.getTranslatedTabLabel()), mouseX, mouseY);
            }
        }
    }

    @Override
    protected void keyTyped(char c, int keycode) {
        super.keyTyped(c, keycode);

        if (keycode == Keyboard.KEY_DOWN) {
            scrollOffset++;
            update();
        }

        if (keycode == Keyboard.KEY_UP) {
            scrollOffset--;
            if (scrollOffset < 0) scrollOffset = 0;
            update();
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {
        mouseX -= guiLeft;
        mouseY -= guiTop;

        if (inBounds(mouseX, mouseY, SCROLL_UP_X, SCROLL_UP_Y, SCROLL_BUTTON_WIDTH, SCROLL_BUTTON_HEIGHT)) {
            scrollTabSelectionUp();
            mc.getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), 1.0F));
        } else if (inBounds(mouseX, mouseY, SCROLL_DOWN_X, SCROLL_DOWN_Y, SCROLL_BUTTON_WIDTH, SCROLL_BUTTON_HEIGHT)) {
            scrollTabSelectionDown();
            mc.getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), 1.0F));
        } else if (inBounds(mouseX, mouseY, MODE_X, MODE_Y, MODE_WIDTH, MODE_HEIGHT)) {
            sortingMode++;
            if (sortingMode >= SortingMode.values().length) {
                sortingMode = 0;
            }
            update();
            mc.getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), 1.0F));
        } else {
            for (int i= sidebarScrollIndex; i<Math.min(tabCache.length, sidebarScrollIndex + TAB_COUNT); i++) {
                int checkX = TAB_START_X;
                int checkY = TAB_START_Y + (20 * (i - sidebarScrollIndex));

                if (inBounds(mouseX, mouseY, checkX, checkY, 16, 16)) {
                    changeTab(i);
                }
            }
            mouseX += guiLeft;
            mouseY += guiTop;
            super.mouseClicked(mouseX, mouseY, button);
        }
    }

    private void changeTab(int tab) {
        selectedTab = tab;
        update();
    }

    private void update() {
        ((Container)inventorySlots).update(tabItemCache[selectedTab], 0);
    }

    private boolean inBounds(int checkX, int checkY, int x, int y, int w, int h) {
        return checkX >= x && checkX <= x + w && checkY >= y && checkY <= y + h;
    }

    private void scrollTabSelectionUp() {
        sidebarScrollIndex--;
        if (sidebarScrollIndex < 0) {
            sidebarScrollIndex = 0;
        }
    }

    private void scrollTabSelectionDown() {
        sidebarScrollIndex++;
        if (sidebarScrollIndex > tabCache.length - TAB_COUNT) {
            sidebarScrollIndex = tabCache.length - TAB_COUNT;
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partial, int mouseX, int mouseY) {
        GL11.glColor4f(1, 1, 1, 1);
        mc.getTextureManager().bindTexture(TEXTURE);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

        for (int i = sidebarScrollIndex; i<Math.min(tabCache.length, sidebarScrollIndex + TAB_COUNT); i++) {
            int checkX = guiLeft + TAB_START_X;
            int checkY = guiTop + TAB_START_Y + (20 * (i - sidebarScrollIndex));

            if (selectedTab == i) {
                drawTexturedModalRect(checkX, checkY, 0, 223, 16, 16);
            }
        }

        drawTexturedModalRect(guiLeft + MODE_X, guiTop + MODE_Y, 202, (MODE_HEIGHT * sortingMode), MODE_WIDTH, MODE_HEIGHT);

        fontRendererObj.drawString(I18n.format(tabCache[selectedTab].getTranslatedTabLabel()), guiLeft + 34, guiTop + 7, 4210752);
    }

    private void drawItemStack(ItemStack p_146982_1_, int p_146982_2_, int p_146982_3_, String p_146982_4_) {
        GL11.glTranslatef(0.0F, 0.0F, 32.0F);
        this.zLevel = 200.0F;
        itemRender.zLevel = 200.0F;
        FontRenderer font = null;
        if (p_146982_1_ != null) font = p_146982_1_.getItem().getFontRenderer(p_146982_1_);
        if (font == null) font = fontRendererObj;
        itemRender.renderItemAndEffectIntoGUI(font, this.mc.getTextureManager(), p_146982_1_, p_146982_2_, p_146982_3_);
        itemRender.renderItemOverlayIntoGUI(font, this.mc.getTextureManager(), p_146982_1_, p_146982_2_, p_146982_3_, p_146982_4_);
        this.zLevel = 0.0F;
        itemRender.zLevel = 0.0F;
    }

    public static class Container extends net.minecraft.inventory.Container {

        public Container(EntityPlayer entityPlayer) {
            int i;
            for (i = 0; i < 9; ++i) {
                for (int j = 0; j < 8; ++j) {
                    this.addSlotToContainer(new Slot(INVENTORY, i * 9 + j, 34 + j * 18, 18 + i * 18));
                }
            }
        }

        public void update(List<ItemStack> itemList, float scroll) {
            final SortingMode mode = SortingMode.values()[sortingMode];
            List<ItemStack> copy = new ArrayList<ItemStack>(itemList);
            Collections.sort(copy, mode);

            int i;
            for (i = 0; i < 9; ++i) {
                for (int j = 0; j < 8; ++j) {
                    int i1 = (scrollOffset * 9) + (j + i * 9);

                    if (i1 >= 0 && i1 < itemList.size()) {
                        INVENTORY.setInventorySlotContents(i1 - (scrollOffset * 9), copy.get(i1));
                    } else {
                        INVENTORY.setInventorySlotContents(i1 - (scrollOffset * 9), null);
                    }
                }
            }
        }

        @Override
        public boolean canInteractWith(EntityPlayer entityPlayer) {
            return true;
        }
    }
}
