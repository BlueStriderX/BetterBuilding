package net.thederpgamer.betterbuilding.gui;

import net.thederpgamer.betterbuilding.BetterBuilding;
import net.thederpgamer.betterbuilding.util.GUIScale;
import org.schema.common.util.linAlg.Vector4i;
import org.schema.game.client.view.gui.HotbarInterface;
import org.schema.game.client.view.gui.shiphud.newhud.GUIPosition;
import org.schema.game.client.view.gui.shiphud.newhud.HudConfig;
import org.schema.game.client.view.gui.weapon.WeaponSlotOverlayElement;
import org.schema.game.common.data.player.PlayerState;
import org.schema.game.common.data.player.inventory.Inventory;
import org.schema.schine.graphicsengine.core.Controller;
import org.schema.schine.graphicsengine.core.GlUtil;
import org.schema.schine.graphicsengine.core.MouseEvent;
import org.schema.schine.graphicsengine.forms.font.FontLibrary;
import org.schema.schine.graphicsengine.forms.gui.*;
import org.schema.schine.graphicsengine.forms.gui.newgui.GUIInnerBackground;
import org.schema.schine.input.InputState;
import javax.vecmath.Vector2f;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 * BuildHotbar.java
 * Build Hotbar GUI
 * ==================================================
 * Created 01/22/2021
 * @author TheDerpGamer
 */
public class BuildHotbar extends HudConfig implements HotbarInterface {

    private Inventory inventory;
    private int activeHotbar;
    private short[][] hotbars;
    private File hotbarsFile;

    private GUITextOverlay barIndexText;
    private GUIAncor bgIndexAnchor;
    private GUIInnerBackground bgIndex;
    private GUIOverlay upButton;
    private GUIOverlay downButton;

    public BuildHotbar(InputState inputState, PlayerState playerState) {
        super(inputState);
        inventory = playerState.getInventory();
        activeHotbar = 0;
        hotbars = new short[10][10];
    }

    /**
     * Saves the active hotbar to slot
     */
    public void saveActive() {
        short[] elements = new short[10];
        for(int i = 0; i < 10; i ++) {
            elements[i] = inventory.getSlot(i).getType();
        }
        hotbars[activeHotbar] = elements;
    }


    /**
     * Attempts to load hotbars from file
     */
    public void loadHotbars() {
        try {
            hotbarsFile = new File(BetterBuilding.getInstance().getResourcesFolder().getPath() + "/hotbars.smdat");
            saveToFile();

            Scanner scanner = new Scanner(hotbarsFile);
            int j = 0;
            while(scanner.hasNextLine() && j < 10) {
                String line = scanner.nextLine();
                String[] elements = line.split(",");
                for(int i = 0; i < 10; i ++) {
                    hotbars[j][i] = Short.parseShort(elements[i]);
                }
                j ++;
            }
            scanner.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Saves the current hotbars to file
     */
    public void saveToFile() throws IOException {
        if(hotbarsFile.exists()) hotbarsFile.delete();
        hotbarsFile.createNewFile();

        FileWriter writer = new FileWriter(hotbarsFile);
        for(int j = 0; j < 10; j ++) {
            for(int i = 0; i < 10; i ++) {
                writer.write(String.valueOf(hotbars[j][i]));
                if(i != 9) writer.write(",");
            }
            if(j != 9) writer.write("\n");
        }
        writer.close();
    }

    /**
     * @return The active hotbar elements
     */
    public short[] getActive() {
        return hotbars[activeHotbar];
    }

    /**
     * Updates the active hotbar
     */
    public void updateHotbar() {
        for(int i = 0; i < 10; i ++) {
            inventory.getSlot(i).setType(getActive()[i]);
        }
        barIndexText.setTextSimple(String.valueOf((activeHotbar + 1)));
    }

    /**
     * Cycles the active hotbar to the next slot
     */
    public void cycleNext() {
        saveActive();
        if(activeHotbar == 9) {
            activeHotbar = 0;
        } else {
            activeHotbar ++;
        }
        updateHotbar();
    }

    /**
     * Cycles the active hotbar to the previous slot
     */
    public void cyclePrevious() {
        saveActive();
        if(activeHotbar == 0) {
            activeHotbar = 9;
        } else {
            activeHotbar --;
        }
        updateHotbar();
    }

    /**
     * Sets the active hotbar to a specified slot
     */
    public void setActiveHotbar(int num) {
        int slot;
        if(num == 0) {
            slot = 9;
        } else {
            slot = num - 1;
        }
        saveActive();
        activeHotbar = slot;
        updateHotbar();
    }

    @Override
    public void activateDragging(boolean b) {

    }

    @Override
    public void drawDragging(WeaponSlotOverlayElement weaponSlotOverlayElement) {

    }

    @Override
    public Vector4i getConfigColor() {
        return null;
    }

    @Override
    public GUIPosition getConfigPosition() {
        return null;
    }

    @Override
    public Vector2f getConfigOffset() {
        return null;
    }

    @Override
    protected String getTag() {
        return null;
    }

    @Override
    public void drawToolTip() {

    }

    @Override
    public void onInit() {
        barIndexText = new GUITextOverlay(10, 10, FontLibrary.FontSize.BIG, getState());
        barIndexText.setTextSimple(String.valueOf((activeHotbar + 1)));
        barIndexText.setPos(GUIScale.S.defaultInset, GUIScale.S.scale(28), 0);

        bgIndexAnchor = new GUIAncor(getState(), GUIScale.S.scale(18) + (((int) Math.log10(activeHotbar + 1)) * GUIScale.S.scale(11)), GUIScale.S.scale(77));
        bgIndex = new GUIInnerBackground(getState(), bgIndexAnchor, 0);
        bgIndexAnchor.attach(bgIndex);
        bgIndex.attach(barIndexText);
        String p = getState().getGUIPath();

        upButton = new GUIOverlay(Controller.getResLoader().getSprite(p + "UI 16px-8x8-gui-"), getState());
        downButton = new GUIOverlay(Controller.getResLoader().getSprite(p + "UI 16px-8x8-gui-"), getState());

        upButton.setSpriteSubIndex(4);
        downButton.setSpriteSubIndex(5);
        bgIndex.attach(upButton);
        bgIndex.attach(downButton);

        upButton.setPos(GUIScale.S.scale(1), 0, 0);
        downButton.setPos(GUIScale.S.scale(1), bgIndexAnchor.getHeight() - downButton.getHeight(), 0);

        upButton.setMouseUpdateEnabled(true);
        downButton.setMouseUpdateEnabled(true);

        upButton.setCallback(new GUICallback() {

            @Override
            public void callback(GUIElement callingGuiElement, MouseEvent event) {
                if (event.pressedLeftMouse()) {
                    cycleNext();
                }
            }

            @Override
            public boolean isOccluded() {
                return false;
            }
        });

        downButton.setCallback(new GUICallback() {

            @Override
            public boolean isOccluded() {
                return false;
            }

            @Override
            public void callback(GUIElement callingGuiElement, MouseEvent event) {
                if (event.pressedLeftMouse()) {
                    cyclePrevious();
                }
            }
        });

        upButton.onInit();
        downButton.onInit();
        barIndexText.onInit();
    }

    @Override
    public void draw() {
        GlUtil.glPushMatrix();
        bgIndexAnchor.getPos().x = getWidth() - GUIScale.S.scale(157);
        bgIndexAnchor.getPos().y = GUIScale.S.scale(50);
        bgIndexAnchor.draw();
        GlUtil.glPopMatrix();
    }
}