package de.jumpingpxl.labymod.customhitboxes.util.colorpicker.selector;

import de.jumpingpxl.labymod.customhitboxes.util.Color;
import de.jumpingpxl.labymod.customhitboxes.util.colorpicker.ColorUtils;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;

import java.util.List;
import java.util.regex.Pattern;

public class HexSelector extends ColorSelector {

	private boolean validHex;

	private HexSelector(ColorUtils colorUtils, Color color, int x, int y, int width, int height,
	                    int textFieldX, List<ColorSelector> colorSelectorList) {
		super(colorUtils, color, x, y, width, height, textFieldX, colorSelectorList);
	}

	public static HexSelector create(ColorUtils colorUtils, Color color, int x, int y, int width,
	                                 int height, int textFieldX,
	                                 List<ColorSelector> colorSelectorList) {
		return new HexSelector(colorUtils, color, x, y, width, height, textFieldX, colorSelectorList);
	}

	@Override
	public void initTextField() {
		validHex = true;
		getTextField().setText(String.format("#%06X", (0xFFFFFF & getColor().getRgb())));
		getTextField().setValidator(input -> {
			StringBuilder acceptedInput = new StringBuilder();
			char[] charArray = input.toCharArray();
			for (int i = 0; i < input.length(); i++) {
				char character = charArray[i];
				if (Pattern.matches("[" + (i == 0 ? "#" : "") + "0-9a-zA-Z]", String.valueOf(character))) {
					acceptedInput.append(character);
				} else {
					return false;
				}
			}

			if (!input.equals(acceptedInput.toString())) {
				getTextField().setText(acceptedInput.toString());
			}

			input = acceptedInput.toString();
			if (input.length() > 0 && input.toCharArray()[0] != '#') {
				input = "#" + input;
			}

			if (input.length() == 7) {
				try {
					int newValue = Integer.decode(input);
					if (getColor().getRgb() != newValue) {
						Color previousColor = getColor().createCopy();
						getColor().setRgb(newValue);
						for (ColorSelector colorSelector : getColorSelectorList()) {
							colorSelector.updateTextField(previousColor);
						}
					}

					validHex = true;
				} catch (Exception e) {
					e.printStackTrace();
					validHex = false;
				}
			} else {
				validHex = false;
			}

			return input.length() > 0 && input.length() <= (
					acceptedInput.toString().toCharArray()[0] == '#' ? 7 : 6);
		});
	}

	@Override
	public void updateTextField(Color previousColor) {
		String hex = String.format("#%06X", (0xFFFFFF & getColor().getRgb()));
		if (!hex.equals(getTextField().getText())) {
			getTextField().setText(hex);
		}
	}

	@Override
	public void draw(Tessellator tessellator, BufferBuilder buffer) {
		renderText("Hex");
	}

	@Override
	public void drawMarker(Tessellator tessellator, BufferBuilder buffer) {
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY) {
	}

	@Override
	public void mouseDragged(int mouseX, int mouseY) {
	}

	@Override
	protected GuiTextField createTextField(int width, int height) {
		return super.createTextField(106, 16);
	}

	@Override
	public void renderTextField(Tessellator tessellator, BufferBuilder buffer, int mouseX,
	                            int mouseY) {
		Color color = validHex ? Color.fromRgba(85, 255, 85, 100) : Color.fromRgba(255, 85, 85, 100);
		int minX = getTextField().x - 2;
		int minY = getTextField().y - 2;
		int maxX = minX + getTextField().getWidth() + 12;
		int maxY = minY + 20;
		Gui.drawRect(minX, minY, maxX, maxY, color.getColor());

		super.renderTextField(tessellator, buffer, mouseX, mouseY);
	}

	@Override
	public boolean isMouseOver(int mouseX, int mouseY) {
		return false;
	}
}
