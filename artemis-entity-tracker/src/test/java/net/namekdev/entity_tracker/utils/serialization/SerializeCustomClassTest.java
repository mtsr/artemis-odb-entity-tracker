package net.namekdev.entity_tracker.utils.serialization;

import static org.junit.Assert.*;

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
	public void inspect_vector3() {
		ObjectModelNode model = inspector1.inspect(Vector3.class);

		assertTrue(model.children != null && model.children.size() == 3);
		assertEquals("x", model.children.get(0).name);
		assertEquals("y", model.children.get(1).name);
		assertEquals("z", model.children.get(2).name);
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


	public static class Vector3 {
		public float x, y, z;

		public Vector3(float x, float y, float z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}
	}
}
