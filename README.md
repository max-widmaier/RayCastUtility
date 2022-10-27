# RayCastUtility

This is a very, very niche resource because I doubt much of anyone needs to use ray-casting for their plugins. But, I found myself needing it since I am programming plugins that are version compatible from 1.8-1.19. In 1.8 there is no Bukkit implemented ray-casting/raytracing. Anyways, if you find yourself in a similar situations, I hope this utility is useful!

# Contents
[What can this do?](#what-can-this-thing-do)

[Levels of Precision](#levels-of-precision)

[Code Example](#code-examples)
1. [General Examples](#normal-ray-casting)
2. [Step by step ray-cast usage](#step-by-step-examples)

[Specific Performance Information](#specific-performance-information)

[How can I get this in my project?](#how-can-i-get-this-in-my-project)

[NMS Disclaimer](#nms-disclaimer)

## What can this thing do?
You can use this utility to ray-cast blocks and entities based on where the player is looking. Want to find out what block the player is looking at (ignoring entities)? Use rayCastBlocks. Want to find what entity the player is looking at? Use rayCastEntities. Want to find out whether the player is looking at a block or entity? Use rayCast. You can specify a maximum distance and a specific precision. Want to run code for every step along a ray-cast? Use executeStepByStep. I have a bunch of testing information in the class javadoc on specific precisions and it's performance impact. This doesn't necessarily apply to players though! You can also raytrace from the eyes of any LivingEntity!

## Levels of Precision
### Inaccurate Block Precision
The check location will move 1 block ahead for every check. It's very innacurate and could lead to some issues if you need preciseness. If you're, say, replacing the block the player's looking at, I'd say this would work most of the time.

### Inaccurate Entity Precision
The check location will move 0.25 blocks ahead for every check. This is also very innacurate. I can't think of what I'd use it for, but it might be useful for say a `low performance server` option or something.

### Semi-Accurate Block Precision
The check location will move 0.5 blocks ahead for every check. This is pretty reliable except for non-full blocks.
### Semi-Accurate Entity Precision
The check location will move 0.125 blocks ahead for every check. This is definitely more reliable but can still goof up some times, especially when looking at entities where the hitbox is thinnest.

### Accurate Block Precision (Recommended)
The check location will move 0.25 blocks ahead for every check. This one has the best performance and I would say this is very accurate for general purpose use.

### Accurate Entity Precision
The check location will move 0.05 blocks ahead for every check. This is pretty much accurate unless you're looking at an entity with a small hitbox or at an odd angle.

### Precise Block Precision
The check location will move 0.1 blocks ahead for every check. Guareenteed to get the correct block every time. Anti-cheat grade.

### Precise Entity Precision (Recommended)
The check location will move 0.01 blocks ahead for every check. Guarenteed to get the correct entity no matter the angle or hit box size. Anti-cheat grade.

# Code Examples
## Normal ray-casting
### Discrepency between what player breaks and what player is looking at.
```java
public void onBreak(BlockBreakEvent event) {
    Block block = event.getBlock();
    RayCastUtility.BlockRayCastResult result = RayCastUtility.rayCastBlocks(event.getPlayer(), 6, true, RayCastUtility.Precision.PRECISE_BLOCK);
    if (!result.getBlock().equals(block)) {
       event.setCancelled(true);
       // That's not the block you're looking at!
    }
}
```

### Set the block player is looking at to a diamond block
```java
RayCastUtility.BlockRayCastResult result = RayCastUtility.rayCastBlocks(event.getPlayer(), 10, true, RayCastUtility.Precision.ACCURATE_BLOCK);
result.getBlock().setType(Material.DIAMOND_BLOCK);
```

### Kill the entity the player is looking
```java
RayCastUtility.EntityRayCastResult result = RayCastUtility.rayCastEntities(event.getPlayer(), 10, true, RayCastUtility.Precision.ACCURATE_BLOCK);
result.getEntity().remove();
//Shoot off some fireworks and call it laser eyes or something
```

### Set off TnT at whatever the player is looking at
```java
RayCastUtility.RayCastResult result = RayCastUtility.rayCast(event.getPlayer(), 10, true, RayCastUtility.Precision.ACCURATE_BLOCK);
if (result.getType() == RayCastUtility.ResultType.BLOCK) {
    Block block = (Block) result.get();
    Location loc = block.getLocation();
    TNTPrimed tnt = (TNTPrimed) entity.getWorld().spawnEntity(loc, EntityType.PRIMED_TNT);
    tnt.setFuseTicks(80);
} else if (result.getType() == RayCastUtility.ResultType.ENTITY) {
    Entity entity = (Entity) result.get();
    Location loc = entity.getLocation();
    TNTPrimed tnt = (TNTPrimed) entity.getWorld().spawnEntity(loc, EntityType.PRIMED_TNT);
    tnt.setFuseTicks(80);
}
```

## Step by step examples
### Spawning TnT every step (Doesn't do anything on ray-cast finish)
```java
RayCastUtility.executeStepByStep(player, 20, true, 0.5D, true, stepLocation -> {
    TNTPrimed tnt = (TNTPrimed) stepLocation.getWorld().spawnEntity(stepLocation, EntityType.PRIMED_TNT);
    tnt.setFuseTicks(60);
}, null);
```

### Spawning TnT every step (Sets a block the raytrace hits to obsidian)
```java
RayCastUtility.executeStepByStep(player, 20, true, 0.5D, true, stepLocation -> {
    TNTPrimed tnt = (TNTPrimed) stepLocation.getWorld().spawnEntity(stepLocation, EntityType.PRIMED_TNT);
    tnt.setFuseTicks(60);
}, raycastFinishResult -> {
    if (result.getType() == RayCastUtility.ResultType.BLOCK) {
        Block block = (Block) result.get();
        block.setType(Material.OBSIDIAN);
    }
});
```

### Laser Eyes
```java
RayCastUtility.executeStepByStep(player, 20, true, 0.5D, true, stepLocation -> {
    stepLocation.getWorld().spawnParticle(Particle.CRIT, stepLocation.getX(), stepLocation.getY(), stepLocation.getZ(), 1);
}, raycastFinishResult -> {
    if (raycastFinishResult.getType() == RayCastUtility.ResultType.BLOCK) {
        Location blockLoc = ((Block) raycastFinishResult.get()).getLocation();
        blockLoc.getWorld().createExplosion(blockLoc.getX(), blockLoc.getY(), blockLoc.getZ(), 2.5F, false, true);
    } else if (raycastFinishResult.getType() == RayCastUtility.ResultType.ENTITY) {
        Entity e = (Entity) raycastFinishResult.get();
        if (e instanceof LivingEntity) {
            ((LivingEntity)e).damage(10D)
        } else {
            e.remove();
        }
    }
});
```

I hope you got the jist of it. There's a ton of stuff you can do with ray-casting. From party tricks to cheat detection!

## Specific Performance Information
**I used a 1.16.5 server with 2GB of RAM. I ran each method (rayCast, rayCastEntities, rayCastBlocks) 1,000 times.**

**Note: Times for entity raycasting vary depending on how many entities are currently loaded.**\

Units are in milliseconds
|  Precision Level  |  Block  |  Entity  |  All (Entity and Block)  |
|---|---|---|---|
|  Inaccurate  |  61.22 ms (0.06 ms/raycast)  |  71.96 ms (0.07 ms/raycast)  |  120.24 ms (0.12 ms/raycast)  |
|  Semi-Accurate  |  71.90 ms (0.07 ms/raycast)  |  97.18 ms (0.10 ms/raycast)  |  177.97 ms (0.18 ms/raycast)  |
|  Accurate  |  61.64 ms (0.06 ms/raycast)  |  225.14 ms (0.23 ms/raycast)  |  253.52 ms (0.25 ms/raycast)  |
|  Precise  |  80.07 ms (0.08 ms/raycast)  |  395.43 ms (0.40 ms/raycast)  |  437.01 ms (0.44 ms/raycast)  |

## How can I get this in my project?
You can use [this paste bin](https://pastebin.com/ifnGu6rZ) or you can download the file from [this repository](https://github.com/YeetmanLord/RayCastUtility/blob/main/RayCastUtility.java).

## Warning! This code uses NMS.
This means that it is possible for this code not to work. I am fairly confident that this code should work from 1.8.8-1.19. If you see any kind of NoSuchMethodException in the console, or the ray-casting utility doesn't seem to produce results, this is the likely reason. I had to use NMS to get an entity's bounding boxes while ray-casting entities. Anyways this code is still version-independent because of Java Reflection, so at least there's that. TL;DR this has NMS and I haven't checked every single version, so it's possible that some versions don't fully work. Later, I will go and test all major versions from 1.8.8 to 1.19. If you have any issues, please report them to the [issues page](https://github.com/YeetmanLord/RayCastUtility/issues) and make sure to specify the server's version.
