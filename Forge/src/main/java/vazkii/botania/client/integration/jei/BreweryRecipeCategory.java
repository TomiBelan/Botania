/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.client.integration.jei;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import vazkii.botania.api.recipe.IBrewRecipe;
import vazkii.botania.common.block.ModBlocks;
import vazkii.botania.common.item.ModItems;

import javax.annotation.Nonnull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static vazkii.botania.common.lib.ResourceLocationHelper.prefix;

public class BreweryRecipeCategory implements IRecipeCategory<IBrewRecipe> {

	public static final ResourceLocation UID = prefix("brewery");
	private final IDrawableStatic background;
	private final IDrawable icon;
	private final Component localizedName;

	public BreweryRecipeCategory(IGuiHelper guiHelper) {
		ResourceLocation location = prefix("textures/gui/nei_brewery.png");
		background = guiHelper.createDrawable(location, 28, 6, 131, 55);
		localizedName = new TranslatableComponent("botania.nei.brewery");
		icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM, new ItemStack(ModBlocks.brewery));
	}

	@Nonnull
	@Override
	public ResourceLocation getUid() {
		return UID;
	}

	@Nonnull
	@Override
	public Class<? extends IBrewRecipe> getRecipeClass() {
		return IBrewRecipe.class;
	}

	@Nonnull
	@Override
	public Component getTitle() {
		return localizedName;
	}

	@Nonnull
	@Override
	public IDrawable getBackground() {
		return background;
	}

	@Nonnull
	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public void setRecipe(@Nonnull IRecipeLayoutBuilder builder, @Nonnull IBrewRecipe recipe, @Nonnull IFocusGroup focuses) {
		List<ItemStack> outputs = new ArrayList<>();
		List<ItemStack> containers = new ArrayList<>();

		for (var container : new ItemStack[] {
				new ItemStack(ModItems.vial), new ItemStack(ModItems.flask),
				new ItemStack(ModItems.incenseStick), new ItemStack(ModItems.bloodPendant)
		}) {
			ItemStack brewed = recipe.getOutput(container);
			if (!brewed.isEmpty()) {
				containers.add(container);
				outputs.add(brewed);
			}
		}

		IFocus<ItemStack> outputFocus = focuses.getFocuses(VanillaTypes.ITEM, RecipeIngredientRole.OUTPUT).findAny().orElse(null);
		IFocus<ItemStack> inputFocus = focuses.getFocuses(VanillaTypes.ITEM, RecipeIngredientRole.INPUT).findAny().orElse(null);

		builder.addSlot(RecipeIngredientRole.INPUT, 10, 35)
				.addItemStacks(getItemMatchingFocus(outputFocus, outputs, containers));

		var inputs = recipe.getIngredients();
		int posX = 67 - (inputs.size() * 9);
		for (var ingr : inputs) {
			builder.addSlot(RecipeIngredientRole.INPUT, posX, 0)
					.addIngredients(ingr);
			posX += 18;
		}

		builder.addSlot(RecipeIngredientRole.OUTPUT, 58, 35)
				.addItemStacks(getItemMatchingFocus(inputFocus, containers, outputs));
	}

	/**
	 * If an item in this recipe is focused, returns the corresponding item instead of all the containers/results.
	 */
	private List<ItemStack> getItemMatchingFocus(IFocus<ItemStack> focus, List<ItemStack> focused, List<ItemStack> other) {
		if (focus != null) {
			ItemStack focusStack = focus.getTypedValue().getIngredient();
			for (int i = 0; i < focused.size(); i++) {
				if (focusStack.sameItem(focused.get(i))) {
					return Collections.singletonList(other.get(i));
				}
			}
		}
		return other;
	}
}
