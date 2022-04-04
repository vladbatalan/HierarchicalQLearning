package timebender.physics.utils;

import timebender.physics.PointVector;

public class PhysicsConstants {
    public static final PointVector GRAVITY_ACCELERATION = new PointVector(0, 0.098f);
    public static final float JUMP_CONSTANT = 90f;
    public static final float JUMP_DECREASE_RATIO = 0.65f;

    public static PointVector GravityForce(float mass){
        return GRAVITY_ACCELERATION.scalarMultiply(mass);
    }
}
