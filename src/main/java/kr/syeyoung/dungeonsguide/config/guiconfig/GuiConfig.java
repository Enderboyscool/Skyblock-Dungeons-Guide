/*
 *     Dungeons Guide - The most intelligent Hypixel Skyblock Dungeons Mod
 *     Copyright (C) 2021  cyoung06
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as published
 *     by the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package kr.syeyoung.dungeonsguide.config.guiconfig;

import com.google.common.base.Supplier;
import kr.syeyoung.dungeonsguide.features.AbstractFeature;
import kr.syeyoung.dungeonsguide.features.FeatureRegistry;
import kr.syeyoung.dungeonsguide.gui.MPanel;
import kr.syeyoung.dungeonsguide.gui.elements.MNavigatingPane;
import kr.syeyoung.dungeonsguide.gui.elements.MTabbedPane;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.BufferUtils;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

import java.awt.*;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.stream.Collectors;

public class GuiConfig extends GuiScreen {

    private final MPanel mainPanel = new MPanel();

    @Getter
    private final MNavigatingPane tabbedPane;

    private final Stack<String> history = new Stack();

    public GuiConfig() {
        MNavigatingPane tabbedPane = new MNavigatingPane();
        mainPanel.add(tabbedPane);
        tabbedPane.setBackground2(new Color(38, 38, 38, 255));

        tabbedPane.setPageGenerator(ConfigPanelCreator.INSTANCE);

        tabbedPane.addBookmarkRunnable("GUI Relocate", new Runnable() {
            @Override
            public void run() {
                Minecraft.getMinecraft().displayGuiScreen(new GuiGuiLocationConfig(GuiConfig.this, null));
            }
        });

        for (final Map.Entry<String, List<AbstractFeature>> cate: FeatureRegistry.getFeaturesByCategory().entrySet())
            if (!cate.getKey().equals("hidden")) {
                tabbedPane.addBookmark(cate.getKey(), "base." + cate.getKey());

                ConfigPanelCreator.map.put("base." + cate.getKey(), new Supplier<MPanel>() {
                    @Override
                    public MPanel get() {
                        return new FeatureEditPane(cate.getValue(), GuiConfig.this);
                    }
                });
            }
        tabbedPane.addBookmark("All", "base.all");

        ConfigPanelCreator.map.put("base.all", new Supplier<MPanel>() {
            @Override
            public MPanel get() {
                return new FeatureEditPane(FeatureRegistry.getFeatureList().stream().filter( a-> !a.getCategory().equals("hidden")).collect(Collectors.toList()), GuiConfig.this);
            }
        });
        this.tabbedPane = tabbedPane;
    }

    @Override
    public void initGui() {
        super.initGui();
        ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
        mainPanel.setBounds(new Rectangle(Math.min((scaledResolution.getScaledWidth() - 500) / 2, scaledResolution.getScaledWidth()), Math.min((scaledResolution.getScaledHeight() - 300) / 2, scaledResolution.getScaledHeight()),500,300));
    }
    FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(16);

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        try {
            ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
            GlStateManager.pushMatrix();
            GlStateManager.pushAttrib();
            GlStateManager.disableLighting();
            GlStateManager.disableFog();
            GL11.glDisable(GL11.GL_FOG);
            GlStateManager.color(1, 1, 1, 1);
            GlStateManager.disableDepth();
            GlStateManager.depthMask(false);
            mainPanel.render0(scaledResolution, new Point(0,0), new Rectangle(0,0,scaledResolution.getScaledWidth(),scaledResolution.getScaledHeight()), mouseX, mouseY, mouseX, mouseY, partialTicks);
            GlStateManager.enableDepth();
            GlStateManager.depthMask(true);
            GlStateManager.popAttrib();
            GlStateManager.popMatrix();
            GlStateManager.enableBlend();
            GlStateManager.enableLighting();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        mainPanel.keyTyped0(typedChar, keyCode);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        mainPanel.mouseClicked0(mouseX, mouseY,mouseX,mouseY, mouseButton);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        mainPanel.mouseReleased0(mouseX, mouseY,mouseX,mouseY, state);
    }

    @Override
    public void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        mainPanel.mouseClickMove0(mouseX,mouseY,mouseX,mouseY,clickedMouseButton,timeSinceLastClick);
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();

        int i = Mouse.getEventX() * this.width / this.mc.displayWidth;
        int j = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;

        int wheel = Mouse.getDWheel();
        if (wheel != 0) {
            mainPanel.mouseScrolled0(i, j,i,j, wheel);
        }
    }
}
