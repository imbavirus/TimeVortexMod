package za.co.infernos.vortexmod.entities.villager;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;

import java.util.Optional;

public class ItemsForItems implements VillagerTrades.ItemListing {
    private final Item item;
    private final Item costItem;
    private final int cost;
    private Item costItem2 = null;
    private int cost2 = 0;
    private final int f_35736_;
    private final int maxUses;
    private final int villagerXp;
    private final float priceMultiplier;

    /*public ItemsForItems(Block pBlock, int pEmeraldCost, int pNumberOfItems, int pMaxUses, int pVillagerXp) {
        this(new ItemStack(pBlock), pEmeraldCost, pNumberOfItems, pMaxUses, pVillagerXp);
    }

    public ItemsForItems(Item pItem, int pEmeraldCost, int pNumberOfItems, int pVillagerXp) {
        this(new ItemStack(pItem), pEmeraldCost, pNumberOfItems, 12, pVillagerXp);
    }

    public ItemsForItems(Item pItem, int pEmeraldCost, int pNumberOfItems, int pMaxUses, int pVillagerXp) {
        this(new ItemStack(pItem), pEmeraldCost, pNumberOfItems, pMaxUses, pVillagerXp);
    }

    public ItemsForItems(ItemStack pItemStack, int pEmeraldCost, int pNumberOfItems, int pMaxUses, int pVillagerXp) {
        this(pItemStack, pEmeraldCost, pNumberOfItems, pMaxUses, pVillagerXp, 0.05F);
    }*/

    public ItemsForItems(Item pItem, Item costItem, int cost, int pNumberOfItems, int pMaxUses, int pVillagerXp, float pPriceMultiplier) {
        this.item = pItem;
        this.costItem = costItem;
        this.cost = cost;
        this.f_35736_ = pNumberOfItems;
        this.maxUses = pMaxUses;
        this.villagerXp = pVillagerXp;
        this.priceMultiplier = pPriceMultiplier;
    }

    public ItemsForItems(Item pItem, Item costItem, int cost, Item costItem2, int cost2, int pNumberOfItems, int pMaxUses, int pVillagerXp, float pPriceMultiplier) {
        this.item = pItem;
        this.costItem = costItem;
        this.cost = cost;
        this.costItem2 = costItem2;
        this.cost2 = cost2;
        this.f_35736_ = pNumberOfItems;
        this.maxUses = pMaxUses;
        this.villagerXp = pVillagerXp;
        this.priceMultiplier = pPriceMultiplier;
    }

    public MerchantOffer getOffer(Entity pTrader, RandomSource pRandom) {
        ItemCost costA = new ItemCost(costItem, cost);
        Optional<ItemCost> costB = costItem2 == null ? Optional.empty() : Optional.of(new ItemCost(costItem2, cost2));
        return new MerchantOffer(costA, costB, new ItemStack(this.item, this.f_35736_), this.maxUses, this.villagerXp, this.priceMultiplier);
    }
}