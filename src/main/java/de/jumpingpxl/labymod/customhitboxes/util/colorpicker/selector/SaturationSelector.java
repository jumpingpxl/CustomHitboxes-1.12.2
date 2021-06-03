package de.jumpingpxl.labymod.customhitboxes.util.colorpicker.selector;

import de.jumpingpxl.labymod.customhitboxes.util.Color;
import de.jumpingpxl.labymod.customhitboxes.util.colorpicker.ColorUtils;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class SaturationSelector extends ColorSelector {

	private SaturationSelector(ColorUtils colorUtils, Color color, int x, int y, int width,
	                           int height, int textFieldX, List<ColorSelector> colorSelectorList) {
		super(colorUtils, color, x, y, width, height, textFieldX, colorSelectorList);
	}

	public static SaturationSelector create(ColorUtils colorUtils, Color color, int x, int y,
	                                        int width, int height, int textFieldX,
	                                        List<ColorSelector> colorSelectorList) {
		return new SaturationSelector(colorUtils, color, x, y, width, height, textFieldX,
				colorSelectorList);
	}

	@Override
	public void initTextField() {
		getTextField().setText(String.valueOf(getColor().getSaturation()));
		getTextField().setValidator(new NumberPredicate(100, getTextFieldResponse(newValue -> {
			if (getColor().getSaturation() != newValue) {
				getColor().setSaturation(newValue);
				getColor().setRgb();
			}
		})));
	}

	@Override
	public void updateTextField(Color previousColor) {
		if (getColor().getSaturation() != previousColor.getSaturation()) {
			getTextField().setText(String.valueOf(getColor().getSaturation()));
		}
	}

	@Override
	public void draw(Tessellator tessellator, BufferBuilder buffer) {
		renderText("Saturation");

		int rgb = getColor().hsbToRgb(getColor().getHue(), 0, getColor().getBrightness()) | 0xff000000;
		int rgbSaturated = getColor().hsbToRgb(getColor().getHue(), 100, getColor().getBrightness())
				| 0xff000000;
		getColorUtils().drawGradientRect(getX(), getY(), getMaxX(), getMaxY(), rgb, rgbSaturated, rgb,
				rgbSaturated);
	}

	@Override
	public void drawMarker(Tessellator tessellator, BufferBuilder buffer) {
		buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
		buffer.pos(getX() + getColor().getSaturation() * 64D / 100, getY(), 0).endVertex();
		buffer.pos(getX() + getColor().getSaturation() * 64D / 100, getMaxY(), 0).endVertex();
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
		getColor().setSaturation(MathHelper.clamp(dx * 100 / 64, 0, 100));
		getColor().setRgb();
	}
}
