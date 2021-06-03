package de.jumpingpxl.labymod.customhitboxes.util.colorpicker.selector;

import de.jumpingpxl.labymod.customhitboxes.util.Color;
import de.jumpingpxl.labymod.customhitboxes.util.colorpicker.ColorUtils;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class RedSelector extends ColorSelector {

	private RedSelector(ColorUtils colorUtils, Color color, int x, int y, int width, int height,
	                    int textFieldX, List<ColorSelector> colorSelectorList) {
		super(colorUtils, color, x, y, width, height, textFieldX, colorSelectorList);
	}

	public static RedSelector create(ColorUtils colorUtils, Color color, int x, int y, int width,
	                                 int height, int textFieldX,
	                                 List<ColorSelector> colorSelectorList) {
		return new RedSelector(colorUtils, color, x, y, width, height, textFieldX, colorSelectorList);
	}

	@Override
	public void initTextField() {
		getTextField().setText(String.valueOf(getColor().getRed()));
		getTextField().setValidator(new NumberPredicate(255, getTextFieldResponse(newValue -> {
			int rgb = getColor().getRgb();
			rgb &= 0x00ffff;
			rgb |= newValue << 16;
			if (getColor().getRgb() != rgb) {
				getColor().setRgb(rgb);
			}
		})));
	}

	@Override
	public void updateTextField(Color previousColor) {
		if (getColor().getRed() != previousColor.getRed()) {
			getTextField().setText(String.valueOf(getColor().getRed()));
		}
	}

	@Override
	public void draw(Tessellator tessellator, BufferBuilder buffer) {
		renderText("Red");

		int rgb = getColor().getRgb() | 0xff000000;
		getColorUtils().drawGradientRect(getX(), getY(), getMaxX(), getMaxY(), rgb & 0xff00ffff,
				rgb | 0x00ff0000, rgb & 0xff00ffff, rgb | 0x00ff0000);
	}

	@Override
	public void drawMarker(Tessellator tessellator, BufferBuilder buffer) {
		buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
		buffer.pos(getX() + ((getColor().getRgb() & 0xff0000) >> 16) * 64D / 255, getY(), 0)
				.endVertex();
		buffer.pos(getX() + ((getColor().getRgb() & 0xff0000) >> 16) * 64D / 255, getMaxY(), 0)
				.endVertex();
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
		dx = MathHelper.clamp(dx, 0, 64);
		int rgb = getColor().getRgb();
		rgb &= 0x00ffff;
		rgb |= (dx * 255 / 64) << 16;
		getColor().setRgb(rgb);
	}
}