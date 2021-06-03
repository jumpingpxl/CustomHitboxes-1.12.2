package de.jumpingpxl.labymod.customhitboxes.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityFishHook;

public enum EntityType {
	UNKNOWN,
	PLAYER,
	ANIMAL,
	MOB,
	DROP,
	THROWABLE;

	public static EntityType fromEntity(Entity entity) {
		if (entity instanceof EntityPlayer) {
			return PLAYER;
		}

		if (entity instanceof IMob) {
			return MOB;
		}

		if (entity instanceof IAnimals) {
			return ANIMAL;
		}

		if (entity instanceof EntityItem || entity instanceof EntityXPOrb) {
			return DROP;
		}

		if (entity instanceof EntityFireball || entity instanceof IProjectile
				|| entity instanceof EntityFishHook) {
			return THROWABLE;
		}

		return UNKNOWN;
	}
}
