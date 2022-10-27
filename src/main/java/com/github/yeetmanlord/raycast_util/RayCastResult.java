package com.github.yeetmanlord.raycast_util;

public class RayCastResult {

    private ResultType type;

    private Object object;

    public RayCastResult(ResultType type, Object object) {
        this.type = type;
        this.object = object;
    }

    public Object get() {
        return object;
    }

    public ResultType getType() {
        return type;
    }

    public boolean isEmpty() {
        return type == ResultType.EMPTY;
    }

    @Override
    public String toString() {
        return "RayTraceResult{" +
                "type: " + type +
                ", object: " + object +
                '}';
    }
}
