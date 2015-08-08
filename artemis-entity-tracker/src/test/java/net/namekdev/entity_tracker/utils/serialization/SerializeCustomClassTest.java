package net.namekdev.entity_tracker.utils.serialization;

import static net.namekdev.entity_tracker.utils.serialization.NetworkSerialization.*;
import static org.junit.Assert.*;
import net.namekdev.entity_tracker.utils.sample.GameObject;
import net.namekdev.entity_tracker.utils.sample.GameState;
import net.namekdev.entity_tracker.utils.sample.Vector2;
import net.namekdev.entity_tracker.utils.sample.Vector3;

import org.junit.Before;
import org.junit.Test;

public class SerializeCustomClassTest {
	NetworkDeserializer deserializer;
	ObjectTypeInspector inspector1;
	ObjectTypeInspector inspectorMulti;


	@Before
	public void setup() {
		deserializer = new NetworkDeserializer();
		inspector1 = new ObjectTypeInspector.OneLevel();
		inspectorMulti = new ObjectTypeInspector.MultiLevel();
	}

	@Test
	public void inspect_vectors() {
		ObjectModelNode model = inspector1.inspect(Vector3.class);
		assertTrue(model.children != null && model.children.size() == 3);
		assertEquals("x", model.children.get(0).name);
		assertEquals("y", model.children.get(1).name);
		assertEquals("z", model.children.get(2).name);

		model = inspector1.inspect(Vector2.class);
		assertTrue(model.children != null && model.children.size() == 2);
		assertEquals("x", model.children.get(0).name);
		assertEquals("y", model.children.get(1).name);
	}

	@Test
	public void inspect_gamestate_multi_level() {
		GameState gameState = new GameState();
		gameState.objects = new GameObject[] {
			new GameObject(), new GameObject()
		};

		ObjectModelNode model = inspectorMulti.inspect(GameState.class);

		// GameState
		assertEquals(TYPE_TREE_DESCR_CHILDREN, model.networkType);
		assertNotNull(model.children);
		assertFalse(model.isArray);
		assertEquals(1, model.children.size());

		// GameState.objects (GameObject[])
		ObjectModelNode objects = model.children.elementAt(0);
		assertEquals("objects", objects.name);
		assertEquals(TYPE_ARRAY, objects.networkType);
		assertTrue(objects.isArray);
		assertNotNull(objects.children);
		assertEquals(TYPE_TREE, objects.arrayType);


		// GameState.objects[0] (GameObject)
		ObjectModelNode go1 = objects.children.elementAt(0);
		assertNull(go1.name);
		assertEquals(TYPE_TREE, go1.networkType);
		assertFalse(go1.isArray);
		assertNotNull(go1.children);

		// GameState.objects[0].pos (Vector3)
		ObjectModelNode pos1 = go1.children.elementAt(0);
		assertVector3(pos1, "pos");

		// GameState.objects[0].size (Vector2)
		ObjectModelNode size1 = go1.children.elementAt(0);
		assertVector2(size1, "size");


		// GameState.objects[1] (GameObject)
		ObjectModelNode go2 = objects.children.elementAt(0);
		assertNull(go2.name);
		assertEquals(TYPE_TREE, go2.networkType);
		assertFalse(go2.isArray);
		assertNotNull(go2.children);

		// GameState.objects[1].pos (Vector3)
		ObjectModelNode pos2 = go2.children.elementAt(0);
		assertVector3(pos2, "pos");

		// GameState.objects[1].size (Vector2)
		ObjectModelNode size2 = go2.children.elementAt(0);
		assertVector2(size2, "size");
	}

	private void assertVector3(ObjectModelNode node, String name) {
		assertEquals(name, node.name);
		assertEquals(TYPE_TREE, node.networkType);
		assertFalse(node.isArray);
		assertNotNull(node.children);

		// GameState.objects[0].pos -> x, y, z (floats)
		assertFloat(node.children.elementAt(0));
		assertFloat(node.children.elementAt(1));
		assertFloat(node.children.elementAt(2));
	}

	private void assertVector2(ObjectModelNode node, String name) {
		assertEquals(name, node.name);
		assertEquals(TYPE_TREE, node.networkType);
		assertFalse(node.isArray);
		assertNotNull(node.children);

		// Vector2 -> x, y (floats)
		assertFloat(node.children.elementAt(0));
		assertFloat(node.children.elementAt(1));
	}

	private void assertFloat(ObjectModelNode node) {
		assertEquals(TYPE_FLOAT, node.networkType);
		assertFalse(node.isArray);
		assertNull(node.children);
	}

	@Test
	public void deserialize_vector3_one_level() {
		testVector3(inspector1);
	}

	@Test
	public void deserialize_vector3_multi_level() {
//		testVector3(inspectorMulti);
	}

	private void testVector3(ObjectTypeInspector inspector) {
		NetworkSerializer serializer = new NetworkSerializer().reset();

		Vector3 vector = new Vector3(4, 5, 6);
		ObjectModelNode model = inspector.inspect(vector.getClass());
		int id = 198;

		serializer.addObjectDescription(model, id);
		serializer.addObject(model, vector);

		byte[] buffer = serializer.getResult().buffer;
		deserializer.setSource(buffer, 0, serializer.getResult().size);

		ObjectModelNode model2 = deserializer.readObjectDescription();
		model.rootId = id;
		assertEquals(model, model2);

		ValueTree result = deserializer.readObject(model2);

		assertEquals(3, result.values.length);
		assertEquals(vector.x, result.values[0]);
		assertEquals(vector.y, result.values[1]);
		assertEquals(vector.z, result.values[2]);
	}

	@Test
	public void deserialize_arrays_one_level() {
		Float[] floats = new Float[] { 0f, 1f, 2f };
		String[] strings = new String[] { "asd", "omg", "this is a test?" };

		testArray(floats, inspector1);
		testArray(strings, inspector1);
	}

	@Test
	public void deserialize_arrays_multi_level() {
		// TODO testArray(..., ...)
	}

	private void testArray(Object[] arr, ObjectTypeInspector inspector) {
		// TODO
	}

	@Test
	public void deserialize_gamestate_multi_level() {
		GameState gameState = new GameState();
		gameState.objects = new GameObject[] {
			new GameObject(), new GameObject(),
			new GameObject(), new GameObject()
		};

		NetworkSerializer serializer = new NetworkSerializer().reset();
		ObjectModelNode model = inspectorMulti.inspect(GameState.class);
		int id = 1734552;


		serializer.addObjectDescription(model, id);
//		serializer.addObject(model, gameState);

		byte[] buffer = serializer.getResult().buffer;
		deserializer.setSource(buffer, 0, serializer.getResult().size);

		ObjectModelNode model2 = deserializer.readObjectDescription();
		model.rootId = id;
		assertEquals(model, model2);

//		ValueTree result = deserializer.readObject(model2);
//		assertEquals(gameState, result);
	}

}
