package mygame;

import com.jme3.bullet.joints.SixDofSpringJoint;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Matrix3f;
import com.jme3.math.Vector3f;

/**
 *
 * @author Matías Bonino
 */
public class Link extends SixDofSpringJoint {

    private float limit;

    public Link() {
    }

    public Link(float limit, PhysicsRigidBody nodeA, PhysicsRigidBody nodeB, Vector3f pivotA, Vector3f pivotB, Matrix3f rotA, Matrix3f rotB, boolean useLinearReferenceFrameA) {
        super(nodeA, nodeB, pivotA, pivotB, rotA, rotB, useLinearReferenceFrameA);
        this.limit = limit;
    }

    @Override
    public long getObjectId() {
        return objectId;
    }

    public PhysicsRigidBody getNodeA() {
        return nodeA;
    }

    public PhysicsRigidBody getNodeB() {
        return nodeB;
    }

    @Override
    public Vector3f getPivotA() {
        return pivotA;
    }

    @Override
    public Vector3f getPivotB() {
        return pivotB;
    }

    public float getLimit() {
        return limit;
    }

}
