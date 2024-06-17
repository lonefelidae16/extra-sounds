package dev.stashy.extrasounds.logics.entry;

import dev.stashy.extrasounds.logics.ExtraSounds;
import dev.stashy.extrasounds.logics.SoundManager;
import dev.stashy.extrasounds.mapping.SoundDefinition;
import dev.stashy.extrasounds.mapping.SoundGenerator;
import me.lonefelidae16.groominglib.api.McVersionInterchange;
import net.minecraft.block.*;
import net.minecraft.client.sound.SoundEntry;
import net.minecraft.item.*;
import net.minecraft.util.Identifier;

import java.lang.reflect.Method;
import java.util.Objects;

import static dev.stashy.extrasounds.sounds.Categories.*;
import static dev.stashy.extrasounds.sounds.Sounds.aliased;
import static dev.stashy.extrasounds.sounds.Sounds.event;

public abstract class BaseVanillaGenerator {
    protected static final SoundDefinition DEFAULT_SOUND = SoundDefinition.of(aliased(SoundManager.FALLBACK_SOUND_EVENT));
    public static final SoundGenerator GENERATOR;

    static {
        SoundGenerator result = null;
        try {
            Class<BaseVanillaGenerator> clazz = McVersionInterchange.getCompatibleClass(ExtraSounds.BASE_PACKAGE, "entry.VanillaGenerator");
            Method generator = clazz.getMethod("generate");
            result = (SoundGenerator) generator.invoke(null);
        } catch (Exception ex) {
            ExtraSounds.LOGGER.error("Cannot initialize 'VanillaGenerator'", ex);
        }
        GENERATOR = Objects.requireNonNull(result);
    }

    protected static String getItemIdPath(Item item) {
        return ExtraSounds.fromItemRegistry(item).getPath();
    }

    protected static boolean isBrickItem(Item item) {
        final String idPath = getItemIdPath(item);
        return item == Items.BRICK || idPath.endsWith("pottery_sherd") || idPath.startsWith("pottery_shard");
    }

    protected static boolean isPaperItem(Item item) {
        return item instanceof BannerPatternItem || item instanceof BookItem || item instanceof WritableBookItem ||
                item instanceof WrittenBookItem || item instanceof EnchantedBookItem || item instanceof EmptyMapItem ||
                item instanceof FilledMapItem || item instanceof NameTagItem || item instanceof KnowledgeBookItem;
    }

    protected static boolean isGearGoldenItem(Item item) {
        return item instanceof CompassItem ||
                item instanceof SpyglassItem || item instanceof ShearsItem;
    }

    protected static boolean isGearLeatherItem(Item item) {
        return item instanceof LeadItem || item instanceof ElytraItem || item instanceof SaddleItem;
    }

    protected static boolean isGearGenericItem(Item item) {
        return item instanceof BowItem || item instanceof CrossbowItem || item instanceof FishingRodItem ||
                item instanceof OnAStickItem;
    }

    protected static SoundDefinition generateFromToolMaterial(ToolMaterial mat) {
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

    protected static SoundDefinition generateFromBlock(Block block) {
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
        }

        return SoundDefinition.of(event(blockSoundId));
    }

    protected static SoundDefinition generalSounds(Item item) {
        if (item instanceof BoatItem) {
            return SoundDefinition.of(aliased(BOAT));
        } else if (item instanceof ShieldItem) {
            return SoundDefinition.of(aliased(Gear.IRON));
        } else if (item instanceof BucketItem bucketItem) {
            final SoundEntry soundEntry = bucketItem.fluid.getBucketFillSound().map(sound -> event(sound.getId(), 0.4f)).orElse(aliased(METAL));
            return SoundDefinition.of(soundEntry);
        } else if (item instanceof MinecartItem) {
            return SoundDefinition.of(aliased(MINECART));
        } else if (item instanceof ItemFrameItem) {
            return SoundDefinition.of(aliased(FRAME));
        } else if (item instanceof ArrowItem) {
            return SoundDefinition.of(aliased(ARROW));
        } else if (item instanceof DyeItem) {
            return SoundDefinition.of(aliased(DUST));
        } else if (item instanceof SpawnEggItem) {
            return SoundDefinition.of(aliased(WET_SLIPPERY));
        }

        return DEFAULT_SOUND;
    }
}
