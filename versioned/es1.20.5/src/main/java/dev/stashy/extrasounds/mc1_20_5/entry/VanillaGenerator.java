package dev.stashy.extrasounds.mc1_20_5.entry;

import dev.stashy.extrasounds.logics.entry.BaseVanillaGenerator;
import dev.stashy.extrasounds.mapping.SoundDefinition;
import dev.stashy.extrasounds.mapping.SoundGenerator;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PillarBlock;
import net.minecraft.client.sound.Sound;
import net.minecraft.item.*;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

import static dev.stashy.extrasounds.sounds.Categories.*;
import static dev.stashy.extrasounds.sounds.Sounds.*;

public final class VanillaGenerator extends BaseVanillaGenerator {
    @Override
    protected SoundGenerator generate() {
        return SoundGenerator.of(Identifier.DEFAULT_NAMESPACE, item -> {
            if (item instanceof BlockItem blockItem) {
                final Block block = blockItem.getBlock();
                final BlockState blockState = block.getDefaultState();
                final Identifier blockSoundId = blockState.getSoundGroup().getPlaceSound().getId();
                if (block instanceof PillarBlock pillarBlock && pillarBlock.getDefaultState().getSoundGroup().equals(BlockSoundGroup.FROGLIGHT)) {
                    return SoundDefinition.of(event(blockSoundId, 0.3f));
                }
                return generateFromBlock(block);
            } else if (item instanceof ToolItem toolItem) {
                return generateFromToolMaterial(toolItem.getMaterial());
            } else if (item instanceof ArmorItem armorItem) {
                return fromArmorMaterial(armorItem.getMaterial().value());
            } else if (item instanceof StewItem || item instanceof SuspiciousStewItem) {
                return SoundDefinition.of(aliased(BOWL));
            } else if (isPotionItem(item)) {
                return SoundDefinition.of(aliased(POTION));
            } else if (item instanceof GoatHornItem) {
                return SoundDefinition.of(single(LOOSE_METAL.getId(), 0.6f, 0.9f, Sound.RegistrationType.SOUND_EVENT));
            } else if (item instanceof SmithingTemplateItem) {
                return SoundDefinition.of(aliased(LOOSE_METAL));
            } else if (item instanceof DiscFragmentItem) {
                return SoundDefinition.of(single(METAL_BITS.getId(), 0.7f, 0.85f, Sound.RegistrationType.SOUND_EVENT));
            } else if (item instanceof MusicDiscItem) {
                return SoundDefinition.of(aliased(MUSIC_DISC));
            } else if (isBrickItem(item)) {
                return SoundDefinition.of(aliased(BRICK));
            } else if (isGearGoldenItem(item)) {
                return SoundDefinition.of(aliased(Gear.GOLDEN));
            } else if (isGearLeatherItem(item)) {
                return SoundDefinition.of(aliased(Gear.LEATHER));
            } else if (isGearGenericItem(item)) {
                return SoundDefinition.of(aliased(Gear.GENERIC));
            } else if (isPaperItem(item)) {
                return SoundDefinition.of(aliased(PAPER));
            }

            return generalSounds(item);
        });
    }

    private boolean isPotionItem(Item item) {
        return item instanceof PotionItem || item instanceof ExperienceBottleItem || item instanceof OminousBottleItem;
    }

    private SoundDefinition fromArmorMaterial(ArmorMaterial mat) {
        if (mat == null) {
            return SoundDefinition.of(aliased(Gear.GENERIC));
        } else if (mat == ArmorMaterials.IRON.value()) {
            return SoundDefinition.of(aliased(Gear.IRON));
        } else if (mat == ArmorMaterials.GOLD.value()) {
            return SoundDefinition.of(aliased(Gear.GOLDEN));
        } else if (mat == ArmorMaterials.DIAMOND.value()) {
            return SoundDefinition.of(aliased(Gear.DIAMOND));
        } else if (mat == ArmorMaterials.NETHERITE.value()) {
            return SoundDefinition.of(aliased(Gear.NETHERITE));
        } else if (mat == ArmorMaterials.CHAIN.value()) {
            return SoundDefinition.of(aliased(Gear.CHAIN));
        } else if (mat == ArmorMaterials.TURTLE.value()) {
            return SoundDefinition.of(aliased(Gear.TURTLE));
        } else if (mat == ArmorMaterials.LEATHER.value()) {
            return SoundDefinition.of(aliased(Gear.LEATHER));
        } else if (mat == ArmorMaterials.ARMADILLO.value()) {
            return SoundDefinition.of(aliased(Gear.ARMADILLO));
        } else {
            return SoundDefinition.of(aliased(Gear.GENERIC));
        }
    }
}
