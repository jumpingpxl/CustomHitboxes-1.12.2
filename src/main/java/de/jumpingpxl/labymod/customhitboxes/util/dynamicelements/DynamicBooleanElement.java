package de.jumpingpxl.labymod.customhitboxes.util.dynamicelements;

import net.labymod.main.LabyMod;
import net.labymod.utils.DrawUtils;
import net.labymod.utils.ModColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.util.function.Consumer;

public class DynamicBooleanElement extends DynamicSettingsElement {

	protected static final ResourceLocation BUTTON_TEXTURES = new ResourceLocation(
			"textures/gui/widgets.png");
	private static final DrawUtils DRAW_UTILS = LabyMod.getInstance().getDrawUtils();
	private static final String ENABLED = "ON";
	private static final String DISABLED = "OFF";

	private final ToggleButton toggleButton;
	private final Consumer<Boolean> toggleListener;
	private boolean currentValue;

	private DynamicBooleanElement(String displayName, Icon icon, boolean currentValue,
	                              Consumer<Boolean> toggleListener) {
		super(displayName, icon);

		this.currentValue = currentValue;
		this.toggleListener = toggleListener;

		toggleButton = new ToggleButton(this, getObjectWidth(), getEntryHeight() - 2, () -> {
			this.currentValue = !this.currentValue;
			this.toggleListener.accept(this.currentValue);
		});
	}

	public static DynamicBooleanElement create(String displayName, Icon icon, boolean currentValue,
	                                           Consumer<Boolean> toggleListener) {
		return new DynamicBooleanElement(displayName, icon, currentValue, toggleListener);
	}

	@Override
	public void draw(int prevX, int prevY, int prevMaxX, int prevMaxY, int mouseX, int mouseY) {
		super.draw(prevX, prevY, prevMaxX, prevMaxY, mouseX, mouseY);
		toggleButton.drawButton(Minecraft.getMinecraft(), mouseX, mouseY, 0F);
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		if (toggleButton.isMouseOver()) {
			toggleButton.onClick();
			toggleButton.playPressSound(Minecraft.getMinecraft().getSoundHandler());
		}
	}

	private static class ToggleButton extends GuiButton {

		private final DynamicBooleanElement settingsElement;
		private final Runnable onClick;

		public ToggleButton(DynamicBooleanElement settingsElement, int width, int height,
		                    Runnable onClick) {
			super(-2, 0, 0, width, height, "");

			this.settingsElement = settingsElement;
			this.onClick = onClick;
		}

		@Override
		public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
			int buttonWidth = width;

			enabled = false;
			x = settingsElement.maxX - buttonWidth - 1;
			y = settingsElement.y + 1;
			super.drawButton(mc, mouseX, mouseY, partialTicks);
			enabled = true;

			String color = (settingsElement.currentValue ? ModColor.WHITE.toString()
					: ModColor.GRAY.toString());
			String displayString =
					(isMouseOver() ? ModColor.YELLOW.toString() : color) + (settingsElement.currentValue
							? ENABLED : DISABLED);
			DRAW_UTILS.drawCenteredString(displayString,
					x + (buttonWidth - 4) / 2D + (settingsElement.currentValue ? 0 : 6),
					settingsElement.y + height / 2D - 3);

			mc.getTextureManager().bindTexture(BUTTON_TEXTURES);
			int valuePosX = (settingsElement.currentValue ? settingsElement.maxX - 8
					: settingsElement.maxX - buttonWidth) - 1;
			int red = (settingsElement.currentValue ? 85 : 255);
			int green = (settingsElement.currentValue ? 255 : 85);
			int blue = 85;
			GlStateManager.color(red / 255F, green / 255F, blue / 255F);
			DRAW_UTILS.drawTexturedModalRect(valuePosX, settingsElement.y + 1D, 0D, 66D, 4D, 20D);
			DRAW_UTILS.drawTexturedModalRect(valuePosX + 4D, settingsElement.y + 1D, 196.0D, 66.0D, 4.0D,
					20.0D);
			DRAW_UTILS.drawRectangle(settingsElement.x - 1, settingsElement.y, settingsElement.x,
					settingsElement.maxY, settingsElement.currentValue ? ModColor.toRGB(20, 120, 20, 120)
							: ModColor.toRGB(120, 20, 20, 120));
		}

		public void onClick() {
			onClick.run();
		}
	}
}
