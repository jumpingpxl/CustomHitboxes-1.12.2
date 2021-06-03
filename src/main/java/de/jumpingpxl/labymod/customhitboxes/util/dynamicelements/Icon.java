package de.jumpingpxl.labymod.customhitboxes.util.dynamicelements;

import net.labymod.utils.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.Objects;

public class Icon {

	private final ItemStack itemStack;
	private final ResourceLocation resourceLocation;

	private Icon(Material material) {
		itemStack = material.createItemStack();
		resourceLocation = null;
	}

	private Icon(Material material, int damage) {
		this(material);

		itemStack.setItemDamage(damage);
	}

	private Icon(ResourceLocation resourceLocation) {
		this.resourceLocation = resourceLocation;
		itemStack = null;
	}

	public static Icon of(Material material) {
		return new Icon(material);
	}

	public static Icon of(Material material, int damage) {
		return new Icon(material, damage);
	}

	public static Icon of(ResourceLocation resourceLocation) {
		return new Icon(resourceLocation);
	}

	public ItemStack getItemStack() {
		return itemStack;
	}

	public ResourceLocation getResourceLocation() {
		return resourceLocation;
	}

	public Object getIcon() {
		return Objects.isNull(itemStack) ? resourceLocation : itemStack;
	}
}
