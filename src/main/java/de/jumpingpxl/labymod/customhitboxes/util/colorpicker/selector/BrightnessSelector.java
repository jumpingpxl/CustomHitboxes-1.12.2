package de.jumpingpxl.labymod.customhitboxes.util.colorpicker.selector;

import de.jumpingpxl.labymod.customhitboxes.util.Color;
import de.jumpingpxl.labymod.customhitboxes.util.colorpicker.ColorUtils;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class BrightnessSelector extends ColorSelector {

	private BrightnessSelector(ColorUtils colorUtils, Color color, int x, int y, int width,
	                           int height, int textFieldX, List<ColorSelector> colorSelectorList) {
		super(colorUtils, color, x, y, width, height, textFieldX, colorSelectorList);
	}

	public static BrightnessSelector create(ColorUtils colorUtils, Color color, int x, int y,
	                                        int width, int height, int textFieldX,
	                                        List<ColorSelector> colorSelectorList) {
		return new BrightnessSelector(colorUtils, color, x, y, width, height, textFieldX,
				colorSelectorList);
	}

	@Override
	public void initTextField() {
		getTextField().setText(String.valueOf(getColor().getBrightness()));
		getTextField().setValidator(new NumberPredicate(100, getTextFieldResponse(newValue -> {
			if (getColor().getBrightness() != newValue) {
				getColor().setBrightness(newValue);
				getColor().setRgb();
			}
		})));
	}

	@Override
	public void updateTextField(Color previousColor) {
		if (getColor().getBrightness() != previousColor.getBrightness()) {
			getTextField().setText(String.valueOf(getColor().getBrightness()));
		}
	}

	@Override
	public void draw(Tessellator tessellator, BufferBuilder buffer) {
		renderText("Brightness");

		int rgb = getColor().hsbToRgb(getColor().getHue(), getColor().getSaturation(), 100)
				| 0xff000000;
		getColorUtils().drawGradientRect(getX(), getY(), getMaxX(), getMaxY(), 0xff000000, rgb,
				0xff000000, rgb);
	}

	@Override
	public void drawMarker(Tessellator tessellator, BufferBuilder buffer) {
		buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
		buffer.pos(getX() + getColor().getBrightness() * 64D / 100, getY(), 0).endVertex();
		buffer.pos(getX() + getColor().getBrightness() * 64D / 100, getMaxY(), 0).endVertex();
		tessellator.draw();
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY) {
		update(mouseX - getX());
	}

	@Override
	public void mouseDragged(int mouseX, int mouseY) {
		update(mouseX - getX());
	}

	private void update(int dx) {
		getColor().setBrightness(MathHelper.clamp(dx * 100 / 64, 0, 100));
		getColor().setRgb();
	}
}
