package com.EvilNotch.lib.minecraft.proxy;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

import com.EvilNotch.lib.Api.BlockApi;
import com.EvilNotch.lib.Api.FieldAcess;
import com.EvilNotch.lib.Api.FieldAcessClient;
import com.EvilNotch.lib.Api.MCPMappings;
import com.EvilNotch.lib.Api.ReflectionUtil;
import com.EvilNotch.lib.main.Config;
import com.EvilNotch.lib.main.ConfigMenu;
import com.EvilNotch.lib.main.MainJava;
import com.EvilNotch.lib.main.eventhandlers.ClientEvents;
import com.EvilNotch.lib.minecraft.content.ConfigLang;
import com.EvilNotch.lib.minecraft.content.LangEntry;
import com.EvilNotch.lib.minecraft.content.LangLine;
import com.EvilNotch.lib.minecraft.content.blocks.BasicBlock;
import com.EvilNotch.lib.minecraft.content.blocks.IBasicBlock;
import com.EvilNotch.lib.minecraft.content.client.ClientUUID;
import com.EvilNotch.lib.minecraft.content.client.block.ModelPart;
import com.EvilNotch.lib.minecraft.content.client.block.StateMapperSupreme;
import com.EvilNotch.lib.minecraft.content.client.creativetab.BasicCreativeTab;
import com.EvilNotch.lib.minecraft.content.client.gui.MenuRegistry;
import com.EvilNotch.lib.minecraft.content.items.BasicItem;
import com.EvilNotch.lib.minecraft.content.items.IBasicItem;
import com.EvilNotch.lib.util.JavaUtil;
import com.EvilNotch.lib.util.Line.Comment;
import com.EvilNotch.lib.util.Line.ConfigBase;
import com.EvilNotch.lib.util.Line.IHead;
import com.EvilNotch.lib.util.Line.ILine;
import com.EvilNotch.lib.util.Line.LineItemStack;
import com.EvilNotch.lib.util.simple.PairString;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.LanguageManager;
import net.minecraft.client.resources.Locale;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemSword;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.util.text.translation.LanguageMap;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientProxy extends ServerProxy{
	
	public static String currentLang = null;
	public static Map<String, String> langlistClient = null;
	public static  Map<String,String> langlist = null;
	public static File root = null;

	@Override
	public void proxypreinit()
	{
		MainJava.isClient = true;
	}
	
	@Override
	public void preinit(FMLPreInitializationEvent e) 
	{
		super.preinit(e);
		
		ConfigMenu.loadMenuLib(e.getModConfigurationDirectory());
		FieldAcessClient.cacheFields();
		MinecraftForge.EVENT_BUS.register(this);
		MinecraftForge.EVENT_BUS.register(new ClientEvents());
		MenuRegistry.registerGuiMenu(GuiMainMenu.class, new ResourceLocation("mainmenu"));
		if(Config.debug)
			ClientCommandHandler.instance.registerCommand(new ClientUUID());
	}
	@Override
	public void jsonGen() throws Exception
	{
		if(!MainJava.isDeObfuscated)
		{
			System.out.println("json generation only occurs on the client side in dev enviorment");
			return;
		}
		boolean flag = false;
		for(IBasicItem i : MainJava.items)
		{
			if(!i.registerModel())
			{
				System.out.println("skipping model gen:" + i.getRegistryName());
				continue;
			}
			JSONObject json = null;
			ResourceLocation loc = i.getRegistryName();
			if(i instanceof ItemAxe || i instanceof ItemHoe || i instanceof ItemPickaxe || i instanceof ItemSpade || i instanceof ItemSword)
			{
				json = getJSONItem("item/handheld", i);
				File file = new File(root,loc.getResourceDomain() + "/models/item/" + loc.getResourcePath() + ".json");
				if(!file.exists())
					flag = true;
				JavaUtil.saveJSONSafley(json, file);
			}
			else if(i.isMeta())
			{
				for(int index=0;index<=i.getMaxMeta();index++)
				{
					json = getJSONItem("item/generated", i,index);
					File file = new File(root,loc.getResourceDomain() + "/models/item/" + loc.getResourcePath() + "_" + index + ".json");
					if(!file.exists())
						flag = true;
					JavaUtil.saveJSONSafley(json, file);
				}
			}
			else
			{
				//for both item armor and regular items this is the parent
				json = getJSONItem("item/generated", i);
				File file = new File(root,loc.getResourceDomain() + "/models/item/" + loc.getResourcePath() + ".json");
				if(!file.exists())
					flag = true;
				JavaUtil.saveJSONSafley(json, file);
			}
		}
		for(IBasicBlock b : MainJava.blocks)
		{
			if(!b.registerModel())
			{
				System.out.println("skipping model gen:" + b.getRegistryName());
				continue;
			}
			ResourceLocation loc = b.getRegistryName();
			
			//blockstate gen
			List<String> names = b.getBlockStatesNames();
			
			//block model gen
			if(b.isMeta())
			{
				//blockstates
				JSONObject bs = getJSONBlockState(names,b);
				File fbs = new File(root,loc.getResourceDomain() + "/blockstates/" + loc.getResourcePath() + ".json");
				if(!fbs.exists())
					flag = true;
				JavaUtil.saveJSONSafley(bs, fbs);
				
				for(String s : names)
				{
					String prop = s.split("=")[1];
					
					JSONObject block = getModelBlock(b,prop);
					File mBlockFile = new File(root,loc.getResourceDomain() + "/models/block/" + loc.getResourcePath() + "_" + prop + ".json");
					if(!mBlockFile.exists())
						flag = true;
					JavaUtil.saveJSONSafley(block, mBlockFile);
					
					//itemblock part
					JSONObject item = getJSONBlockItem(b,prop);
					File itemFile = new File(root,loc.getResourceDomain() + "/models/item/" + loc.getResourcePath() + "_" + prop + ".json");
					if(!itemFile.exists())
						flag = true;
					JavaUtil.saveJSONSafley(item, itemFile);
				}
			}
			else
			{
				JSONObject block = getModelBlock(b,names.get(0));
				File bmodel = new File(root,loc.getResourceDomain() + "/models/block/" + loc.getResourcePath() + ".json");
				
				if(!bmodel.exists())
					flag = true;
				
				JavaUtil.saveJSONSafley(block, bmodel);
				
				JSONObject item = getJSONBlockItem(b,names.get(0));
				File itemFile = new File(root,loc.getResourceDomain() + "/models/item/" + loc.getResourcePath() + ".json");
				
				if(!itemFile.exists())
					flag = true;
				
				JavaUtil.saveJSONSafley(item, itemFile);
			}
		}
		if(flag)
		{
			System.out.println("refreshing resources as a new json has been generated");
			Minecraft.getMinecraft().refreshResources();
		}
	}

	public static JSONObject getJSONBlockItem(IBasicBlock b,String name) {
		JSONObject json = new JSONObject();
		ResourceLocation loc = b.getRegistryName();
		json.put("parent", loc.getResourceDomain() + ":block/" + (name.equals("normal") ? loc.getResourcePath() : loc.getResourcePath() + "_" + name) );
		return json;
	}

	public static JSONObject getJSONBlockState(List<String> names,IBasicBlock b) 
	{
		JSONObject json = new JSONObject();
		JSONObject varients = new JSONObject();
		JSONObject normal = new JSONObject();
		
		if(b.isMeta())
		{
			IProperty p = b.getStateProperty();
			for(String s : names)
			{
				JSONObject model = new JSONObject();
				String name = s.split("=")[1];
				model.put("model", b.getRegistryName() + "_" + name);
				varients.put(p.getName() + "=" + name, model);
			}
		}
		else
		{
			normal.put("model", b.getRegistryName().toString());
			varients.put("normal", normal);
		}
		
		json.put("variants", varients);
		
		return json;
	}

	/**
	 * supports textures with multiple states and multi textured blocks at the same time
	 */
	public static JSONObject getModelBlock(IBasicBlock b,String stateValue) 
	{
		ModelPart base = b.getModelPart();
		Block block = (Block)b;
		JSONObject json = new JSONObject();
		json.put("parent", base.parent);
		
		JSONObject textures = new JSONObject();
		ResourceLocation loc = block.getRegistryName();
		String particle = "particle";
		for(PairString pair : base.keySet)
		{
			if(pair.obj1.equals(particle) && base.customParticle)
			{
				textures.put(particle, loc.getResourceDomain() + ":blocks/" + (stateValue.equals("normal") ? b.getTextureName() + "_" + particle : b.getTextureName() + "_" + stateValue + "_" + particle) );
				continue;
			}
			
			String texture = loc.getResourceDomain() + ":blocks/" + (stateValue.equals("normal") ? b.getTextureName() : b.getTextureName() + "_" + stateValue);
			if(base.customSide)
			{
				texture += "_" + pair.getValue();
			}
			textures.put(pair.getValue(),texture);
		}
		json.put("textures",textures);
		return json;
	}
	
	public static JSONObject getJSONItem(String parent,IBasicItem i) {
		return getJSONItem(parent,i,0);
	}

	public static JSONObject getJSONItem(String parent,IBasicItem i,int meta) {
		Item item = (Item)i;
		JSONObject json = new JSONObject();
		json.put("parent", parent);
		
		JSONObject textures = new JSONObject();
		ResourceLocation loc = item.getRegistryName();
		if(i.isMeta())
		{
			textures.put("layer0", loc.getResourceDomain() + ":items/" + i.getTextureName() + "_" + meta);
		}
		else
			textures.put("layer0", loc.getResourceDomain() + ":items/" + i.getTextureName());
		json.put("textures",textures);
		return json;
	}

	@Override
	public void lang() 
	{
		if(!MainJava.isDeObfuscated)
		{
			System.out.println("lan generation only occurs in dev enviorment on client:");
			return;
		}
		root  = new File(Config.cfg.getParentFile().getParentFile().getParentFile().getParentFile(),"src/main/resources/assets");
		if(!root.exists())
			root.mkdirs();
		HashMap<File,ConfigLang> cfgs = new HashMap();
		populateLang(root,BasicBlock.blocklangs,cfgs);
		populateLang(root,BasicItem.itemlangs,cfgs);
		populateLang(root,BasicCreativeTab.creativeTabLang,cfgs);
		
		if(langlistClient == null)
		{
			LanguageManager manager = Minecraft.getMinecraft().getLanguageManager();
			Locale l = (Locale)ReflectionUtil.getObject(manager, LanguageManager.class, FieldAcessClient.CURRENT_LOCALE);
			Map<String, String> map = (Map<String, String>) ReflectionUtil.getObject(l, Locale.class, FieldAcessClient.properties);
			langlistClient = map;
		}
		if(langlist == null)
		{
			LanguageMap manager = (LanguageMap) ReflectionUtil.getObject(null, I18n.class, FieldAcess.lang_localizedName);
			langlist = (Map<String, String>) ReflectionUtil.getObject(manager, LanguageMap.class, MCPMappings.getField(LanguageMap.class, "languageList"));
		}
		for(ConfigLang cfg : cfgs.values())
		{
			for(ILine line : cfg.lines)
			{
				String key = line.getModPath();
				String value = (String) line.getHead();
				if(!langlistClient.containsKey(key))
				{
					if(Config.debug)
						System.out.println("injecting:" + line.getString());
					langlistClient.put(key,value);
				}
				if(!langlist.containsKey(key))
				{
					if(Config.debug)
						System.out.println("injectingServer:" + line.getString());
					langlist.put(key,value);
				}
			}
		}
	}
	/**
	 * generate lang files here
	 */
	public void populateLang(File root, ArrayList<LangEntry> li,HashMap<File,ConfigLang> map) 
	{
		String currentLang = getCurrentLang();
		for(LangEntry lang : li)
		{
			File file = new File(root,lang.loc.getResourceDomain() + "/lang/" + currentLang + ".lang");
			ConfigLang cfg = map.get(file);
			if(cfg == null)
			{
				cfg = new ConfigLang(file);
				map.put(file, cfg);
			}
			LangLine line = new LangLine(lang.getString());
			cfg.addLine(line);
		}
		for(ConfigLang lang : map.values())
		{	
			lang.updateConfig();
		}
	}

	public static String getCurrentLang() 
	{
		 return Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode();
	}

	/**
	 * Create fast utf-8 instanceof ConfigLang then do manual injections
	 */
	public void injectClientLang(File f) 
	{
		ConfigLang cfg = new ConfigLang(f);
		injectClientLang(cfg);
	}

	public void injectClientLang(ConfigLang cfg) 
	{
		for(ILine line : cfg.lines)
		{
			String key = line.getModPath();
			String value = (String) line.getHead();
			if(!langlistClient.containsKey(key))
				langlistClient.put(key,value);
		}
	}

	@Override
	public void initMod()
	{
		menuLibInit();
		super.initMod();
	}
	
	@Override
	public void postinit()
	{
		super.postinit();
	}

	/**
	 * Reorder menus or if client overrides using whitelist do only the whitelist
	 */
	public static void menuLibInit() 
	{	
		//register user registered menus
		File f = new File(ConfigMenu.cfgmenu.getParent(),"menulib.cfg");
		ArrayList<Comment> comments = (ArrayList<Comment>)JavaUtil.asArray(new Comment[]{new Comment("Menu Lib Configuration File. Register Other Mod's Main Menus That refuse to do it themselves :("),new Comment("Format is: \"modid:mainmenu\" = \"class.full.name\"")});
		ConfigBase cfg = new ConfigBase(f,comments);
		if(Loader.isModLoaded("thebetweenlands"))
		{
			cfg.addLine(new LineItemStack("\"thebetweenlands:mainmenu\" = \"thebetweenlands.client.gui.menu.GuiBLMainMenu\""));
			cfg.updateConfig(false, false, true);
		}
		for(ILine line : cfg.lines)
		{
			IHead head = (IHead)line;
			try
			{
				MenuRegistry.registerGuiMenu((Class<? extends GuiScreen>) Class.forName(head.getStringHead()), line.getResourceLocation());
			}
			catch(Throwable t)
			{
				System.out.print("[MenuLib/ERR] Unable to Locate class skipping menu registration for:" + line.getString() + "\n");
			}
		}
		
		MenuRegistry.reOrder();
		MenuRegistry.setCurrentMenu(ConfigMenu.currentMenuIndex);
	}

	/**
	 * future:Generate models with textures coming from the registry name
	 */
	@SubscribeEvent(priority=EventPriority.HIGH)
	public void modeltest(ModelRegistryEvent event)
	{
		modelpreInit();
	}
	
	public static void modelpreInit() 
	{
		System.out.println("Loading MODELS");
		for(IBasicItem i : MainJava.items)
	    {
		   if(i.registerModel())
		   {
			  Item item = (Item)i;
			  if(i.isMeta())
			  {
				 List<ResourceLocation> locs = new ArrayList();
				 for(int index=0;index<=i.getMaxMeta();index++)
				 {
					 ModelLoader.setCustomModelResourceLocation(item, index, new ModelResourceLocation(item.getRegistryName() + "_" + index,"inventory"));
				 }
			  }
			  else
			  {
				  ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(BlockApi.getItemString(item), "inventory"));
			  }
		   }
	   }
	   for(IBasicBlock i : MainJava.blocks)
	   {
		   if(i.registerModel())
		   {
			   Block b = (Block)i;
			   Item item = Item.getItemFromBlock(b);
			   
			   if(i.isMeta() )
			   {
					ModelLoader.setCustomStateMapper((Block) b, new StateMapperSupreme());
					HashMap<Integer,ModelResourceLocation> metas = i.getModelMap();
					Iterator<Map.Entry<Integer,ModelResourceLocation>> it = metas.entrySet().iterator();
					while(it.hasNext())
					{
						Map.Entry<Integer, ModelResourceLocation> pair = it.next();
						int meta = pair.getKey();
						ModelResourceLocation loc = pair.getValue();
						ModelLoader.setCustomModelResourceLocation(item, meta, loc);
					}
			   }
			   else
			   {
				   for(String s : i.getModelStates())
				   {
					   ModelLoader.setCustomModelResourceLocation(item, 0,  new ModelResourceLocation(BlockApi.getBlockString(b), s));
			   	   }
			   }
		   }
	   }
	}

	public static EntityPlayer getPlayer() {
		return FMLClientHandler.instance().getClientPlayerEntity();
	}

}
