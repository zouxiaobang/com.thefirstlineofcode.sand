package com.firstlinecode.sand.client.things.obm;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author xb.zou
 * @date 2020/4/25
 * @option the data of obm converted
 */
public class ObmData {
	private Object protocolObject;
	private byte[] binary;

	public ObmData(byte[] binary) {
		this.binary = binary;
	}

	public ObmData(Object protocolObject, byte[] binary) {
		this.protocolObject = protocolObject;
		this.binary = binary;
	}

	@SuppressWarnings("unchecked")
	public <T> T getProtocolObject() {
		return (T) protocolObject;
	}

	public void setProtocolObject(Object protocolObject) {
		this.protocolObject = protocolObject;
	}

	public byte[] getBinary() {
		return binary;
	}

	public void setBinary(byte[] binary) {
		this.binary = binary;
	}

	public String getProtocolObjectInfoString() {
		return getObjectInfoString(protocolObject);
	}

	/**
	 * Return object info string.
	 */
	private String getObjectInfoString(Object obj) {
		if (obj == null) {
			return "Null";
		}

		Class<?> objClass = obj.getClass();
		if (isPrimitiveType(objClass)) {
			return obj.toString();
		}

		Field[] fields = objClass.getDeclaredFields();
		String className = objClass.getSimpleName();
		StringBuilder sb = new StringBuilder(className);
		try {
			sb.append("[");
			for (Field field : fields) {
				boolean oldAccessible = field.isAccessible();

				try {
					field.setAccessible(true);

					Object fieldValue = field.get(protocolObject);
					if (fieldValue == null) {
						continue;
					}

					String fieldName = field.getName();
					Class<?> fieldType = field.getType();
					if (fieldType == List.class) {
						sb.append(fieldName).append("=[");

						List<?> list = (List<?>) fieldValue;
						for (Object aObj : list) {
							sb.append(getObjectInfoString(aObj)).append(",");
						}

						if (list.size() > 0 && sb.charAt(sb.length() - 1) == ',') {
							sb.deleteCharAt(sb.length() - 1);
						}

						sb.append("]").append(",");
					} else if (fieldType == Map.class) {
						sb.append(fieldName).append("=[");

						Map<?, ?> map = (Map<?, ?>) fieldValue;
						for (Map.Entry<?, ?> entry : map.entrySet()) {
							sb.append(entry.getKey()).append("=").append(getObjectInfoString(entry.getValue()))
									.append(",");
						}
						
						if (!map.isEmpty() && sb.charAt(sb.length() - 1) == ',') {
							sb.deleteCharAt(sb.length() - 1);
						}
						
						sb.append("]").append(",");
					} else if (fieldType == Set.class) {
						sb.append(fieldName).append("=[");
						
						Set<?> set = (Set<?>) fieldValue;
						for (Object fieldObj : set) {
							sb.append(getObjectInfoString(fieldObj)).append(",");
						}
						
						if (!set.isEmpty() && sb.charAt(sb.length() - 1) == ',') {
							sb.deleteCharAt(sb.length() - 1);
						}
						
						sb.append("]").append(",");
					} else {
						sb.append(fieldName).append("='").append(getObjectInfoString(fieldValue)).append("',");
					}
				} finally {
					field.setAccessible(oldAccessible);
				}
			}
			
			if (sb.charAt(sb.length() - 1) == ',') {
				sb.deleteCharAt(sb.length() - 1);
			}
			
			sb.append(']');
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		return sb.toString();
	}
	
	private boolean isPrimitiveType(Class<?> fieldType) {
		return fieldType.equals(String.class) ||
				fieldType.equals(boolean.class) ||
				fieldType.equals(Boolean.class) ||
				fieldType.equals(int.class) ||
				fieldType.equals(Integer.class) ||
				fieldType.equals(long.class) ||
				fieldType.equals(Long.class) ||
				fieldType.equals(float.class) ||
				fieldType.equals(Float.class) ||
				fieldType.equals(double.class) ||
				fieldType.equals(Double.class) ||
				fieldType.equals(BigInteger.class) ||
				fieldType.equals(BigDecimal.class) ||
				fieldType.isEnum();
	}
}
