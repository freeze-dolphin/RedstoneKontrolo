package io.freeze_dolphin.redstone_kontrolo;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import io.freeze_dolphin.redstone_kontrolo.bytecode.MethodUpdater;

import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.CSCoreLibPlugin.events.ItemUseEvent;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu.MenuClickHandler;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ClickAction;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.Item.CustomItem;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Lists.SlimefunItems;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.Research;
import me.mrCookieSlime.Slimefun.Objects.SlimefunBlockHandler;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.UnregisterReason;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.handlers.ItemHandler;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.handlers.ItemInteractionHandler;
import me.mrCookieSlime.Slimefun.api.BlockStorage;

public class RedstoneKontrolo extends JavaPlugin {

	private Config config = new Config(this);
	private Plugin plugin = this;

	@Override
	public void onEnable() {
		updateTicker();
		items();
	}

	@SuppressWarnings("deprecation")
	private void items() {

		String[] clore = getC().contains("value.hide-creator-name") ? new String[] {"", "&a> 点击打开"} : new String[] {"", "&7&oCreated by Freeze_Dolphin", "", "&a> 点击打开"};
		Category c = new Category(new CustomItem(Material.REDSTONE_COMPARATOR, "&c红石控制&r", 0, clore) , 4);

		Research r0 = new Research(getC().getInt("value.research.id-start" + 0), "Redstone Kontrolo Parts", 24);

		SlimefunItem REDSTONE_KONTROLADOR = new SlimefunItem(c, new CustomItem(new MaterialData(Material.STAINED_CLAY, (byte) 14), "&c红石控制器&r"), "REDSTONE_KONTROLADOR", RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
				m(Material.REDSTONE_COMPARATOR), SlimefunItems.REDSTONE_ALLOY, m(Material.REDSTONE_COMPARATOR), 
				m(Material.GLASS), m(Material.QUARTZ_BLOCK), m(Material.GLASS), 
				m(Material.REDSTONE_COMPARATOR), SlimefunItems.BASIC_CIRCUIT_BOARD, m(Material.REDSTONE_COMPARATOR)
		});
		REDSTONE_KONTROLADOR.register(false);

		SlimefunItem REDSTONE_CIRCUIT_BOARD = new SlimefunItem(c, new CustomItem(new MaterialData(Material.ACTIVATOR_RAIL), "&c红石电路板&r"), "REDSTONE_CIRCUIT_BOARD", RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
				SlimefunItems.ALUMINUM_INGOT, REDSTONE_KONTROLADOR.getItem(), SlimefunItems.ALUMINUM_INGOT, 
				SlimefunItems.STEEL_INGOT, SlimefunItems.ADVANCED_CIRCUIT_BOARD, SlimefunItems.STEEL_INGOT, 
				SlimefunItems.ALUMINUM_INGOT, REDSTONE_KONTROLADOR.getItem(), SlimefunItems.ALUMINUM_INGOT
		});
		REDSTONE_CIRCUIT_BOARD.register(false);

		r0.addItems(REDSTONE_KONTROLADOR, REDSTONE_CIRCUIT_BOARD);

		Research r1 = new Research(getC().getInt("value.research.id-start" + 1), "Redstone Wrench", 26);

		SlimefunItem REDSTONE_WRENCH = new SlimefunItem(c, new CustomItem(new MaterialData(Material.WATCH), "&c红石电路板交互器&r", "", "&f通过与机器内的红石电路板交互", "&f从而修改机器与红石控制相关的设置", ""), "REDSTONE_WRENCH", RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
				SlimefunItems.COPPER_INGOT, m(Material.QUARTZ), SlimefunItems.COPPER_INGOT, 
				REDSTONE_KONTROLADOR.getItem(), SlimefunItems.MULTIMETER, REDSTONE_KONTROLADOR.getItem(), 
				SlimefunItems.BATTERY, SlimefunItems.ADVANCED_CIRCUIT_BOARD, SlimefunItems.BATTERY
		});
		REDSTONE_WRENCH.register(false, new ItemInteractionHandler() {

			@Override
			public boolean onRightClick(ItemUseEvent e, Player p, ItemStack item) {
				if (e.isCancelled()) return false;
				
				Block b = e.getClickedBlock();
				if (BlockStorage.hasBlockInfo(b)) {
					openSettingPanel(b, p);
				}
				
				return false;
			}});

		r1.addItems(REDSTONE_WRENCH);

	}

	private static final int[] border = 
		{
				0,   1,  2,  3,  4,  5,  6,  7,  8, 
				9,      11,     13,     15,     16, 
				17, 18, 19, 20, 21, 22, 23, 24, 25
		};
	
	@SuppressWarnings("deprecation")
	private void openSettingPanel(Block b, Player p) {
		ChestMenu cm = new ChestMenu("&8红石控制&r");
		
		for (int slot : border) {
			cm.addItem(slot, new CustomItem(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 8), " "), new MenuClickHandler() {
				
				@Override
				public boolean onClick(Player arg0, int arg1, ItemStack arg2, ClickAction arg3) { return false; }
			});
		}
		
		cm.addItem(10, new CustomItem(new MaterialData(Material.EMPTY_MAP), "&2模式 &e(点击切换)&r", "", "&b当前模式: &f" + BlockStorage.getBlockInfo(b, "redstone-kontrolo").toUpperCase().replaceAll("-", "_")), new MenuClickHandler() {
			
			@Override
			public boolean onClick(Player arg0, int arg1, ItemStack arg2, ClickAction arg3) {
				
				return false;
			}
		});
		
	}

	private void updateTicker() {
		for (String s : getC().getStringList("cglib.class-to-modify")) {
			if (s.matches("(.*)\\.\\*")) {
				for (Class<?> c : ClassUtil.getClasses(s.split("\\.\\*")[0])) {
					updateMethod(c.getCanonicalName());
				}
			} else {
				updateMethod(s);
			}
		}
	}

	private void updateMethod(String canonicalName) {
		try {
			Class<?> clazz = Class.forName(canonicalName);
			if (clazz.getSuperclass().getCanonicalName().equals(getC().getString("cglib.superclass-canonical-name"))) {
				MethodUpdater mu = new MethodUpdater(this, clazz);
				try {
					mu.update();
				} catch (Exception e) {
					e.printStackTrace();
					getLogger().severe("Error occurred while modifying method for class '" + clazz.getCanonicalName() + "'");
				}
			}
		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
			getLogger().severe("A specific class is not exist, check and edit your 'config.yml' to fix it");
		}
	}

	public Config getC() {
		return config;
	}

	public Plugin getInstance() {
		return plugin;
	}

	private ItemStack m(Material m) {
		return new ItemStack(m);
	}

}
