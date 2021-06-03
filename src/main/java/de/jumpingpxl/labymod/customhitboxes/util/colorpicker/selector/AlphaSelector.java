package de.jumpingpxl.labymod.customhitboxes.util.colorpicker.selector;

import de.jumpingpxl.labymod.customhitboxes.util.Color;
import de.jumpingpxl.labymod.customhitboxes.util.colorpicker.ColorUtils;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class AlphaSelector extends ColorSelector {

	private AlphaSelector(ColorUtils colorUtils, Color color, int x, int y, int width, int height,
	                      int textFieldX, List<ColorSelector> colorSelectorList) {
		super(colorUtils, color, x, y, width, height, textFieldX, colorSelectorList);
	}

	public static AlphaSelector create(ColorUtils colorUtils, Color color, int x, int y, int width,
	                                   int height, int textFieldX,
	                                   List<ColorSelector> colorSelectorList) {
		return new AlphaSelector(colorUtils, color, x, y, width, height, textFieldX,
				colorSelectorList);
	}

	@Override
	public void initTextField() {
		getTextField().setText(String.valueOf(getColor().getAlpha()));
		getTextField().setValidator(new NumberPredicate(255, getTextFieldResponse(newValue -> {
			if (getColor().getAlpha() != newValue) {
				getColor().setAlpha(newValue);
			}
		})));
	}

	@Override
	public void updateTextField(Color previousColor) {
		if (getColor().getAlpha() != previousColor.getAlpha()) {
			getTextField().setText(String.valueOf(getColor().getAlpha()));
		}
	}

	@Override
	public void draw(Tessellator tessellator, BufferBuilder buffer) {
		renderText("Alpha");

		GlStateManager.enableTexture2D();
		getColorUtils().drawTransparentBackground(tessellator, buffer, getX(), getY(), getWidth(),
				getHeight());

		int rgb = getColor().getRgb();
		getColorUtils().drawGradientRect(getX(), getY(), getMaxX(), getMaxY(), rgb, rgb | 0xff000000,
				rgb, rgb | 0xff000000);
		GlStateManager.disableTexture2D();
	}

	@Override
	public void drawMarker(Tessellator tessellator, BufferBuilder buffer) {
		buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
		buffer.pos(getX() + getColor().getAlpha() * 64D / 255, getY(), 0).endVertex();
		buffer.pos(getX() + getColor().getAlpha() * 64D / 255, getMaxY(), 0).endVertex();
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
		getColor().setAlpha(dx * 255 / 64);
	}
}