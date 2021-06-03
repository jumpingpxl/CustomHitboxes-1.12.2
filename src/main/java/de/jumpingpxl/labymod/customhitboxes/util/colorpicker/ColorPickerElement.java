package de.jumpingpxl.labymod.customhitboxes.util.colorpicker;

import de.jumpingpxl.labymod.customhitboxes.util.Color;
import de.jumpingpxl.labymod.customhitboxes.util.dynamicelements.DynamicSettingsElement;
import de.jumpingpxl.labymod.customhitboxes.util.dynamicelements.Icon;
import net.labymod.utils.Consumer;
import net.minecraft.client.renderer.Tessellator;

public class ColorPickerElement extends DynamicSettingsElement {

	private static final ColorUtils COLOR_UTILS = new ColorUtils();

	private final Consumer<Integer> callback;
	private final Color color;
	private boolean mouseOverPreview;

	private ColorPickerElement(String elementName, Icon icon, Color color,
	                           Consumer<Integer> callback) {
		super(elementName, icon);
		this.color = color;
		this.callback = callback;
	}

	public static ColorPickerElement create(String elementName, Icon icon, Color color,
	                                        Consumer<Integer> callback) {
		return new ColorPickerElement(elementName, icon, color, callback);
	}

	@Override
	public void draw(int preX, int preY, int preMaxX, int preMaxY, int mouseX, int mouseY) {
		super.draw(preX, preY, preMaxX, preMaxY, mouseX, mouseY);

		int previewWidth = getObjectWidth();
		int previewHeight = 20;
		int previewX = maxX - previewWidth - 1;
		int previewY = y + 1;
		int previewMaxX = previewX + previewWidth;
		int previewMaxY = previewY + previewHeight;

		Tessellator tessellator = Tessellator.getInstance();
		COLOR_UTILS.drawTransparentBackground(tessellator, tessellator.getBuffer(), previewX, previewY,
				previewWidth, previewHeight);
		COLOR_UTILS.drawColorPreview(previewX, previewY, previewMaxX, previewMaxY, color.getColor());
		mouseOverPreview =
				mouseX > previewX && mouseX < previewMaxX && mouseY > previewY && mouseY < previewMaxY;

		this.mc.getTextureManager().bindTexture(buttonTextures);
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		if (mouseOverPreview) {
			mc.displayGuiScreen(
					ColorPickerGui.create(getDisplayName(), color.createCopy(), mc.currentScreen,
							newColor -> {
								color.setColor(newColor);
								callback.accept(newColor);
							}));
		}
	}
}
