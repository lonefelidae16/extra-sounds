package dev.stashy.extrasounds.mapping;

import dev.stashy.extrasounds.ExtraSounds;
import dev.stashy.extrasounds.mixin.BlockMaterialAccessor;
import dev.stashy.extrasounds.mixin.BucketFluidAccessor;
import net.minecraft.block.*;
import net.minecraft.client.sound.Sound;
import net.minecraft.client.sound.SoundEntry;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.*;
import net.minecraft.util.Identifier;

import static dev.stashy.extrasounds.sounds.Categories.*;
import static dev.stashy.extrasounds.sounds.Sounds.*;

public final class VanillaGenerator {
    private static boolean isGearGoldenItem(Item item) {
        return item instanceof HorseArmorItem || item instanceof CompassItem ||
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

    public static SoundGenerator generator = SoundGenerator.of("minecraft", ExtraSounds.MODID, item -> {
        if (item instanceof MusicDiscItem) {
            return SoundDefinition.of(aliased(MUSIC_DISC));
        } else if (item instanceof BoatItem) {
            return SoundDefinition.of(aliased(BOAT));
        } else if (item instanceof ToolItem toolItem) {
            if (toolItem.getMaterial() instanceof ToolMaterials mat) {
                return switch (mat) {
                    case WOOD -> SoundDefinition.of(aliased(Gear.WOOD));
                    case STONE -> SoundDefinition.of(aliased(Gear.STONE));
                    case IRON -> SoundDefinition.of(aliased(Gear.IRON));
                    case GOLD -> SoundDefinition.of(aliased(Gear.GOLDEN));
                    case DIAMOND -> SoundDefinition.of(aliased(Gear.DIAMOND));
                    case NETHERITE -> SoundDefinition.of(aliased(Gear.NETHERITE));
                    default -> SoundDefinition.of(aliased(Gear.GENERIC));
                    //⬆ even though not required, this is in case any mods add to the enum of materials
                };
            }
            return SoundDefinition.of(aliased(Gear.GENERIC));
        } else if (item instanceof ArmorItem armorItem) {
            if (armorItem.getMaterial() instanceof ArmorMaterials mat) {
                return switch (mat) {
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
            return SoundDefinition.of(aliased(Gear.GENERIC));
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
        } else if (item instanceof PotionItem || item instanceof ExperienceBottleItem) {
            return SoundDefinition.of(aliased(POTION));
        } else if (item instanceof ArrowItem) {
            return SoundDefinition.of(aliased(ARROW));
        } else if (item instanceof DyeItem) {
            return SoundDefinition.of(aliased(DUST));
        } else if (item instanceof SpawnEggItem) {
            return SoundDefinition.of(aliased(WET_SLIPPERY));
        } else if (item instanceof StewItem || item instanceof SuspiciousStewItem) {
            return SoundDefinition.of(aliased(BOWL));
        } else if (item instanceof GoatHornItem) {
            return SoundDefinition.of(single(LOOSE_METAL.getId(), 0.6f, 0.9f, Sound.RegistrationType.SOUND_EVENT));
        } else if (item instanceof DiscFragmentItem) {
            return SoundDefinition.of(single(METAL_BITS.getId(), 0.7f, 0.85f, Sound.RegistrationType.SOUND_EVENT));
        } else if (isGearGoldenItem(item)) {
            return SoundDefinition.of(aliased(Gear.GOLDEN));
        } else if (isGearLeatherItem(item)) {
            return SoundDefinition.of(aliased(Gear.LEATHER));
        } else if (isGearGenericItem(item)) {
            return SoundDefinition.of(aliased(Gear.GENERIC));
        } else if (isPaperItem(item)) {
            return SoundDefinition.of(aliased(PAPER));
        } else if (item instanceof BlockItem blockItem) {
            Block block = blockItem.getBlock();
            Identifier blockSound = block.getSoundGroup(block.getDefaultState()).getPlaceSound().getId();

            if (block instanceof AbstractRailBlock) {
                return SoundDefinition.of(aliased(RAIL));
            } else if (block instanceof BannerBlock) {
                return SoundDefinition.of(aliased(BANNER));
            } else if (block instanceof SeaPickleBlock) {
                return SoundDefinition.of(event(blockSound, 0.4f));
            } else if (block instanceof LeavesBlock || block instanceof PlantBlock || block instanceof SugarCaneBlock) {
                Identifier soundId = block.getSoundGroup(block.getDefaultState()).getPlaceSound().getId();
                if (soundId.getPath().equals("block.grass.place")) {
                    return SoundDefinition.of(aliased(LEAVES));
                } else {
                    return SoundDefinition.of(event(soundId));
                }
            } else if (block instanceof PillarBlock && ((BlockMaterialAccessor) block).getMaterial().equals(Material.FROGLIGHT)) {
                return SoundDefinition.of(event(blockSound, 0.3f));
            }

            return SoundDefinition.of(event(blockSound));
        }

        return SoundDefinition.of(aliased(ITEM_PICK));
    });
}
