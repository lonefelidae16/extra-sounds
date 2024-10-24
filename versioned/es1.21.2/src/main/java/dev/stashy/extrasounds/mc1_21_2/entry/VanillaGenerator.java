package dev.stashy.extrasounds.mc1_21_2.entry;

import dev.stashy.extrasounds.logics.entry.BaseVanillaGenerator;
import dev.stashy.extrasounds.mapping.SoundDefinition;
import dev.stashy.extrasounds.mapping.SoundGenerator;
import net.minecraft.block.Block;
import net.minecraft.block.PillarBlock;
import net.minecraft.client.sound.Sound;
import net.minecraft.client.sound.SoundEntry;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.RepairableComponent;
import net.minecraft.item.*;
import net.minecraft.registry.tag.ItemTags;
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
                final Identifier blockSoundId = block.getDefaultState().getSoundGroup().getPlaceSound().id();
                if (block instanceof PillarBlock pillarBlock && pillarBlock.getDefaultState().getSoundGroup().equals(BlockSoundGroup.FROGLIGHT)) {
                    return SoundDefinition.of(event(blockSoundId, 0.3f));
                }
                return this.generateFromBlock(block);
            } else if (item instanceof MiningToolItem || item instanceof SwordItem ||
                    item instanceof ArmorItem || item instanceof AnimalArmorItem
            ) {
                return this.generateFromRepairable(item.getComponents().get(DataComponentTypes.REPAIRABLE));
            } else if (this.isPotionItem(item)) {
                return SoundDefinition.of(aliased(POTION));
            } else if (item instanceof GoatHornItem) {
                return SoundDefinition.of(single(LOOSE_METAL.getId(), 0.6f, 0.9f, Sound.RegistrationType.SOUND_EVENT));
            } else if (item instanceof SmithingTemplateItem) {
                return SoundDefinition.of(aliased(LOOSE_METAL));
            } else if (item instanceof DiscFragmentItem) {
                return SoundDefinition.of(single(METAL_BITS.getId(), 0.7f, 0.85f, Sound.RegistrationType.SOUND_EVENT));
            } else if (item instanceof BucketItem bucketItem) {
                final SoundEntry soundEntry = bucketItem.fluid.getBucketFillSound().map(sound -> event(sound.id(), 0.4f)).orElse(aliased(METAL));
                return SoundDefinition.of(soundEntry);
            }

            return super.generalSounds(item);
        });
    }

    private boolean isPotionItem(Item item) {
        return item instanceof PotionItem || item instanceof ExperienceBottleItem || item == Items.OMINOUS_BOTTLE;
    }

    private SoundDefinition generateFromRepairable(RepairableComponent component) {
        if (component == null) {
            return SoundDefinition.of(aliased(Gear.GENERIC));
        }

        final var optionalTagKey = component.items().getTagKey();
        if (optionalTagKey.isEmpty()) {
            return SoundDefinition.of(aliased(Gear.GENERIC));
        }

        final var matTags = optionalTagKey.get();
        if (matTags == ItemTags.WOODEN_TOOL_MATERIALS) {
            return SoundDefinition.of(aliased(Gear.WOOD));
        } else if (matTags == ItemTags.STONE_TOOL_MATERIALS) {
            return SoundDefinition.of(aliased(Gear.STONE));
        } else if (matTags == ItemTags.IRON_TOOL_MATERIALS || matTags == ItemTags.REPAIRS_IRON_ARMOR) {
            return SoundDefinition.of(aliased(Gear.IRON));
        } else if (matTags == ItemTags.GOLD_TOOL_MATERIALS || matTags == ItemTags.REPAIRS_GOLD_ARMOR) {
            return SoundDefinition.of(aliased(Gear.GOLDEN));
        } else if (matTags == ItemTags.DIAMOND_TOOL_MATERIALS || matTags == ItemTags.REPAIRS_DIAMOND_ARMOR) {
            return SoundDefinition.of(aliased(Gear.DIAMOND));
        } else if (matTags == ItemTags.NETHERITE_TOOL_MATERIALS || matTags == ItemTags.REPAIRS_NETHERITE_ARMOR) {
            return SoundDefinition.of(aliased(Gear.NETHERITE));
        } else if (matTags == ItemTags.REPAIRS_LEATHER_ARMOR) {
            return SoundDefinition.of(aliased(Gear.LEATHER));
        } else if (matTags == ItemTags.REPAIRS_CHAIN_ARMOR) {
            return SoundDefinition.of(aliased(Gear.CHAIN));
        } else if (matTags == ItemTags.REPAIRS_TURTLE_HELMET) {
            return SoundDefinition.of(aliased(Gear.TURTLE));
        } else if (matTags == ItemTags.REPAIRS_WOLF_ARMOR) {
            return SoundDefinition.of(aliased(Gear.ARMADILLO));
        } else {
            return SoundDefinition.of(aliased(Gear.GENERIC));
            //⬆ even though not required, this is in case any mods add to the repairable materials
        }
    }
}
