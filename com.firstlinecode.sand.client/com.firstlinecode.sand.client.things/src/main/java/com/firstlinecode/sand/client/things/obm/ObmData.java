package com.firstlinecode.sand.client.things.obm;

import java.lang.reflect.Field;
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

    public Object getProtocolObject() {
        return protocolObject;
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

    /**
     * return the string of protocol object info
     */
    public String getProtocolObjectInfoString() {
        if (protocolObject == null) {
            return "";
        }

        Class<?> objClass = protocolObject.getClass();
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

                        List fieldList = (List) fieldValue;
                        for (Object fieldObj : fieldList) {
                            sb.append(fieldObj).append(",");
                        }

                        if (fieldList.size() > 0 && sb.charAt(sb.length() - 1) == ',') {
                            sb.deleteCharAt(sb.length() - 1);
                        }

                        sb.append("]").append(",");
                    } else if (fieldType == Map.class) {
                        sb.append(fieldName).append("=[");

                        Map fieldMap = (Map) fieldValue;
                        Set<Map.Entry> entrySet = fieldMap.entrySet();
                        for (Map.Entry entry : entrySet) {
                            sb.append(entry.getKey()).append("=").append(entry.getValue()).append(",");
                        }

                        if (entrySet.size() > 0 && sb.charAt(sb.length() - 1) == ',') {
                            sb.deleteCharAt(sb.length() - 1);
                        }

                        sb.append("]").append(",");
                    } else if (fieldType == Set.class) {
                        sb.append(fieldName).append("=[");

                        Set fieldSet = (Set) fieldValue;
                        for (Object fieldObj : fieldSet) {
                            sb.append(fieldObj).append(",");
                        }

                        if (fieldSet.size() > 0 && sb.charAt(sb.length() - 1) == ',') {
                            sb.deleteCharAt(sb.length() - 1);
                        }

                        sb.append("]").append(",");
                    } else {
                        sb.append(fieldName).append("='").append(fieldValue).append("',");
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
}
