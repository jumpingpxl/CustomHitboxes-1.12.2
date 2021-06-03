package de.jumpingpxl.labymod.customhitboxes;

import de.jumpingpxl.labymod.customhitboxes.asm.RenderManagerEditor;
import net.labymod.core.asm.LabyModCoreMod;
import net.labymod.core.asm.global.ClassEditor;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

public class CustomHitboxTransformer implements IClassTransformer {

	private static final String RENDER_MANAGER_CLASS = (LabyModCoreMod.isObfuscated() ? "bzd"
			: "net.minecraft.client.renderer.entity.RenderManager");

	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		if (!name.equals(RENDER_MANAGER_CLASS)) {
			return basicClass;
		}

		ClassEditor editor = new RenderManagerEditor();
		ClassNode node = new ClassNode();
		ClassReader reader = new ClassReader(basicClass);
		reader.accept(node, 0);
		editor.accept(name, node);
		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		node.accept(writer);
		return writer.toByteArray();
	}
}
