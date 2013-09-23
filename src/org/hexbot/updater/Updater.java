package org.hexbot.updater;

import org.hexbot.updater.transform.*;
import org.hexbot.updater.transform.Character;
import org.hexbot.updater.transform.parent.Container;
import org.hexbot.updater.transform.parent.Hook;
import org.hexbot.updater.transform.parent.Transform;
import org.objectweb.asm.tree.ClassNode;

import java.util.HashMap;
import java.util.Map;

public class Updater implements Runnable {

	public final Map<String, ClassNode> classnodes;

	public final Transform[] transforms = {new Node(this), new CacheableNode(this), new NodeDeque(this),
			new CacheableNodeDeque(this), new NodeHashTable(this), new NodeCache(this), new Friend(this),
			new Widget(this), new ObjectDefinition(this), new CollisionMap(this), new Renderable(this),
			new InteractableObject(this), new Model(this), new Projectile(this), new Boundary(this),
			new FloorDecoration(this), new WallDecoration(this), new ItemLayer(this), new TileData(this),
			new ItemDefinition(this), new Item(this), new Character(this), new PlayerDefinition(this),
			new Player(this), new NpcDefinition(this), new Npc(this), new Region(this), new Client(this)};

	public Updater() {
		long start = System.currentTimeMillis();
		classnodes = ClassNode.loadAll("deob.jar");
		long end = System.currentTimeMillis();
		System.out.println("Loaded ClassNodes in " + (end - start) + "ms");
	}

	@Override
	public void run() {
		long start = System.currentTimeMillis();
		int totalClasses = 0;
		int classes = 0;
		int totalFields = 0;
		int fields = 0;
		Map<String, Container> containers = new HashMap<>();
		for (Transform transform : transforms) {
			totalClasses++;
			Container container = (Container) transform;
			ClassNode cn = transform.validate(classnodes);
			if (cn != null) {
				container.cn = cn;
				containers.put(container.getClass().getSimpleName(), container);
				classes++;
			}
		}
		long end = System.currentTimeMillis();
		System.out.println("Identified " + classes + "/" + totalClasses + " classes in " + (end - start) + "ms");
		start = System.currentTimeMillis();
		for (Container container : containers.values()) {
			if (container.getTotalHookCount() == 0) continue;
			container.transform(container.cn);
			totalFields += container.getTotalHookCount();
			fields += container.getHookSuccessCount();
		}
		end = System.currentTimeMillis();
		System.out.println("Identified " + fields + "/" + totalFields + " fields in " + (end - start) + "ms");
		System.out.println();
		for (Map.Entry<String, Container> entry : containers.entrySet()) {
			String key = entry.getKey();
			ClassNode node = entry.getValue().cn;
			System.out.println(String.format(" > %s identified as '%s'", key, node == null ? "null" : node.name));
			for (Hook hook : entry.getValue().hooks) {
				String s = String.format("%s.%s() %s returns %s.%s", hook.toInject, hook.name, hook.desc, hook.clazz, hook.field);
				if (hook.multiplier != -1)
					s += " * " + hook.multiplier;
				System.out.println(" ^ " + s);
			}
			System.out.println();
		}
	}

	public static void main(String[] args) throws Exception {
		new Updater().run();
	}
}
