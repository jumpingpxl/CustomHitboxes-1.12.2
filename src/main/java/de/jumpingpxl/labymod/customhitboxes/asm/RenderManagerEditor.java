package de.jumpingpxl.labymod.customhitboxes.asm;

import de.jumpingpxl.labymod.customhitboxes.CustomHitboxes;
import de.jumpingpxl.labymod.customhitboxes.util.Color;
import de.jumpingpxl.labymod.customhitboxes.util.EntityType;
import de.jumpingpxl.labymod.customhitboxes.util.Settings;
import net.labymod.api.permissions.Permissions;
import net.labymod.core.asm.LabyModCoreMod;
import net.labymod.core.asm.global.ClassEditor;
import net.labymod.main.LabyMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.Objects;

public class RenderManagerEditor extends ClassEditor {

	private static final String MINECRAFT_CLASS = (LabyModCoreMod.isObfuscated() ? "bib"
			: "net/minecraft/client/Minecraft");
	private static final String RENDER_MANAGER_CLASS = (LabyModCoreMod.isObfuscated() ? "bzf"
			: "net/minecraft/client/renderer/entity/RenderManager");
	private static final String RENDER_MANAGER_EDITOR_CLASS =
			"de/jumpingpxl/labymod/customhitboxes" + "/asm/RenderManagerEditor";
	private static final String ENTITY_CLASS = (LabyModCoreMod.isObfuscated() ? "vg"
			: "net/minecraft/entity/Entity");
	private static final String IS_REDUCED_DEBUG_METHOD = (LabyModCoreMod.isObfuscated() ? "an"
			: "isReducedDebug");
	private static final String RENDER_ENTITY_METHOD = (LabyModCoreMod.isObfuscated() ? "a"
			: "renderEntity");
	private static final String RENDER_DEBUG_BOUNDING_BOX_METHOD = (LabyModCoreMod.isObfuscated()
			? "a" : "renderDebugBoundingBox");
	private static final String DEBUG_BOUNDING_BOX_FIELD = (LabyModCoreMod.isObfuscated() ? "t"
			: "debugBoundingBox");
	private static final String RENDER_POS_X_FIELD = (LabyModCoreMod.isObfuscated() ? "o"
			: "renderPosX");
	private static final String RENDER_POS_Y_FIELD = (LabyModCoreMod.isObfuscated() ? "p"
			: "renderPosY");
	private static final String RENDER_POS_Z_FIELD = (LabyModCoreMod.isObfuscated() ? "q"
			: "renderPosZ");
	private static Settings settings;

	public RenderManagerEditor() {
		super(ClassEditorType.CLASS_VISITOR);
	}

	public static boolean isReducedDebug() {
		return false;
	}

	public static boolean debugBoundingBox(RenderManager renderManager) {
		return renderManager.isDebugBoundingBox() || isEnabled();
	}

	public static void renderDebugBoundingBox(RenderManager renderManager, Entity entity, double x,
	                                          double y, double z, float entityYaw,
	                                          float partialTicks,
	                                          double renderPosX, double renderPosY,
	                                          double renderPosZ) {
		Color color = getColor(settings, entity);
		if (Objects.isNull(color)) {
			if (renderManager.isDebugBoundingBox()) {
				color = settings.getColor();
			} else {
				return;
			}
		}

		GlStateManager.pushMatrix();
		GlStateManager.disableTexture2D();
		GlStateManager.disableLighting();
		GlStateManager.disableCull();
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);

		AxisAlignedBB axis = entity.getEntityBoundingBox();
		RenderGlobal.drawBoundingBox(axis.minX - entity.posX + x, axis.minY - entity.posY + y,
				axis.minZ - entity.posZ + z, axis.maxX - entity.posX + x, axis.maxY - entity.posY + y,
				axis.maxZ - entity.posZ + z, color.getRed() / 255F, color.getGreen() / 255F,
				color.getBlue() / 255F, color.getAlpha() / 255F);

		Entity[] entityParts = entity.getParts();
		if (Objects.nonNull(entityParts)) {
			for (Entity part : entityParts) {
				double partX = (part.posX - part.prevPosX) * partialTicks;
				double partY = (part.posY - part.prevPosY) * partialTicks;
				double partZ = (part.posZ - part.prevPosZ) * partialTicks;
				AxisAlignedBB partAxis = part.getEntityBoundingBox();
				RenderGlobal.drawBoundingBox(partAxis.minX - renderPosX + partX,
						partAxis.minY - renderPosY + partY, partAxis.minZ - renderPosZ + partZ,
						partAxis.maxX - renderPosX + partX, partAxis.maxY - renderPosY + partY,
						partAxis.maxZ - renderPosZ + partZ, color.getRed() / 255F, color.getGreen() / 255F,
						color.getBlue() / 255F, color.getAlpha() / 255F);
			}
		}

		if (entity instanceof EntityLivingBase && settings.isEyeHeightBoxEnabled()) {
			float f = entity.width / 2.0F;
			Color eyeHeightBoxColor = settings.getEyeHeightBoxColor();
			RenderGlobal.drawBoundingBox(x - f, y + entity.getEyeHeight() - 0.01F, z - f, x + f,
					y + entity.getEyeHeight() + 0.01F, z + f, eyeHeightBoxColor.getRed() / 255F,
					eyeHeightBoxColor.getGreen() / 255F, eyeHeightBoxColor.getBlue() / 255F,
					eyeHeightBoxColor.getAlpha() / 255F);
		}

		if (!(LabyMod.getSettings().playerAnimation && LabyMod.getSettings().oldHitbox
				&& Permissions.isAllowed(Permissions.Permission.ANIMATIONS))) {
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferbuilder = tessellator.getBuffer();
			Vec3d vec3d = entity.getLook(partialTicks);
			bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
			bufferbuilder.pos(x, y + (double) entity.getEyeHeight(), z).color(0, 0, 255, 255).endVertex();
			bufferbuilder.pos(x + vec3d.x * 2.0D, y + (double) entity.getEyeHeight() + vec3d.y * 2.0D,
					z + vec3d.z * 2.0D).color(0, 0, 255, 255).endVertex();
			tessellator.draw();
		}

		GlStateManager.enableCull();
		GlStateManager.enableLighting();
		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
		GlStateManager.popMatrix();
	}

	private static Color getColor(Settings settings, Entity entity) {
		Color color = null;
		switch (EntityType.fromEntity(entity)) {
			case PLAYER:
				if (settings.isPlayersEnabled() && !(Minecraft.getMinecraft().player == entity
						&& !settings.isSelfEnabled())) {
					color = settings.isOwnColorPlayers() ? settings.getPlayerColor() : settings.getColor();
				}

				break;
			case ANIMAL:
				if (settings.isAnimalsEnabled()) {
					color = settings.isOwnColorAnimals() ? settings.getAnimalColor() : settings.getColor();
				}

				break;
			case MOB:
				if (settings.isMobsEnabled()) {
					color = settings.isOwnColorMobs() ? settings.getMobColor() : settings.getColor();
				}

				break;
			case DROP:
				if (settings.isDropsEnabled()) {
					color = settings.isOwnColorDrops() ? settings.getDropColor() : settings.getColor();
				}

				break;
			case THROWABLE:
				if (settings.isThrowablesEnabled()) {
					color =
							settings.isOwnColorThrowables() ? settings.getThrowableColor() : settings.getColor();
				}

				break;
			default:

				break;
		}

		return color;
	}

	private static boolean isEnabled() {
		if (Objects.isNull(settings)) {
			settings = CustomHitboxes.getSettings();
		}

		return settings.isEnabled();
	}

	@Override
	public void accept(String name, ClassNode node) {
		for (MethodNode method : node.methods) {
			if (method.name.equals(RENDER_ENTITY_METHOD) && method.desc.startsWith(
					"(L" + ENTITY_CLASS + ";DDDFFZ)")) {
				InsnList instructions = method.instructions;
				for (int i = 0; i < instructions.size(); i++) {
					AbstractInsnNode abstractNode = instructions.get(i);
					if (abstractNode.getOpcode() == Opcodes.GETFIELD) {
						FieldInsnNode fieldNode = (FieldInsnNode) abstractNode;
						if (fieldNode.owner.equals(RENDER_MANAGER_CLASS) && fieldNode.name.equals(
								DEBUG_BOUNDING_BOX_FIELD) && fieldNode.desc.equals("Z")) {
							instructions.insert(abstractNode,
									new MethodInsnNode(Opcodes.INVOKESTATIC, RENDER_MANAGER_EDITOR_CLASS,
											"debugBoundingBox", "(L" + RENDER_MANAGER_CLASS + ";)Z", false));
							instructions.remove(abstractNode);
						}
					} else if (abstractNode.getOpcode() == Opcodes.INVOKEVIRTUAL) {
						MethodInsnNode methodNode = (MethodInsnNode) abstractNode;
						if (methodNode.owner.equals(MINECRAFT_CLASS) && methodNode.name.equals(
								IS_REDUCED_DEBUG_METHOD) && methodNode.desc.equals("()Z")) {
							instructions.insert(abstractNode,
									new MethodInsnNode(Opcodes.INVOKESTATIC, RENDER_MANAGER_EDITOR_CLASS,
											"isReducedDebug", "()Z", false));
							instructions.remove(abstractNode.getPrevious());
							instructions.remove(abstractNode);
						}
					} else if (abstractNode.getOpcode() == Opcodes.INVOKESPECIAL) {
						MethodInsnNode methodNode = (MethodInsnNode) abstractNode;
						if (methodNode.owner.equals(RENDER_MANAGER_CLASS) && methodNode.name.equals(
								RENDER_DEBUG_BOUNDING_BOX_METHOD) && methodNode.desc.equals(
								"(L" + ENTITY_CLASS + ";DDDFF)V")) {
							InsnList insnList = new InsnList();
							insnList.insert(
									new FieldInsnNode(Opcodes.GETFIELD, RENDER_MANAGER_CLASS, RENDER_POS_Z_FIELD,
											"D"));
							insnList.insert(new VarInsnNode(Opcodes.ALOAD, 0));
							insnList.insert(
									new FieldInsnNode(Opcodes.GETFIELD, RENDER_MANAGER_CLASS, RENDER_POS_Y_FIELD,
											"D"));
							insnList.insert(new VarInsnNode(Opcodes.ALOAD, 0));
							insnList.insert(
									new FieldInsnNode(Opcodes.GETFIELD, RENDER_MANAGER_CLASS, RENDER_POS_X_FIELD,
											"D"));
							insnList.insert(new VarInsnNode(Opcodes.ALOAD, 0));

							instructions.insertBefore(abstractNode, insnList);
							instructions.insert(abstractNode,
									new MethodInsnNode(Opcodes.INVOKESTATIC, RENDER_MANAGER_EDITOR_CLASS,
											"renderDebugBoundingBox",
											"(L" + RENDER_MANAGER_CLASS + ";L" + ENTITY_CLASS + ";" + "DDDFFDDD)V",
											false));
							instructions.remove(abstractNode);
						}
					}
				}

				break;
			}
		}
	}
}
