package dev.stashy.extrasounds.mc1_21.mapping;

import dev.stashy.extrasounds.logics.mapping.BaseVanillaGenerator;
import dev.stashy.extrasounds.logics.mapping.SoundDefinition;
import dev.stashy.extrasounds.logics.mapping.SoundGenerator;
import dev.stashy.extrasounds.mc1_21.SupportedVersions;
import dev.stashy.extrasounds.mc1_21.mixin.BucketFluidAccessor;
import me.lonefelidae16.groominglib.api.McVersionInterchange;
import net.minecraft.block.*;
import net.minecraft.client.sound.Sound;
import net.minecraft.client.sound.SoundEntry;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.*;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

import static dev.stashy.extrasounds.logics.sounds.Categories.*;
import static dev.stashy.extrasounds.logics.sounds.Sounds.*;

public final class VanillaGenerator extends BaseVanillaGenerator {
    private static boolean isGearGoldenItem(Item item) {
        return item instanceof CompassItem ||
                item instanceof SpyglassItem || item instanceof ShearsItem;
    }

    private static boolean isGearLeatherItem(Item item) {
        return item instanceof LeadItem || item instanceof ElytraItem || item instanceof SaddleItem;
    }

    private static boolean isGearGenericItem(Item item) {
        return item instanceof BowItem || item instanceof CrossbowItem || item instanceof FishingRodItem ||
                item instanceof OnAStickItem;
    }

    private static boolean isPaperItem(Item item) {
        return item instanceof BannerPatternItem || item instanceof BookItem || item instanceof WritableBookItem ||
                item instanceof WrittenBookItem || item instanceof EnchantedBookItem || item instanceof EmptyMapItem ||
                item instanceof FilledMapItem || item instanceof NameTagItem || item instanceof KnowledgeBookItem;
    }

    private static boolean isBrickItem(Item item) {
        return item == Items.BRICK || item.getTranslationKey().endsWith("pottery_sherd");
    }

    private static boolean isStewItem(Item item) {
        return item == Items.RABBIT_STEW || item == Items.BEETROOT_SOUP ||
                item == Items.MUSHROOM_STEW || item instanceof SuspiciousStewItem;
    }

    private static SoundDefinition fromToolMaterial(ToolMaterial mat) {
        if (mat == null) {
            return SoundDefinition.of(aliased(Gear.GENERIC));
        } else if (mat == ToolMaterials.WOOD) {
            return SoundDefinition.of(aliased(Gear.WOOD));
        } else if (mat == ToolMaterials.STONE) {
            return SoundDefinition.of(aliased(Gear.STONE));
        } else if (mat == ToolMaterials.IRON) {
            return SoundDefinition.of(aliased(Gear.IRON));
        } else if (mat == ToolMaterials.GOLD) {
            return SoundDefinition.of(aliased(Gear.GOLDEN));
        } else if (mat == ToolMaterials.DIAMOND) {
            return SoundDefinition.of(aliased(Gear.DIAMOND));
        } else if (mat == ToolMaterials.NETHERITE) {
            return SoundDefinition.of(aliased(Gear.NETHERITE));
        } else {
            return SoundDefinition.of(aliased(Gear.GENERIC));
            //⬆ even though not required, this is in case any mods add to the enum of materials
        }
    }

    private static SoundDefinition fromArmorMaterial(ArmorMaterial mat) {
        if (mat == null) {
            return SoundDefinition.of(aliased(Gear.GENERIC));
        } else if (ArmorMaterials.IRON.value().equals(mat)) {
            return SoundDefinition.of(aliased(Gear.IRON));
        } else if (ArmorMaterials.GOLD.value().equals(mat)) {
            return SoundDefinition.of(aliased(Gear.GOLDEN));
        } else if (ArmorMaterials.DIAMOND.value().equals(mat)) {
            return SoundDefinition.of(aliased(Gear.DIAMOND));
        } else if (ArmorMaterials.NETHERITE.value().equals(mat)) {
            return SoundDefinition.of(aliased(Gear.NETHERITE));
        } else if (ArmorMaterials.CHAIN.value().equals(mat)) {
            return SoundDefinition.of(aliased(Gear.CHAIN));
        } else if (ArmorMaterials.TURTLE.value().equals(mat)) {
            return SoundDefinition.of(aliased(Gear.TURTLE));
        } else if (ArmorMaterials.LEATHER.value().equals(mat)) {
            return SoundDefinition.of(aliased(Gear.LEATHER));
        } else if (ArmorMaterials.ARMADILLO.value().equals(mat)) {
            return SoundDefinition.of(aliased(Gear.ARMADILLO));
        } else {
            return SoundDefinition.of(aliased(Gear.GENERIC));
        }
    }

    public static SoundGenerator generator = SoundGenerator.of(McVersionInterchange.isInVersionRange(SupportedVersions.START, SupportedVersions.END) ? Identifier.DEFAULT_NAMESPACE : null, item -> {
        if (item instanceof BoatItem) {
            return SoundDefinition.of(aliased(BOAT));
        } else if (item instanceof ToolItem toolItem) {
            return fromToolMaterial(toolItem.getMaterial());
        } else if (item instanceof ArmorItem armorItem) {
            return fromArmorMaterial(armorItem.getMaterial().value());
        } else if (item instanceof ShieldItem) {
            return SoundDefinition.of(aliased(Gear.IRON));
        } else if (item instanceof BucketItem bucketItem) {
            final Fluid fluid = ((BucketFluidAccessor) bucketItem).getFluid();
            final SoundEntry soundEntry = fluid.getBucketFillSound().map(sound -> event(sound.getId(), 0.4f)).orElse(aliased(METAL));
            return SoundDefinition.of(soundEntry);
        } else if (item instanceof MinecartItem) {
            return SoundDefinition.of(aliased(MINECART));
        } else if (item instanceof ItemFrameItem) {
            return SoundDefinition.of(aliased(FRAME));
        } else if (item instanceof PotionItem || item instanceof ExperienceBottleItem || item instanceof OminousBottleItem) {
            return SoundDefinition.of(aliased(POTION));
        } else if (item instanceof ArrowItem) {
            return SoundDefinition.of(aliased(ARROW));
        } else if (item instanceof DyeItem) {
            return SoundDefinition.of(aliased(DUST));
        } else if (item instanceof SpawnEggItem) {
            return SoundDefinition.of(aliased(WET_SLIPPERY));
        } else if (isStewItem(item)) {
            return SoundDefinition.of(aliased(BOWL));
        } else if (item instanceof GoatHornItem) {
            return SoundDefinition.of(single(LOOSE_METAL.getId(), 0.6f, 0.9f, Sound.RegistrationType.SOUND_EVENT));
        } else if (item instanceof SmithingTemplateItem) {
            return SoundDefinition.of(aliased(LOOSE_METAL));
        } else if (item instanceof DiscFragmentItem) {
            return SoundDefinition.of(single(METAL_BITS.getId(), 0.7f, 0.85f, Sound.RegistrationType.SOUND_EVENT));
        } else if (getItemIdPath(item).startsWith("music_disc_")) {
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
        } else if (item instanceof BlockItem blockItem) {
            final Block block = blockItem.getBlock();
            final BlockState blockState = block.getDefaultState();
            final Identifier blockSoundId = blockState.getSoundGroup().getPlaceSound().getId();

            if (block instanceof AbstractRailBlock) {
                return SoundDefinition.of(aliased(RAIL));
            } else if (block instanceof BannerBlock) {
                return SoundDefinition.of(aliased(BANNER));
            } else if (block instanceof SeaPickleBlock) {
                return SoundDefinition.of(event(blockSoundId, 0.4f));
            } else if (block instanceof LeavesBlock || block instanceof PlantBlock || block instanceof SugarCaneBlock) {
                if (blockSoundId.getPath().equals("block.grass.place")) {
                    return SoundDefinition.of(aliased(LEAVES));
                } else {
                    return SoundDefinition.of(event(blockSoundId));
                }
            } else if (block instanceof PillarBlock pillarBlock && pillarBlock.getDefaultState().getSoundGroup().equals(BlockSoundGroup.FROGLIGHT)) {
                return SoundDefinition.of(event(blockSoundId, 0.3f));
            }

            return SoundDefinition.of(event(blockSoundId));
        }

        return SoundDefinition.of(aliased(ITEM_PICK));
    });
}
