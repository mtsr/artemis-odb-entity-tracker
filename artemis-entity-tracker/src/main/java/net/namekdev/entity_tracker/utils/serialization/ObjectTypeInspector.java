package net.namekdev.entity_tracker.utils.serialization;

import static net.namekdev.entity_tracker.utils.serialization.NetworkSerialization.*;

import java.util.Vector;

import com.artemis.utils.reflect.ClassReflection;
import com.artemis.utils.reflect.Field;

public abstract class ObjectTypeInspector {
	/**
	 * Returns tree description of type.
	 *
	 * @return return {@code null} to ignore type (it won't be de/serialized)
	 */
	public abstract ObjectModelNode inspect(Class<?> type);


	protected ObjectModelNode inspectOneLevel(Class<?> type) {
		Field[] fields = ClassReflection.getDeclaredFields(type);

		ObjectModelNode root = new ObjectModelNode();
		root.networkType = TYPE_TREE_DESCR_CHILDREN;
		root.children = new Vector<>(fields.length);

		for (Field field : fields) {
			Class<?> fieldType = field.getType();
			ObjectModelNode child = new ObjectModelNode();

			child.name = field.getName();
			child.isArray = fieldType.isArray();

			if (child.isArray) {
				child.networkType = TYPE_ARRAY;
				child.arrayType = NetworkSerialization.determineSimpleType(fieldType);
			}
			else {
				child.networkType = NetworkSerialization.determineSimpleType(fieldType);
			}

			root.children.addElement(child);
		}

		return root;
	}

	protected ObjectModelNode inspectMultiLevel(Class<?> type) {
		// TODO if child.networkType == TYPE_UNKNOWN -> TYPE_TREE -> inspect by recurrency

		throw new UnsupportedOperationException("not yet implemented");
	}



	public static class OneLevel extends ObjectTypeInspector {
		@Override
		public ObjectModelNode inspect(Class<?> type) {
			return inspectOneLevel(type);
		}
	}

	public static class MultiLevel extends ObjectTypeInspector {
		// TODO add max depth

		@Override
		public ObjectModelNode inspect(Class<?> type) {
			return inspectMultiLevel(type);
		}
	}
}
