package de.jumpingpxl.labymod.customhitboxes;

import de.jumpingpxl.labymod.customhitboxes.util.Settings;
import net.labymod.api.LabyModAddon;
import net.labymod.settings.elements.SettingsElement;

import java.util.List;

/**
 * @author Nico (JumpingPxl) Middendorf
 * @date 04.06.2021
 * @project LabyMod-Addon: CustomHitboxes-1.12.2
 */

public class CustomHitboxes extends LabyModAddon {

	public static final String VERSION = "2";

	private static Settings settings;

	public static Settings getSettings() {
		return settings;
	}

	@Override
	public void onEnable() {
		settings = new Settings(this);
	}

	@Override
	public void loadConfig() {
		settings.loadConfig();
	}

	@Override
	protected void fillSettings(List<SettingsElement> settingsElements) {
		settings.fillSettings(settingsElements);
	}
}
