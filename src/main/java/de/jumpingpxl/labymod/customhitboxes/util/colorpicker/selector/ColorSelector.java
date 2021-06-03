package de.jumpingpxl.labymod.customhitboxes.util.colorpicker.selector;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import de.jumpingpxl.labymod.customhitboxes.util.Color;
import de.jumpingpxl.labymod.customhitboxes.util.colorpicker.ColorUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.regex.Pattern;

public abstract class ColorSelector {

	private final ColorUtils colorUtils;
	private final Color color;
	private final List<ColorSelector> colorSelectorList;
	private final int x;
	private final int y;
	private final int maxX;
	private final int maxY;
	private final int width;
	private final int height;
	private final int textFieldX;
	private GuiTextField textField;
	private int textFieldWidth;

	protected ColorSelector(ColorUtils colorUtils, Color color, int x, int y) {
		this(colorUtils, color, x, y, 0, 0, -1, Lists.newArrayList());
	}

	protected ColorSelector(ColorUtils colorUtils, Color color, int x, int y, int width, int height,
	                        int textFieldX, List<ColorSelector> colorSelectorList) {
		this.colorUtils = colorUtils;
		this.color = color;
		this.x = x;
		this.y = y;
		this.maxX = x + width;
		this.maxY = y + height;
		this.width = width;
		this.height = height;
		this.textFieldX = textFieldX;
		this.colorSelectorList = colorSelectorList;

		if (textFieldX != -1) {
			textField = createTextField(40, 18);
		}
	}

	public abstract void initTextField();

	public abstract void updateTextField(Color previousColor);

	public abstract void draw(Tessellator tessellator, BufferBuilder buffer);

	public abstract void drawMarker(Tessellator tessellator, BufferBuilder buffer);

	public abstract void mouseClicked(int mouseX, int mouseY);

	public abstract void mouseDragged(int mouseX, int mouseY);

	public void tick() {
		if (Objects.nonNull(textField)) {
			textField.updateCursorCounter();
		}
	}

	public void renderTextField(Tessellator tessellator, BufferBuilder buffer, int mouseX,
	                            int mouseY) {
		if (Objects.nonNull(textField)) {
			textField.drawTextBox();
		}
	}

	public boolean isMouseOver(int mouseX, int mouseY) {
		return mouseX > x && mouseX < maxX && mouseY > y && mouseY < maxY;
	}

	protected void renderText(String text) {
		int width = this.width == 0 ? textFieldWidth * 2 + 5 : this.width;
		colorUtils.getDrawUtils().drawCenteredString(text,
				(x < textFieldX ? (width + textFieldWidth + 5) / 2D + x
						: (width - textFieldWidth - 5) / 2D + x), y - 10D);
	}

	protected Consumer<String> getTextFieldResponse(IntConsumer newValue) {
		return newText -> {
			Color previousColor = color.createCopy();
			newValue.accept(Integer.parseInt(newText.isEmpty() ? "0" : newText));
			for (ColorSelector colorSelector : colorSelectorList) {
				colorSelector.updateTextField(previousColor);
			}
		};
	}

	protected GuiTextField createTextField(int width, int height) {
		textFieldWidth = width;
		return new GuiTextField(0, Minecraft.getMinecraft().fontRenderer, textFieldX, y + 1, width,
				height);
	}

	protected ColorUtils getColorUtils() {
		return colorUtils;
	}

	protected Color getColor() {
		return color;
	}

	protected List<ColorSelector> getColorSelectorList() {
		return colorSelectorList;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getMaxX() {
		return maxX;
	}

	public int getMaxY() {
		return maxY;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getTextFieldX() {
		return textFieldX;
	}

	public int getTextFieldWidth() {
		return textFieldWidth;
	}

	public GuiTextField getTextField() {
		return textField;
	}

	protected class NumberPredicate implements Predicate<String> {

		private static final String NUMBERS_ONLY = "[0-9]";
		private final int max;
		private final Consumer<String> response;

		public NumberPredicate(int max, Consumer<String> response) {
			this.max = max;
			this.response = response;
		}

		@Override
		public boolean apply(String input) {
			StringBuilder acceptedInput = new StringBuilder();
			if (input != null) {
				for (char character : input.toCharArray()) {
					if (Pattern.matches(NUMBERS_ONLY, String.valueOf(character))) {
						acceptedInput.append(character);
					} else {
						return false;
					}
				}

				if (!input.equals(acceptedInput.toString())) {
					textField.setText(acceptedInput.toString());
				}
			}

			input = acceptedInput.toString();
			int number = input.isEmpty() ? 0 : Integer.parseInt(input);
			if (number >= 0 && number <= max) {
				if (Objects.nonNull(response)) {
					response.accept(String.valueOf(number));
				}

				return true;
			}

			return false;
		}
	}
}
