package de.jumpingpxl.labymod.customhitboxes.util.dynamicelements;

import com.google.common.collect.Lists;
import net.labymod.main.LabyMod;
import net.labymod.settings.elements.SettingsElement;
import net.labymod.utils.DrawUtils;
import net.labymod.utils.ModColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;

import java.util.List;
import java.util.Objects;

public class DynamicSettingsElement extends SettingsElement {

	private static final DrawUtils DRAW_UTILS = LabyMod.getInstance().getDrawUtils();
	private static final int OBJECT_WIDTH = 50;

	private final Icon icon;
	private final int entryHeight;
	private final SubSettingsButton subSettingsButton;
	private final List<SettingsElement> subSettingList;
	protected int x;
	protected int y;
	protected int maxX;
	protected int maxY;
	private boolean subSettingsExpanded;
	private boolean subSetting;

	protected DynamicSettingsElement(String displayName, Icon icon, int entryHeight) {
		super(displayName, null);

		this.entryHeight = entryHeight;
		this.icon = icon;

		subSettingList = Lists.newArrayList();

		subSettingsButton = new SubSettingsButton(this, 0, 0, 0,
				() -> subSettingsExpanded = !subSettingsExpanded);
		subSetting = false;
	}

	protected DynamicSettingsElement(String displayName, Icon icon) {
		this(displayName, icon, 22);
	}

	public static DynamicSettingsElement create(String displayName, Icon icon, int entryHeight) {
		return new DynamicSettingsElement(displayName, icon, entryHeight);
	}

	public static DynamicSettingsElement create(String displayName, Icon icon) {
		return new DynamicSettingsElement(displayName, icon);
	}

	@Override
	public void draw(int preX, int preY, int preMaxX, int preMaxY, int mouseX, int mouseY) {
		x = preX;
		y = preY;
		maxX = preMaxX;
		maxY = preMaxY;

		if (!isSubSetting()) {
			x -= 20;
			maxX += 20;
		}

		super.draw(x, y, maxX, maxY, mouseX, mouseY);

		if (isSubSetting() || subSettingsExpanded) {
			DRAW_UTILS.drawRectangle(x - 1, y - 1, maxX + 1,
					y + (subSettingsExpanded ? entryHeight : getEntryHeight()) + 1,
					ModColor.toRGB(60, 60, 60, 60));
		}

		if (Objects.nonNull(displayName)) {
			DRAW_UTILS.drawRectangle(x, y, maxX, y + entryHeight,
					ModColor.toRGB(80, 80, 80, isSubSetting() || subSettingsExpanded ? 80 : 60));
			DRAW_UTILS.drawString(getDisplayName(), x + 24D, y + entryHeight / 2D - 3.5D);
		}

		if (Objects.nonNull(icon)) {
			if (icon.getIcon() instanceof ItemStack) {
				DRAW_UTILS.drawItem(icon.getItemStack(), x + 4D, y + 3D, null);
			} else {

			}
		}

		if (!subSettingList.isEmpty()) {
			subSettingsButton.x = maxX - subSettingsButton.getButtonWidth() - 1;
			subSettingsButton.y = y + 1;
			subSettingsButton.drawButton(mc, mouseX, mouseY, 0F);
		}

		if (subSettingsExpanded) {
			drawSubSettings(mouseX, mouseY);
		}
	}

	@Override
	public void drawDescription(int x, int y, int screenWidth) {
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		if (!subSettingList.isEmpty()) {
			if (subSettingsButton.isMouseOver()) {
				subSettingsButton.onClick();
				subSettingsButton.playPressSound(Minecraft.getMinecraft().getSoundHandler());
			}

			for (SettingsElement settingsElement : subSettingList) {
				if (settingsElement.isMouseOver()) {
					settingsElement.mouseClicked(mouseX, mouseY, mouseButton);
				}
			}
		}
	}

	@Override
	public void mouseRelease(int mouseX, int mouseY, int mouseButton) {
	}

	@Override
	public void mouseClickMove(int mouseX, int mouseY, int mouseButton) {
	}

	@Override
	public void keyTyped(char c, int i) {

	}

	@Override
	public void unfocus(int mouseX, int mouseY, int mouseButton) {
	}

	@Override
	public int getEntryHeight() {
		int height = entryHeight;
		if (subSettingsExpanded) {
			height += subSettingList.size() * 2 + 8;
			for (SettingsElement settingsElement : subSettingList) {
				height += settingsElement.getEntryHeight();
			}
		}

		return height;
	}

	private void drawSubSettings(int mouseX, int mouseY) {
		int markerX = x + 13;
		int markerY = y + entryHeight + subSettingList.get(0).getEntryHeight() / 2 + 1;
		int entryY = y + entryHeight + 2;
		for (SettingsElement settingsElement : subSettingList) {
			if (Objects.nonNull(settingsElement.getDisplayName())) {
				DRAW_UTILS.drawRectangle(markerX, markerY, markerX + 11, markerY + 2,
						ModColor.toRGB(60, 60, 60, 120));
			}

			markerY += settingsElement.getEntryHeight() + 2;
			settingsElement.draw(x + 24, entryY, maxX, entryY + settingsElement.getEntryHeight(), mouseX,
					mouseY);
			entryY += settingsElement.getEntryHeight() + 2;
		}

		DRAW_UTILS.drawRectangle(x + 11, y + entryHeight, x + 13,
				markerY - subSettingList.get(subSettingList.size() - 1).getEntryHeight(),
				ModColor.toRGB(60, 60, 60, 120));
	}

	public DynamicSettingsElement addSubSettings(SettingsElement... settingsElements) {
		for (SettingsElement settingsElement : settingsElements) {
			if (settingsElement instanceof DynamicSettingsElement) {
				((DynamicSettingsElement) settingsElement).subSetting = true;
			}

			subSettingList.add(settingsElement);
		}

		return this;
	}

	public int getObjectWidth() {
		return OBJECT_WIDTH;
	}

	public boolean isSubSetting() {
		return subSetting;
	}

	private static class SubSettingsButton extends GuiButton {

		private final DynamicSettingsElement settingsElement;
		private final Runnable onClick;

		public SubSettingsButton(DynamicSettingsElement settingsElement, int buttonId, int x, int y,
		                         Runnable onClick) {
			super(buttonId, x, y, 20, 20, "");

			this.settingsElement = settingsElement;
			this.onClick = onClick;
		}

		@Override
		public void drawButton(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {
			this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width
					&& mouseY < this.y + this.height;

			FontRenderer fontrenderer = minecraft.fontRenderer;
			minecraft.getTextureManager().bindTexture(buttonTextures);
			GlStateManager.color(1.0F, 1.0F, 1.0F);
			int i = this.getHoverState(this.hovered);
			GlStateManager.enableBlend();
			GlStateManager.enableDepth();
			this.drawTexturedModalRect(this.x, this.y, 0, 46 + i * 20, this.width / 2, this.height);
			this.drawTexturedModalRect(this.x + this.width / 2, this.y, 200 - this.width / 2,
					46 + i * 20,
					this.width / 2, this.height);
			this.mouseDragged(minecraft, mouseX, mouseY);
			int j = this.enabled ? 16777215 : 10526880;
			drawCenteredString(fontrenderer, settingsElement.subSettingsExpanded ? "V" : ">",
					this.x + this.width / 2, this.y + (this.height - 8) / 2, j);
		}

		public void onClick() {
			onClick.run();
		}
	}
}
