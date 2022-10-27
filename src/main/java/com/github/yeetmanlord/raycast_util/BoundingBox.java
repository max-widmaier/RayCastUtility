package com.github.yeetmanlord.raycast_util;

import org.bukkit.Location;

import java.lang.reflect.Field;

public class BoundingBox {
    private double x1;
    private double y1;
    private double z1;

    private double x2;
    private double y2;
    private double z2;

    public BoundingBox(double x1, double y1, double z1, double x2, double y2, double z2) {
        this.x1 = Math.min(x1, x2);
        this.y1 = Math.min(y1, y2);
        this.z1 = Math.min(z1, z2);
        this.x2 = Math.max(x1, x2);
        this.y2 = Math.max(y1, y2);
        this.z2 = Math.max(z1, z2);
    }

    public BoundingBox(Object nmsObject) throws NoSuchFieldException {
        try {
            this.x1 = (double) getFieldFromNmsObject("a", nmsObject);
            this.y1 = (double) getFieldFromNmsObject("b", nmsObject);
            this.z1 = (double) getFieldFromNmsObject("c", nmsObject);

            this.x2 = (double) getFieldFromNmsObject("d", nmsObject);
            this.y2 = (double) getFieldFromNmsObject("e", nmsObject);
            this.z2 = (double) getFieldFromNmsObject("f", nmsObject);
        } catch (NoSuchFieldException e) {
            this.x1 = (double) getFieldFromNmsObject("minX", nmsObject);
            this.y1 = (double) getFieldFromNmsObject("minY", nmsObject);
            this.z1 = (double) getFieldFromNmsObject("minZ", nmsObject);

            this.x2 = (double) getFieldFromNmsObject("maxX", nmsObject);
            this.y2 = (double) getFieldFromNmsObject("maxY", nmsObject);
            this.z2 = (double) getFieldFromNmsObject("maxZ", nmsObject);
        }
    }

    public BoundingBox(Location loc1, Location loc2) {
        this(loc1.getX(), loc1.getY(), loc1.getZ(), loc2.getX(), loc2.getY(), loc2.getZ());
    }

    @Override
    public String toString() {
        return "BoundingBox{" +
                "x1: " + x1 +
                ", y1: " + y1 +
                ", z1: " + z1 +
                ", x2: " + x2 +
                ", y2: " + y2 +
                ", z2: " + z2 +
                '}';
    }

    public boolean isWithinBoundingBox(double x, double y, double z) {
        return x >= this.x1 && x <= this.x2 && y >= this.y1 && y <= this.y2 && z >= this.z1 && z <= this.z2;
    }

    public boolean isWithinBoundingBox(Location location) {
        return this.isWithinBoundingBox(location.getX(), location.getY(), location.getZ());
    }

    public static Object getFieldFromNmsObject(String fieldName, Object o) throws NoSuchFieldException {

        try {
            Field field;
            try {
                field = o.getClass().getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                field = o.getClass().getField(fieldName);
            }
            field.setAccessible(true);
            Object value = field.get(o);
            field.setAccessible(false);
            return value;
        } catch (NoSuchFieldException exc) {
            throw exc;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }
}
