// SPDX-FileCopyrightText: 2025 owlmaddie LLC
// SPDX-License-Identifier: GPL-3.0-or-later
// Assets CC-BY-NC-SA-4.0; CreatureChat™ trademark © owlmaddie LLC - unauthorized use prohibited
package com.owlmaddie.particle;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.SimpleParticleType;

/**
 * Particle definitions for CreatureChat. Modified for Minecraft 1.20.5+ compatibility, and now
 * based on SimpleParticleType and not DefaultParticletype, etc...
 */
public class Particles {
    public static final SimpleParticleType HEART_SMALL_PARTICLE   = FabricParticleTypes.simple();
    public static final SimpleParticleType HEART_BIG_PARTICLE     = FabricParticleTypes.simple();
    public static final SimpleParticleType FIRE_SMALL_PARTICLE    = FabricParticleTypes.simple();
    public static final SimpleParticleType FIRE_BIG_PARTICLE      = FabricParticleTypes.simple();
    public static final SimpleParticleType ATTACK_PARTICLE        = FabricParticleTypes.simple();
    public static final SimpleParticleType FLEE_PARTICLE          = FabricParticleTypes.simple();
    public static final SimpleParticleType FOLLOW_FRIEND_PARTICLE = FabricParticleTypes.simple();
    public static final SimpleParticleType FOLLOW_ENEMY_PARTICLE  = FabricParticleTypes.simple();
    public static final SimpleParticleType PROTECT_PARTICLE       = FabricParticleTypes.simple();
    public static final SimpleParticleType LEAD_FRIEND_PARTICLE   = FabricParticleTypes.simple();
    public static final SimpleParticleType LEAD_ENEMY_PARTICLE    = FabricParticleTypes.simple();
    public static final ParticleType<LeadParticleEffect> LEAD_PARTICLE = LeadParticleEffect.TYPE;
}