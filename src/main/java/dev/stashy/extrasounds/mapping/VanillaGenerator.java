package dev.stashy.extrasounds.mapping;

import dev.stashy.extrasounds.ExtraSounds;
import dev.stashy.extrasounds.mixin.BlockMaterialAccessor;
import dev.stashy.extrasounds.mixin.BucketFluidAccessor;
import net.minecraft.block.*;
import net.minecraft.client.sound.Sound;
import net.minecraft.client.sound.SoundEntry;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static dev.stashy.extrasounds.sounds.Categories.*;
import static dev.stashy.extrasounds.sounds.Sounds.*;

public class VanillaGenerator
{
    public static final Map<Class<? extends Item>, Function<Item, SoundEntry>> map = new HashMap<>();

    public static SoundGenerator generator = SoundGenerator.of("minecraft", ExtraSounds.MODID, item -> {
        Identifier id = Registries.ITEM.getId(item);
        Class cls = item.getClass();
        while (!map.containsKey(cls) && cls.getSuperclass() != null && Item.class.isAssignableFrom(cls.getSuperclass()))
            cls = cls.getSuperclass();
        return SoundDefinition.of(map.containsKey(cls) ? map.get(cls).apply(item) : aliased(ITEM_PICK));
    });

    static
    {
        map.put(MusicDiscItem.class, it -> aliased(MUSIC_DISC));
        map.put(BoatItem.class, it -> aliased(BOAT));
        map.put(ToolItem.class, it -> {
            if (((ToolItem) it).getMaterial() instanceof ToolMaterials mat)
                return switch (mat)
                        {
                            case WOOD -> aliased(Gear.WOOD);
                            case STONE -> aliased(Gear.STONE);
                            case IRON -> aliased(Gear.IRON);
                            case GOLD -> aliased(Gear.GOLDEN);
                            case DIAMOND -> aliased(Gear.DIAMOND);
                            case NETHERITE -> aliased(Gear.NETHERITE);
                            default -> aliased(Gear.GENERIC);
                            //⬆ even though not required, this is in case any mods add to the enum of materials
                        };
            else
                return aliased(Gear.GENERIC);
        });
        map.put(ArmorItem.class, it -> {
            if (((ArmorItem) it).getMaterial() instanceof ArmorMaterials mat)
                return switch (mat)
                        {
                            case IRON -> aliased(Gear.IRON);
                            case GOLD -> aliased(Gear.GOLDEN);
                            case DIAMOND -> aliased(Gear.DIAMOND);
                            case NETHERITE -> aliased(Gear.NETHERITE);
                            case CHAIN -> aliased(Gear.CHAIN);
                            case TURTLE -> aliased(Gear.TURTLE);
                            case LEATHER -> aliased(Gear.LEATHER);
                            default -> aliased(Gear.GENERIC);
                        };
            else
                return aliased(Gear.GENERIC);
        });
        map.put(ShieldItem.class, it -> aliased(Gear.IRON));
        putMulti(it -> aliased(Gear.GOLDEN), HorseArmorItem.class, CompassItem.class, SpyglassItem.class,
                 ShearsItem.class);
        putMulti(it -> aliased(Gear.LEATHER), LeadItem.class, ElytraItem.class, SaddleItem.class);
        putMulti(it -> aliased(Gear.GENERIC), BowItem.class, CrossbowItem.class, FishingRodItem.class,
                 OnAStickItem.class);
        map.put(BucketItem.class, it -> {
            var f = ((BucketFluidAccessor) it).getFluid();
            return f.getBucketFillSound().isPresent() ?
                    event(f.getBucketFillSound().get().getId(), 0.4f) : aliased(METAL);
        });
        map.put(MinecartItem.class, it -> aliased(MINECART));
        map.put(ItemFrameItem.class, it -> aliased(FRAME));
        putMulti(it -> aliased(POTION), PotionItem.class, ExperienceBottleItem.class);
        putMulti(it -> aliased(PAPER), BannerPatternItem.class, BookItem.class, WritableBookItem.class,
                 WrittenBookItem.class,
                 EnchantedBookItem.class, EmptyMapItem.class, FilledMapItem.class, NameTagItem.class);
        map.put(ArrowItem.class, it -> aliased(ARROW));
        map.put(DyeItem.class, it -> aliased(DUST));
        map.put(SpawnEggItem.class, it -> aliased(WET_SLIPPERY));
        putMulti(it -> aliased(BOWL), StewItem.class, SuspiciousStewItem.class);
        map.put(GoatHornItem.class, it -> single(LOOSE_METAL.getId(), 0.6f, 0.9f, Sound.RegistrationType.SOUND_EVENT));
        map.put(DiscFragmentItem.class, it -> single(METAL_BITS.getId(), 0.7f, 0.85f, Sound.RegistrationType.SOUND_EVENT));
        map.put(BlockItem.class, it -> {
            Block b = ((BlockItem) it).getBlock();
            Identifier blockSound = b.getSoundGroup(b.getDefaultState()).getPlaceSound().getId();

            if (b instanceof AbstractRailBlock)
                return aliased(RAIL);
            else if (b instanceof BannerBlock)
                return aliased(BANNER);
            else if (b instanceof SeaPickleBlock)
                return event(blockSound, 0.4f);
            else if (b instanceof LeavesBlock || b instanceof PlantBlock || b instanceof SugarCaneBlock)
            {
                Identifier soundId = b.getSoundGroup(b.getDefaultState()).getPlaceSound().getId();
                if (soundId.getPath().equals("block.grass.place"))
                    return aliased(LEAVES);
                else
                    return event(soundId);
            }
            else if (b instanceof PillarBlock && ((BlockMaterialAccessor) b).getMaterial().equals(Material.FROGLIGHT))
            {
                return event(blockSound, 0.3f);
            }

            return event(blockSound);
        });
    }

    @SafeVarargs
    private static void putMulti(Function<Item, SoundEntry> entry, Class<? extends Item>... classez)
    {
        for (var clazz : classez)
            map.put(clazz, entry);
    }
}
