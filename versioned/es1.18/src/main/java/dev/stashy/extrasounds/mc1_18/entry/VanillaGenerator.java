package dev.stashy.extrasounds.mc1_18.entry;

import dev.stashy.extrasounds.logics.entry.BaseVanillaGenerator;
import dev.stashy.extrasounds.mapping.SoundDefinition;
import dev.stashy.extrasounds.mapping.SoundGenerator;
import net.minecraft.client.sound.SoundEntry;
import net.minecraft.item.*;

import static dev.stashy.extrasounds.sounds.Categories.*;
import static dev.stashy.extrasounds.sounds.Sounds.aliased;
import static dev.stashy.extrasounds.sounds.Sounds.event;

public final class VanillaGenerator extends BaseVanillaGenerator {
    @Override
    protected SoundGenerator generate() {
        return SoundGenerator.of(item -> {
            if (item instanceof BlockItem blockItem) {
                return this.generateFromBlock(blockItem.getBlock());
            } else if (item instanceof ToolItem toolItem) {
                if (toolItem.getMaterial() instanceof ToolMaterials mats) {
                    return this.generateFromToolMaterial(mats);
                }
                return SoundDefinition.of(aliased(Gear.GENERIC));
            } else if (item instanceof ArmorItem armorItem) {
                if (armorItem.getMaterial() instanceof ArmorMaterials mats) {
                    return this.generateFromArmorMaterial(mats);
                }
                return SoundDefinition.of(aliased(Gear.GENERIC));
            } else if (this.isPotionItem(item)) {
                return SoundDefinition.of(aliased(POTION));
            } else if (item instanceof BucketItem bucketItem) {
                final SoundEntry soundEntry = bucketItem.fluid.getBucketFillSound().map(sound -> event(sound.getId(), 0.4f)).orElse(aliased(METAL));
                return SoundDefinition.of(soundEntry);
            }

            return super.generalSounds(item);
        });
    }

    private boolean isPotionItem(Item item) {
        return item instanceof PotionItem || item instanceof ExperienceBottleItem;
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

    private SoundDefinition generateFromArmorMaterial(ArmorMaterials mats) {
        return switch (mats) {
            case IRON -> SoundDefinition.of(aliased(Gear.IRON));
            case GOLD -> SoundDefinition.of(aliased(Gear.GOLDEN));
            case DIAMOND -> SoundDefinition.of(aliased(Gear.DIAMOND));
            case NETHERITE -> SoundDefinition.of(aliased(Gear.NETHERITE));
            case CHAIN -> SoundDefinition.of(aliased(Gear.CHAIN));
            case TURTLE -> SoundDefinition.of(aliased(Gear.TURTLE));
            case LEATHER -> SoundDefinition.of(aliased(Gear.LEATHER));
            default -> SoundDefinition.of(aliased(Gear.GENERIC));
        };
    }
}
