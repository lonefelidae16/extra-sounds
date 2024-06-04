package dev.stashy.extrasounds.mc1_19_4.mapping;

import dev.stashy.extrasounds.logics.mapping.BaseVanillaGenerator;
import dev.stashy.extrasounds.logics.mapping.SoundDefinition;
import dev.stashy.extrasounds.logics.mapping.SoundGenerator;
import net.minecraft.client.sound.Sound;
import net.minecraft.item.*;
import net.minecraft.util.Identifier;

import static dev.stashy.extrasounds.logics.sounds.Categories.*;
import static dev.stashy.extrasounds.logics.sounds.Sounds.aliased;
import static dev.stashy.extrasounds.logics.sounds.Sounds.single;

public final class VanillaGenerator extends BaseVanillaGenerator {
    public static SoundGenerator generate() {
        return SoundGenerator.of(Identifier.DEFAULT_NAMESPACE, item -> {
            if (item instanceof BlockItem blockItem) {
                return generateFromBlock(blockItem.getBlock());
            } else if (item instanceof ToolItem toolItem) {
                return generateFromToolMaterial(toolItem.getMaterial());
            } else if (item instanceof ArmorItem armorItem) {
                return fromArmorMaterial(armorItem.getMaterial());
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

    private static boolean isPotionItem(Item item) {
        return item instanceof PotionItem || item instanceof ExperienceBottleItem;
    }

    private static SoundDefinition fromArmorMaterial(ArmorMaterial mat) {
        if (mat == null) {
            return SoundDefinition.of(aliased(Gear.GENERIC));
        } else if (mat == ArmorMaterials.IRON) {
            return SoundDefinition.of(aliased(Gear.IRON));
        } else if (mat == ArmorMaterials.GOLD) {
            return SoundDefinition.of(aliased(Gear.GOLDEN));
        } else if (mat == ArmorMaterials.DIAMOND) {
            return SoundDefinition.of(aliased(Gear.DIAMOND));
        } else if (mat == ArmorMaterials.NETHERITE) {
            return SoundDefinition.of(aliased(Gear.NETHERITE));
        } else if (mat == ArmorMaterials.CHAIN) {
            return SoundDefinition.of(aliased(Gear.CHAIN));
        } else if (mat == ArmorMaterials.TURTLE) {
            return SoundDefinition.of(aliased(Gear.TURTLE));
        } else if (mat == ArmorMaterials.LEATHER) {
            return SoundDefinition.of(aliased(Gear.LEATHER));
        } else {
            return SoundDefinition.of(aliased(Gear.GENERIC));
        }
    }
}
