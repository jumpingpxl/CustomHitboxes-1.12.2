package de.jumpingpxl.labymod.customhitboxes.util.colorpicker;

import com.google.common.collect.Lists;
import de.jumpingpxl.labymod.customhitboxes.util.Color;
import de.jumpingpxl.labymod.customhitboxes.util.colorpicker.selector.AlphaSelector;
import de.jumpingpxl.labymod.customhitboxes.util.colorpicker.selector.BlueSelector;
import de.jumpingpxl.labymod.customhitboxes.util.colorpicker.selector.BrightnessSelector;
import de.jumpingpxl.labymod.customhitboxes.util.colorpicker.selector.ColorSelector;
import de.jumpingpxl.labymod.customhitboxes.util.colorpicker.selector.GreenSelector;
import de.jumpingpxl.labymod.customhitboxes.util.colorpicker.selector.HexSelector;
import de.jumpingpxl.labymod.customhitboxes.util.colorpicker.selector.HueSelector;
import de.jumpingpxl.labymod.customhitboxes.util.colorpicker.selector.RedSelector;
import de.jumpingpxl.labymod.customhitboxes.util.colorpicker.selector.SaturationSelector;
import de.jumpingpxl.labymod.customhitboxes.util.colorpicker.selector.WheelSelector;
import net.labymod.main.LabyMod;
import net.labymod.utils.DrawUtils;
import net.labymod.utils.ModColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class ColorPickerGui extends GuiScreen {

	private static final ColorUtils COLOR_UTILS = new ColorUtils();
	private static final DrawUtils DRAW_UTILS = LabyMod.getInstance().getDrawUtils();
	private static final String TITLE_PREFIX = "Color Picker for ";

	private final List<ColorSelector> colorSelectorList;
	private final String colorPickerTitle;
	private final Consumer<Integer> callback;
	private final GuiScreen backgroundScreen;
	private final Color color;
	private ColorSelector clickedSelector;

	private int x;
	private int y;
	private int maxX;
	private int maxY;
	private int centerX;

	private GuiButton cancelButton;

	private ColorPickerGui(String colorPickerTitle, Color color, GuiScreen backgroundScreen,
	                       Consumer<Integer> callback) {
		this.colorPickerTitle = TITLE_PREFIX + ModColor.removeColor(colorPickerTitle);
		this.color = color;
		this.callback = callback;
		this.backgroundScreen = backgroundScreen;

		colorSelectorList = Lists.newArrayList();
	}

	public static ColorPickerGui create(String title, Color color, GuiScreen backgroundScreen,
	                                    Consumer<Integer> callback) {
		return new ColorPickerGui(title, color, backgroundScreen, callback);
	}

	@Override
	public void initGui() {
		super.initGui();
		backgroundScreen.width = this.width;
		backgroundScreen.height = this.height;
		backgroundScreen.initGui();

		centerX = this.width / 2;
		x = centerX - 175;
		y = this.height / 3 - 50;
		maxX = centerX + 175;
		maxY = this.height / 3 + 140;
		int centerY = this.height / 3 + 30;

		GuiButton doneButton = new GuiButton(0, centerX - 100, maxY - 25, 98, 20, "§aDone");

		cancelButton = new GuiButton(1, centerX + 2, maxY - 25, 98, 20, "§cCancel");

		buttonList.add(doneButton);
		buttonList.add(cancelButton);

		colorSelectorList.clear();
		colorSelectorList.add(WheelSelector.create(COLOR_UTILS, color, centerX, y + 70));

		int leftX = centerX - 124;
		colorSelectorList.add(
				HexSelector.create(COLOR_UTILS, color, leftX - 44, centerY - 49, 0, 0, leftX - 44,
						colorSelectorList));
		colorSelectorList.add(
				HueSelector.create(COLOR_UTILS, color, leftX, centerY - 15, 64, 20, leftX - 45,
						colorSelectorList));
		colorSelectorList.add(
				SaturationSelector.create(COLOR_UTILS, color, leftX, centerY + 20, 64, 20, leftX - 45,
						colorSelectorList));
		colorSelectorList.add(
				BrightnessSelector.create(COLOR_UTILS, color, leftX, centerY + 55, 64, 20, leftX - 45,
						colorSelectorList));

		int rightX = centerX + 60;
		colorSelectorList.add(
				RedSelector.create(COLOR_UTILS, color, rightX, centerY - 49, 64, 20, rightX + 69,
						colorSelectorList));
		colorSelectorList.add(
				GreenSelector.create(COLOR_UTILS, color, rightX, centerY - 15, 64, 20, rightX + 69,
						colorSelectorList));
		colorSelectorList.add(
				BlueSelector.create(COLOR_UTILS, color, rightX, centerY + 20, 64, 20, rightX + 69,
						colorSelectorList));
		colorSelectorList.add(
				AlphaSelector.create(COLOR_UTILS, color, rightX, centerY + 55, 64, 20, rightX + 69,
						colorSelectorList));

		for (ColorSelector colorSelector : colorSelectorList) {
			GuiTextField textField = colorSelector.getTextField();
			if (Objects.nonNull(textField)) {
				colorSelector.initTextField();
			}
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		backgroundScreen.drawScreen(0, 0, partialTicks);

		drawRect(0, 0, this.width, this.height, -2147483648);
		drawRect(x, y, maxX, maxY, -2147483648);
		DRAW_UTILS.drawCenteredString(colorPickerTitle, centerX, y + 5D);

		super.drawScreen(mouseX, mouseY, partialTicks);

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();

		for (ColorSelector colorSelector : colorSelectorList) {
			colorSelector.renderTextField(tessellator, buffer, mouseX, mouseY);
		}

		GlStateManager.disableLighting();
		GlStateManager.disableFog();
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
		GlStateManager.disableTexture2D();
		GlStateManager.shadeModel(GL11.GL_SMOOTH);

		for (ColorSelector colorSelector : colorSelectorList) {
			colorSelector.draw(tessellator, buffer);
		}

		COLOR_UTILS.drawTransparentBackground(tessellator, buffer, centerX - 45, maxY - 60, 90, 25);
		COLOR_UTILS.drawColorPreview(centerX - 45, maxY - 60, centerX + 45, maxY - 35,
				color.getColor());

		GlStateManager.tryBlendFuncSeparate(GL11.GL_ONE_MINUS_DST_COLOR, GL11.GL_ONE_MINUS_SRC_COLOR
				, 1,
				0);
		GlStateManager.enableBlend();
		GlStateManager.color(1, 1, 1, 1);
		GlStateManager.disableTexture2D();

		for (ColorSelector colorSelector : colorSelectorList) {
			colorSelector.drawMarker(tessellator, buffer);
		}

		GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		for (ColorSelector colorSelector : colorSelectorList) {
			if (Objects.nonNull(colorSelector.getTextField())) {
				colorSelector.getTextField().textboxKeyTyped(typedChar, keyCode);
			}
		}

		if (keyCode == 1) {
			actionPerformed(cancelButton);
		} else {
			super.keyTyped(typedChar, keyCode);
		}
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int button) throws IOException {
		Color previousColor = color.createCopy();
		if (button == 0) {
			for (ColorSelector colorSelector : colorSelectorList) {
				if (colorSelector.isMouseOver(mouseX, mouseY)) {
					clickedSelector = colorSelector;
					colorSelector.mouseClicked(mouseX, mouseY);
					break;
				}

				if (Objects.nonNull(colorSelector.getTextField())) {
					colorSelector.getTextField().mouseClicked(mouseX, mouseY, button);
				}
			}
		}

		if (Objects.nonNull(clickedSelector)) {
			for (ColorSelector colorSelector : colorSelectorList) {
				colorSelector.updateTextField(previousColor);
			}
		}

		super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		if (state == 0 && Objects.nonNull(clickedSelector)) {
			clickedSelector = null;
		}

		super.mouseReleased(mouseX, mouseY, state);
	}

	@Override
	protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton,
	                              long timeSinceLastClick) {
		Color previousColor = color.createCopy();
		if (Objects.nonNull(clickedSelector)) {
			clickedSelector.mouseDragged(mouseX, mouseY);

			for (ColorSelector colorSelector : colorSelectorList) {
				colorSelector.updateTextField(previousColor);
			}
		}

		super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		if (button.id == 0) {
			callback.accept(color.getColor());
		}

		Minecraft.getMinecraft().displayGuiScreen(backgroundScreen);
	}

	@Override
	public void updateScreen() {
		for (ColorSelector colorSelector : colorSelectorList) {
			colorSelector.tick();
		}
	}
}
