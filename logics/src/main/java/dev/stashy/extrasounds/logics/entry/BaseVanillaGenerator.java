package dev.stashy.extrasounds.logics.entry;

import dev.stashy.extrasounds.logics.ExtraSounds;
import dev.stashy.extrasounds.logics.SoundManager;
import dev.stashy.extrasounds.logics.runtime.VersionedSoundEventWrapper;
import dev.stashy.extrasounds.mapping.SoundDefinition;
import dev.stashy.extrasounds.mapping.SoundGenerator;
import me.lonefelidae16.groominglib.api.McVersionInterchange;
import net.minecraft.block.*;
import net.minecraft.item.*;
import net.minecraft.util.Identifier;

import java.util.Objects;

import static dev.stashy.extrasounds.sounds.Categories.*;
import static dev.stashy.extrasounds.sounds.Sounds.aliased;
import static dev.stashy.extrasounds.sounds.Sounds.event;

public abstract class BaseVanillaGenerator {
    private static final SoundDefinition DEFAULT_SOUND = SoundDefinition.of(aliased(SoundManager.FALLBACK_SOUND_EVENT));
    public static final SoundGenerator GENERATOR;

    static {
        BaseVanillaGenerator instance = null;
        try {
            Class<BaseVanillaGenerator> clazz = McVersionInterchange.getCompatibleClass(ExtraSounds.BASE_PACKAGE, "entry.VanillaGenerator");
            instance = clazz.getConstructor().newInstance();
        } catch (Exception ex) {
            ExtraSounds.LOGGER.error("Cannot initialize 'VanillaGenerator'", ex);
        }
        GENERATOR = Objects.requireNonNull(instance).generate();
    }

    protected abstract SoundGenerator generate();

    protected String getItemIdPath(Item item) {
        return ExtraSounds.getItemId(item).getPath();
    }

    private boolean isBrickItem(Item item) {
        final String idPath = getItemIdPath(item);
        return item == Items.BRICK || idPath.endsWith("pottery_sherd") || idPath.startsWith("pottery_shard");
    }

    private boolean isGearGoldenItem(Item item) {
        return item instanceof CompassItem ||
                item instanceof SpyglassItem || item instanceof ShearsItem;
    }

    private boolean isGearLeatherItem(Item item) {
        return item instanceof LeadItem || getItemIdPath(item).equals("elytra") ||
                item instanceof SaddleItem;
    }

    private boolean isGearGenericItem(Item item) {
        return item instanceof BowItem || item instanceof CrossbowItem || item instanceof FishingRodItem ||
                item instanceof OnAStickItem;
    }

    private boolean isPaperItem(Item item) {
        return item instanceof BannerPatternItem || item instanceof WritableBookItem ||
                item instanceof WrittenBookItem || item instanceof EmptyMapItem ||
                item instanceof FilledMapItem || item instanceof NameTagItem || item instanceof KnowledgeBookItem ||
                item == Items.BOOK || item == Items.ENCHANTED_BOOK;
    }

    private boolean isStewItem(Item item) {
        return item == Items.SUSPICIOUS_STEW || item == Items.RABBIT_STEW ||
                item == Items.BEETROOT_SOUP || item == Items.MUSHROOM_STEW;
    }

    protected SoundDefinition generateFromBlock(Block block) {
        final BlockState blockState = block.getDefaultState();
        final Identifier blockSoundId = VersionedSoundEventWrapper.fromBlockState(blockState).getId();

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

    protected SoundDefinition generalSounds(Item item) {
        if (item instanceof BoatItem) {
            return SoundDefinition.of(aliased(BOAT));
        } else if (item instanceof ShieldItem) {
            return SoundDefinition.of(aliased(Gear.IRON));
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
        } else if (this.getItemIdPath(item).startsWith("music_disc_")) {
            return SoundDefinition.of(aliased(MUSIC_DISC));
        } else if (this.isBrickItem(item)) {
            return SoundDefinition.of(aliased(BRICK));
        } else if (this.isGearGoldenItem(item)) {
            return SoundDefinition.of(aliased(Gear.GOLDEN));
        } else if (this.isGearLeatherItem(item)) {
            return SoundDefinition.of(aliased(Gear.LEATHER));
        } else if (this.isGearGenericItem(item)) {
            return SoundDefinition.of(aliased(Gear.GENERIC));
        } else if (this.isPaperItem(item)) {
            return SoundDefinition.of(aliased(PAPER));
        } else if (this.isStewItem(item)) {
            return SoundDefinition.of(aliased(BOWL));
        } else if (item instanceof BundleItem) {
            return SoundDefinition.of(aliased(BUNDLES));
        }

        return DEFAULT_SOUND;
    }
}
