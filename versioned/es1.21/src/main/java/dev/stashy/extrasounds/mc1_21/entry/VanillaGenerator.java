package dev.stashy.extrasounds.mc1_21.entry;

import dev.stashy.extrasounds.logics.entry.BaseVanillaGenerator;
import dev.stashy.extrasounds.mapping.SoundDefinition;
import dev.stashy.extrasounds.mapping.SoundGenerator;
import net.minecraft.block.Block;
import net.minecraft.block.PillarBlock;
import net.minecraft.client.sound.Sound;
import net.minecraft.client.sound.SoundEntry;
import net.minecraft.item.*;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

import static dev.stashy.extrasounds.sounds.Categories.*;
import static dev.stashy.extrasounds.sounds.Sounds.*;

public final class VanillaGenerator extends BaseVanillaGenerator {
    @Override
    protected SoundGenerator generate() {
        return SoundGenerator.of(item -> {
            if (item instanceof BlockItem blockItem) {
                final Block block = blockItem.getBlock();
                final Identifier blockSoundId = block.getDefaultState().getSoundGroup().getPlaceSound().getId();
                if (block instanceof PillarBlock pillarBlock && pillarBlock.getDefaultState().getSoundGroup().equals(BlockSoundGroup.FROGLIGHT)) {
                    return SoundDefinition.of(event(blockSoundId, 0.3f));
                }
                return this.generateFromBlock(block);
            } else if (item instanceof ToolItem toolItem) {
                if (toolItem.getMaterial() instanceof ToolMaterials mats) {
                    return this.generateFromToolMaterial(mats);
                }
                return SoundDefinition.of(aliased(Gear.GENERIC));
            } else if (item instanceof ArmorItem armorItem) {
                return this.generateFromArmorMaterial(armorItem.getMaterial().value());
            } else if (this.isPotionItem(item)) {
                return SoundDefinition.of(aliased(POTION));
            } else if (item instanceof GoatHornItem) {
                return SoundDefinition.of(single(LOOSE_METAL.getId(), 0.6f, 0.9f, Sound.RegistrationType.SOUND_EVENT));
            } else if (item instanceof SmithingTemplateItem) {
                return SoundDefinition.of(aliased(LOOSE_METAL));
            } else if (item instanceof DiscFragmentItem) {
                return SoundDefinition.of(single(METAL_BITS.getId(), 0.7f, 0.85f, Sound.RegistrationType.SOUND_EVENT));
            } else if (item instanceof BucketItem bucketItem) {
                final SoundEntry soundEntry = bucketItem.fluid.getBucketFillSound().map(sound -> event(sound.getId(), 0.4f)).orElse(aliased(METAL));
                return SoundDefinition.of(soundEntry);
            }

            return super.generalSounds(item);
        });
    }

    private boolean isPotionItem(Item item) {
        return item instanceof PotionItem || item instanceof ExperienceBottleItem || item instanceof OminousBottleItem;
    }

    private SoundDefinition generateFromToolMaterial(ToolMaterials mats) {
        return switch (mats) {
            case WOOD -> SoundDefinition.of(aliased(Gear.WOOD));
            case STONE -> SoundDefinition.of(aliased(Gear.STONE));
            case IRON -> SoundDefinition.of(aliased(Gear.IRON));
            case DIAMOND -> SoundDefinition.of(aliased(Gear.DIAMOND));
            case GOLD -> SoundDefinition.of(aliased(Gear.GOLDEN));
            case NETHERITE -> SoundDefinition.of(aliased(Gear.NETHERITE));
            default -> SoundDefinition.of(aliased(Gear.GENERIC));
            //⬆ even though not required, this is in case any mods add to the enum of materials
        };
    }

    private SoundDefinition generateFromArmorMaterial(ArmorMaterial mat) {
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
