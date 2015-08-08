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
		/*ield[] fields = ClassReflection.getFields(type);

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

		return root;*/

		return inspectLevels(type, 0);
	}

	protected ObjectModelNode inspectMultiLevel(Class<?> type) {
		return inspectLevels(type, Integer.MAX_VALUE);
	}

	protected ObjectModelNode inspectLevels(Class<?> type, int leftLevels) {
		Field[] fields = ClassReflection.getFields(type);

		ObjectModelNode root = new ObjectModelNode();
		root.networkType = TYPE_TREE_DESCR_CHILDREN;
		root.children = new Vector<>(fields.length);

		for (Field field : fields) {
			Class<?> fieldType = field.getType();
			ObjectModelNode child = new ObjectModelNode();

			if (fieldType.isArray()) {
				child.arrayType = NetworkSerialization.determineSimpleType(fieldType);

				if (child.arrayType == TYPE_UNKNOWN && leftLevels > 0) {
					Class<?> childType = fieldType.getComponentType();
					child = inspectLevels(childType, leftLevels - 1);

					byte childNetworkType = NetworkSerialization.determineSimpleType(fieldType);
					if (childNetworkType == TYPE_UNKNOWN) {
						childNetworkType = TYPE_TREE;
					}

					child.arrayType = childNetworkType;
				}

				child.networkType = TYPE_ARRAY;
				child.isArray = true;
			}
			else {
				child.networkType = NetworkSerialization.determineSimpleType(fieldType);

				if (child.networkType == TYPE_UNKNOWN && leftLevels > 0) {
					child = inspectLevels(fieldType, leftLevels - 1);
					child.networkType = TYPE_TREE;
				}
			}

			child.name = field.getName();

			root.children.addElement(child);
		}

		return root;
	}



	public static class OneLevel extends ObjectTypeInspector {
		@Override
		public ObjectModelNode inspect(Class<?> type) {
			return inspectOneLevel(type);
		}
	}

	public static class MultiLevel extends ObjectTypeInspector {
		@Override
		public ObjectModelNode inspect(Class<?> type) {
			return inspectMultiLevel(type);
		}
	}
}
