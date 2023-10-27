package com.github.yeetmanlord.raycast_util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Version independent ray-cast. Will ray-cast from an entities eye location with its pitch and yaw.
 * Will stop on hitting either an entity or a block. If it hits neither it will return an
 * {@link ResultType#EMPTY} with a null entity and block.
 * <br>
 * All tests were run using a 1.16.5 server with 2GB of RAM using the highest preciseness. For {@link #rayCastBlocks(LivingEntity, double, boolean, Precision)} I used
 * {@link Precision#PRECISE_BLOCK}, for {@link #rayCastEntities(LivingEntity, double, boolean, Precision)} I used {@link Precision#PRECISE_ENTITY} and for {@link #rayCast(LivingEntity, double, boolean, Precision)}
 * I used {@link Precision#PRECISE_ENTITY}
 * <br>
 * <b>TEST RESULTS:</b>
 * <br>
 * <b>NOTE:</b> Entity results will very, based on how many entities are loaded.
 * <ul>
 *     Block Ray-Cast: 80.07 ms, Precision: {@link Precision#PRECISE_BLOCK}
 *     <ul>
 *         Successfully completed 1000 ray-casts <br>
 *         TPS: No noticeable TPS change.
 *     </ul> <br>
 *     Entity Ray-Cast: 395.43 ms, Precision: {@link Precision#PRECISE_ENTITY}
 *     <ul>
 *         Successfully completed 1000 ray-casts <br>
 *          TPS: No noticeable TPS change.
 *     </ul> <br>
 *     Full Ray-Cast (Block and entity): 437.01 ms, Precision: {@link Precision#PRECISE_ENTITY}
 *     <ul>
 *            Successfully completed 1000 ray-casts <br>
 *            TPS: No noticeable TPS change.
 *     </ul> <br>
 * <p>
 *     Block Ray-Cast: 61.64 ms, Precision: {@link Precision#ACCURATE_BLOCK}
 *     <ul>
 *         Successfully completed 1000 ray-casts <br>
 *         TPS: No noticeable TPS change.
 *     </ul> <br>
 *     Entity Ray-Cast: 225.14 ms, Precision: {@link Precision#ACCURATE_ENTITY}
 *     <ul>
 *         Successfully completed 1000 ray-casts <br>
 *          TPS: No noticeable TPS change.
 *     </ul> <br>
 *     Full Ray-Cast (Block and entity): 253.52 ms, Precision: {@link Precision#ACCURATE_ENTITY}
 *     <ul>
 *            Successfully completed 1000 ray-casts <br>
 *            TPS: No noticeable TPS change.
 *     </ul> <br>
 * <p>
 *     Block Ray-Cast: 71.90 ms (Lol, wow), Precision: {@link Precision#SEMI_ACCURATE_BLOCK}
 *     <ul>
 *         Successfully completed 1000 ray-casts <br>
 *         TPS: No noticeable TPS change.
 *     </ul> <br>
 *     Entity Ray-Cast: 97.18 ms (Wow that's a lot of improvement), Precision: {@link Precision#SEMI_ACCURATE_ENTITY}
 *     <ul>
 *         Successfully completed 1000 ray-casts <br>
 *          TPS: No noticeable TPS change.
 *     </ul> <br>
 *     Full Ray-Cast (Block and entity): 177.97 ms, Precision: {@link Precision#SEMI_ACCURATE_ENTITY}
 *     <ul>
 *            Successfully completed 1000 ray-casts <br>
 *            TPS: No noticeable TPS change.
 *     </ul> <br>
 * <p>
 *     Block Ray-Cast: 61.22 ms, Precision: {@link Precision#IMPRECISE_BLOCK}
 *     <ul>
 *         Successfully completed 1000 ray-casts <br>
 *         TPS: No noticeable TPS change.
 *     </ul> <br>
 *     Entity Ray-Cast: 71.96 ms, Precision: {@link Precision#IMPRECISE_ENTITY}
 *     <ul>
 *         Successfully completed 1000 ray-casts <br>
 *          TPS: No noticeable TPS change.
 *     </ul> <br>
 *     Full Ray-Cast (Block and entity): 120.24 ms, Precision: {@link Precision#IMPRECISE_ENTITY}
 *     <ul>
 *            Successfully completed 1000 ray-casts <br>
 *            TPS: No noticeable TPS change.
 *     </ul> <br>
 *
 * </ul>
 * <b>VERDICT:</b> You should probably use {@link Precision#ACCURATE_BLOCK} and I think you should probably use {@link Precision#PRECISE_ENTITY} or {@link Precision#ACCURATE_ENTITY}. Despite those
 * being resource hogs they are the only accurate way. (Sadly)
 */
public class RayCastUtility {

    /**
     * Ray-casts only blocks. Doesn't affect performance very much. Using a 1.16.5 server and calling this 1000 times, there was minimal tps issue. (Using the highest preciseness)
     *
     * @param starting      Location to ray-cast from
     * @param maxDistance   Maximum distance to ray-cast
     * @param ignoreLiquids Whether to factor in liquids. If true, will not stop ray-casting at a liquid.
     * @param precision     How many blocks (or fractions of) to advance before every next check
     * @return A ray-casted block result or an empty block result
     */
    public static BlockRayCastResult rayCastBlocks(Location starting, double maxDistance, boolean ignoreLiquids, Precision precision) {
        Vector direction = starting.getDirection();
        Location check = starting.clone();
        Location last = starting.clone();

        double distanceTraveled = 0;
        while (distanceTraveled < maxDistance) {
            last = check.clone();
            check = getRayTraceLocation(check, direction, precision.getAdvance());
            if (check.getBlock().getType() != Material.AIR && !(check.getBlock().isLiquid() && ignoreLiquids)) {
                break;
            }
            distanceTraveled += precision.getAdvance();
        }

        Block block = check.getBlock();
        BlockFace face = block.getFace(last.getBlock());


        if (block.getType() == Material.AIR || (ignoreLiquids && block.isLiquid())) {
            return new BlockRayCastResult(ResultType.EMPTY, block, face);
        }
        return new BlockRayCastResult(ResultType.BLOCK, block, face);
    }

    @Deprecated
    public static BlockRayCastResult rayCastBlocks(Entity entity, double maxDistance, boolean ignoreLiquids, Precision precision) {
        return rayCastBlocks(enity.getEyeLocation(), maxDistance, ignoreLiquids, precision);
    }

    /**
     * Ray-casts only entities. Affect performance quite a bit. Using a 1.16.5 server and calling this 1000 time, it took quite a bit of time. (Using the highest preciseness)
     *
     * @param starting      Location to ray-cast from
     * @param maxDistance   Maximum distance to ray-cast
     * @param ignoreLiquids Whether to factor in liquids. If true, will not stop ray-casting at a liquid.
     * @param precision     How many blocks (or fractions of) to advance before every next check
     * @return A ray-casted entity result or an empty entity result
     */
    public static EntityRayCastResult rayCastEntities(Location starting, double maxDistance, boolean ignoreLiquids, Precision precision) {
        Vector direction = starting.getDirection();
        Location check = starting.clone();
        List<Entity> entityList = new ArrayList<>(entity.getNearbyEntities(maxDistance + 0.5, maxDistance + 0.5, maxDistance + 0.5)).stream().filter(e -> e != entity).collect(Collectors.toList());
        Entity hitResult = null;
        double distanceTraveled = 0;
        while (distanceTraveled < maxDistance) {
            check = getRayTraceLocation(check, direction, precision.getAdvance());
            if (check.getBlock().getType() != Material.AIR && !(check.getBlock().isLiquid() && ignoreLiquids)) {
                break;
            }
            List<Entity> results = new ArrayList<>();
            for (Entity e : entityList) {
                try {
                    Object nmsEntity = e.getClass().getMethod("getHandle").invoke(e);
                    Object entityBoundingBox = nmsEntity.getClass().getMethod("getBoundingBox").invoke(nmsEntity);
                    BoundingBox entBB = new BoundingBox(entityBoundingBox);
                    if (entBB.isWithinBoundingBox(check)) {
                        results.add(e);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            if (results.size() > 0) {
                Entity closest = results.get(0);
                for (int i = 1; i < results.size(); i++) {
                    if (results.get(i).getLocation().distanceSquared(check) < closest.getLocation().distanceSquared(check)) {
                        Entity e = results.get(i);
                        if (e != null) {
                            closest = results.get(i);
                        }
                    }
                }
                hitResult = closest;
                break;
            }
            distanceTraveled += precision.getAdvance();
        }

        if (hitResult == null) {
            return new EntityRayCastResult(ResultType.EMPTY, null);
        }
        return new EntityRayCastResult(ResultType.ENTITY, hitResult);
    }

    @Deprecated
    public static EntityRayCastResult rayCastEntities(Entity entity, double maxDistance, boolean ignoreLiquids, Precision precision) {
        return rayCastEntities(entity.getEyeLocation(), maxDistance, ignoreLiquids, precision);
    }    

    /**
     * Ray-casts entities and blocks. Affect performance quite a bit. Using a 1.16.5 server and calling this 1000 times, it took quite a bit of time. (Using the highest preciseness)
     *
     * @param starting      Location to ray-cast from
     * @param maxDistance   Maximum distance to ray-cast
     * @param ignoreLiquids Whether to factor in liquids. If true, will not stop ray-casting at a liquid.
     * @param precision     How many blocks (or fractions of) to advance before every next check
     * @return A ray-cast result or an empty result
     */
    public static RayCastResult rayCast(Location starting, double maxDistance, boolean ignoreLiquids, Precision precision) {
        Vector direction = starting.getDirection();
        Location check = starting.clone();
        Location last = starting.clone();
        List<Entity> entityList = new ArrayList<>(entity.getNearbyEntities(maxDistance + 0.5, maxDistance + 0.5, maxDistance + 0.5)).stream().filter(e -> e != entity).collect(Collectors.toList());
        Entity hitResult = null;
        double distanceTraveled = 0;
        Block blockResult = null;
        while (distanceTraveled < maxDistance) {
            last = check.clone();
            check = getRayTraceLocation(check, direction, precision.getAdvance());
            if (check.getBlock().getType() != Material.AIR && !(check.getBlock().isLiquid() && ignoreLiquids)) {
                blockResult = check.getBlock();
                break;
            }
            List<Entity> results = new ArrayList<>();
            for (Entity e : entityList) {
                try {
                    BoundingBox entBB = new BoundingBox(e);
                    if (entBB.isWithinBoundingBox(check)) {
                        results.add(e);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            if (results.size() > 0) {
                Entity closest = results.get(0);
                for (int i = 1; i < results.size(); i++) {
                    if (results.get(i).getLocation().distanceSquared(check) < closest.getLocation().distanceSquared(check)) {
                        Entity e = results.get(i);
                        if (e != null) {
                            closest = results.get(i);
                        }
                    }
                }
                hitResult = closest;
                break;
            }
            distanceTraveled += precision.getAdvance();
        }

        if (hitResult == null) {
            if (blockResult == null) {
                return new RayCastResult(ResultType.EMPTY, null);
            } else {
                return new BlockRayCastResult(ResultType.BLOCK, blockResult, blockResult.getFace(last.getBlock()));
            }
        }
        return new EntityRayCastResult(ResultType.ENTITY, hitResult);
    }

    @Deprecated
    public static RayCastResult rayCast(Entity entity, double maxDistance, boolean ignoreLiquids, Precision precision) {
        return rayCast(entity.getEyeLocation(), maxDistance, ignoreLiquids, precision);
    }

    /**
     * Ray-casts from entities eye location and executes specified code at each step.
     *
     * @param starting      Location to ray-cast from
     * @param maxDistance     Maximum distance to ray-cast
     * @param ignoreLiquids   Whether to factor in liquids. If true, will not stop ray-casting at a liquid.
     * @param stepSize        How many blocks to advance forward before next check. If you specified 0.5D, it will check every half block.
     * @param ignoreEntities  Whether to stop the ray-casting once it hits an entity
     * @param onStep          Code to execute at each step
     * @param onRayCastFinish Code to execute when ray-casting is finished
     */
    public static void executeStepByStep(Location starting, double maxDistance, boolean ignoreLiquids, double stepSize, boolean ignoreEntities, Consumer<Location> onStep, @Nullable Consumer<RayCastResult> onRayCastFinish) {
        Vector direction = starting.getDirection();
        Location check = starting.clone();
        Location last;
        double distanceTraveled = 0;
        if (ignoreEntities) {
            while (distanceTraveled < maxDistance) {
                last = check.clone();
                check = getRayTraceLocation(check, direction, stepSize);
                onStep.accept(check);
                if (check.getBlock().getType() != Material.AIR && !(check.getBlock().isLiquid() && ignoreLiquids)) {
                    if (onRayCastFinish != null) {
                        onRayCastFinish.accept(new BlockRayCastResult(ResultType.BLOCK, check.getBlock(), check.getBlock().getFace(last.getBlock())));
                    }
                    break;
                }
                distanceTraveled += stepSize;
            }
        } else {
            List<Entity> entityList = new ArrayList<>(entity.getNearbyEntities(maxDistance + 0.5, maxDistance + 0.5, maxDistance + 0.5)).stream().filter(e -> e != entity).collect(Collectors.toList());
            while (distanceTraveled < maxDistance) {
                last = check.clone();
                check = getRayTraceLocation(check, direction, stepSize);
                onStep.accept(check);
                if (check.getBlock().getType() != Material.AIR && !(check.getBlock().isLiquid() && ignoreLiquids)) {
                    if (onRayCastFinish != null) {
                        onRayCastFinish.accept(new BlockRayCastResult(ResultType.BLOCK, check.getBlock(), check.getBlock().getFace(last.getBlock())));
                    }
                    break;
                }
                List<Entity> results = new ArrayList<>();
                for (Entity e : entityList) {
                    try {
                        Object nmsEntity = e.getClass().getMethod("getHandle").invoke(e);
                        Object entityBoundingBox = nmsEntity.getClass().getMethod("getBoundingBox").invoke(nmsEntity);
                        BoundingBox entBB = new BoundingBox(entityBoundingBox);
                        if (entBB.isWithinBoundingBox(check)) {
                            results.add(e);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                if (results.size() > 0) {
                    if (onRayCastFinish != null) {
                        Entity closest = results.get(0);
                        for (int i = 1; i < results.size(); i++) {
                            if (results.get(i).getLocation().distanceSquared(check) < closest.getLocation().distanceSquared(check)) {
                                closest = results.get(i);
                            }
                        }
                        onRayCastFinish.accept(new EntityRayCastResult(ResultType.ENTITY, closest));
                    }
                    break;
                }
                distanceTraveled += stepSize;
            }
        }
    }

    @Deprecated
    public static void executeStepByStep(Entity entity, double maxDistance, boolean ignoreLiquids, double stepSize, boolean ignoreEntities, Consumer<Location> onStep, @Nullable Consumer<RayCastResult> onRayCastFinish) {
        return executeStepByStep(entity.getEyeLocation(), maxDistance, ignoreLiquids, stepSize, ignoreEntities, onStep, onRayCastFinish);
    }

    /**
     * Ray-casts from entities eye location and executes specified code at each step. The stepSize and precision are different. stepSize determines when code will run while
     * precision determines how often a result is checked for (your ray-cast hit something)
     *
     * @param starting      Location to ray-cast from
     * @param maxDistance     Maximum distance to ray-cast
     * @param ignoreLiquids   Whether to factor in liquids. If true, will not stop ray-casting at a liquid.
     * @param stepSize        How many blocks that have to pass before the next onStep is called. If you specified 0.5D, it will run the onStep every half block.
     * @param ignoreEntities  Whether to stop the ray-casting once it hits an entity
     * @param precision       How many blocks (or fractions of) to advance before every next check
     * @param onStep          Code to execute at each step
     * @param onRayCastFinish Code to execute when ray-casting is finished
     */
    public static void executeStepByStepWithPrecision(Location starting, double maxDistance, boolean ignoreLiquids, double stepSize, boolean ignoreEntities, Precision precision, Consumer<Location> onStep, @Nullable Consumer<RayCastResult> onRayCastFinish) {
        Vector direction = starting.getDirection();
        Location check = starting.clone();
        Location last;
        double distanceTraveled = 0;
        double distSinceLastStep = 0D;
        if (ignoreEntities) {
            while (distanceTraveled < maxDistance) {
                last = check.clone();
                check = getRayTraceLocation(check, direction, precision.advance);
                if (distSinceLastStep >= stepSize) {
                    onStep.accept(check);
                    distSinceLastStep = 0D;
                }
                if (check.getBlock().getType() != Material.AIR && !(check.getBlock().isLiquid() && ignoreLiquids)) {
                    if (onRayCastFinish != null) {
                        onRayCastFinish.accept(new BlockRayCastResult(ResultType.BLOCK, check.getBlock(), check.getBlock().getFace(last.getBlock())));
                    }
                    break;
                }
                distanceTraveled += precision.advance;
                distSinceLastStep += precision.advance;
            }
        } else {
            List<Entity> entityList = new ArrayList<>(entity.getNearbyEntities(maxDistance + 0.5, maxDistance + 0.5, maxDistance + 0.5)).stream().filter(e -> e != entity).collect(Collectors.toList());
            while (distanceTraveled < maxDistance) {
                last = check.clone();
                check = getRayTraceLocation(check, direction, precision.advance);
                if (distSinceLastStep >= stepSize) {
                    onStep.accept(check);
                    distSinceLastStep = 0D;
                }
                if (check.getBlock().getType() != Material.AIR && !(check.getBlock().isLiquid() && ignoreLiquids)) {
                    if (onRayCastFinish != null) {
                        onRayCastFinish.accept(new BlockRayCastResult(ResultType.BLOCK, check.getBlock(), check.getBlock().getFace(last.getBlock())));
                    }
                    break;
                }
                List<Entity> results = new ArrayList<>();
                for (Entity e : entityList) {
                    try {
                        Object nmsEntity = e.getClass().getMethod("getHandle").invoke(e);
                        Object entityBoundingBox = nmsEntity.getClass().getMethod("getBoundingBox").invoke(nmsEntity);
                        BoundingBox entBB = new BoundingBox(entityBoundingBox);
                        if (entBB.isWithinBoundingBox(check)) {
                            results.add(e);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                if (results.size() > 0) {
                    if (onRayCastFinish != null) {
                        Entity closest = results.get(0);
                        for (int i = 1; i < results.size(); i++) {
                            if (results.get(i).getLocation().distanceSquared(check) < closest.getLocation().distanceSquared(check)) {
                                closest = results.get(i);
                            }
                        }
                        onRayCastFinish.accept(new EntityRayCastResult(ResultType.ENTITY, closest));
                    }
                    break;
                }
                distanceTraveled += precision.advance;
                distSinceLastStep += precision.advance;
            }
        }
    }

    @Deprecated
    public static void executeStepByStepWithPrecision(Entity entity, double maxDistance, boolean ignoreLiquids, double stepSize, boolean ignoreEntities, Precision precision, Consumer<Location> onStep, @Nullable Consumer<RayCastResult> onRayCastFinish) {
        return executeStepByStepWithPrecision(entity.getEyeLocation(), maxDistance, ignoreLiqiuds, stepSize, ignoreEntities, precision, onStep, onRayCastFinish);
    }

    public static Location getRayTraceLocation(Location starting, Vector direction, double distance) {
        Location ending = starting.clone().add(direction.clone().multiply(distance));
        return ending;
    }

    /**
     * My general advice for entity ray-casting is to just use {@link #PRECISE_ENTITY}, sorry or if you're willing to have some hit or miss.
     * Use {@link #ACCURATE_ENTITY}. I'm pretty sure {@link #ACCURATE_BLOCK} will work pretty well for most cases.
     */
    public enum Precision {

        /**
         * This is kind of pointless since block ray-casts aren't very heavy, performance wise.
         */
        IMPRECISE_BLOCK(1D),
        /**
         * Definitely inaccurate but saves on performance
         */
        IMPRECISE_ENTITY(0.25D),
        SEMI_ACCURATE_BLOCK(0.5D),
        /**
         * Messes up quite a bit not necessarily good for mobs with thin hitboxes.
         */
        SEMI_ACCURATE_ENTITY(0.125D),
        /**
         * Good for general purpose
         */
        ACCURATE_BLOCK(0.25D),
        /**
         * Good for general purpose, messes up a bit
         */
        ACCURATE_ENTITY(0.05D),
        /**
         * You should use this for anti-cheats if you want to be super safe.
         */
        PRECISE_BLOCK(0.1D),
        /**
         * You should use this for anti-cheats if you want to be super safe. Otherwise
         */
        PRECISE_ENTITY(0.01D);

        private double advance;

        Precision(double advance) {
            this.advance = advance;
        }

        public double getAdvance() {
            return advance;
        }
    }


}
