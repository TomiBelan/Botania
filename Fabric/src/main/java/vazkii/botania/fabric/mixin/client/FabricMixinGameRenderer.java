/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.fabric.mixin.client;

import com.mojang.blaze3d.shaders.Program;
import com.mojang.datafixers.util.Pair;

import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.server.packs.resources.ResourceManager;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import vazkii.botania.client.core.helper.CoreShaders;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

@Mixin(GameRenderer.class)
public class FabricMixinGameRenderer {
	@Inject(
		method = "reloadShaders",
		at = @At(
			value = "INVOKE_ASSIGN",
			target = "Lcom/google/common/collect/Lists;newArrayListWithCapacity(I)Ljava/util/ArrayList;",
			remap = false
		),
		locals = LocalCapture.CAPTURE_FAILHARD
	)
	private void loadShaders(ResourceManager resourceManager, CallbackInfo ci,
			List<Program> _programsToClose,
			List<Pair<ShaderInstance, Consumer<ShaderInstance>>> shadersToLoad)
			throws IOException {
		CoreShaders.init(resourceManager, shadersToLoad::add);
	}
}
