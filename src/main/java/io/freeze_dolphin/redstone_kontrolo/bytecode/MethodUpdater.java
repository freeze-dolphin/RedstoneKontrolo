package io.freeze_dolphin.redstone_kontrolo.bytecode;

import java.lang.reflect.Method;
import java.util.List;

import org.bukkit.block.Block;

import io.freeze_dolphin.redstone_kontrolo.RedstoneKontrolo;

import me.mrCookieSlime.Slimefun.api.BlockStorage;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class MethodUpdater {

	private final Class<?> clazz;
	private final RedstoneKontrolo plug;

	public MethodUpdater(RedstoneKontrolo plug, Class<?> clazz) {
		this.plug = plug;
		this.clazz = clazz;
	}

	public void update() throws Exception {
		Enhancer e = new Enhancer();
		e.setSuperclass(clazz);
		e.setCallback(new MethodInterceptor() {

			@Override
			public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
				if (checkMethod(method.getClass()) && checkArguments(args)) {
					Block b = (Block) args[0];

					@SuppressWarnings("deprecation")
					int setting = Integer.parseInt(BlockStorage.getBlockInfo(b, "redstone-kontrolo"));
					if (setting >= 0) {
							
							List<String> modes = 
							for (String s : plug.getC().getConfiguration().getConfigurationSection("value.induction-mode").getKeys(false)) {
								
							}
						
							if (setting.equals()) {
								int[] pi = getPowerInterval(s);
								if (pi[0] <= b.getBlockPower() && b.getBlockPower() <= pi[1]) {
									return null;
							}
						}
					}

					Object result = proxy.invokeSuper(obj, args);
					return result;
				}
				return null;
			}
		});
		
		e.createClass();
	}

	private boolean checkArguments(Object[] args) {
		if (args.length == 1 && args[0].getClass().isInterface()) {
			for (Class<?> i : args[0].getClass().getInterfaces()) {
				if (i.getCanonicalName().equals("org.bukkit.block.Block")) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean checkMethod(Class<?> clazz) {
		if (!clazz.getSuperclass().getCanonicalName().equals(plug.getC().getString("cglib.superclass-canonical-name"))) {
			return false;
		}

		for (Method m : clazz.getDeclaredMethods()) {
			for (String regex : plug.getC().getStringList("cglib.method-name-matching-regex")) {
				if (m.getName().matches(regex)) {
					return true;
				}
			}
		}
		return false;
	}

	private int[] getPowerInterval(String modeName) {
		if (plug.getC().contains("value.induction-mode." + modeName)) {
			String[] m = plug.getC().getString("value.induction-mode." + modeName).split(" ");
			return new int[] {Integer.parseInt(m[0]), Integer.parseInt(m[1])};
		}
		return null;
	}
}
