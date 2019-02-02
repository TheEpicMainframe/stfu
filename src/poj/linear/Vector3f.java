package poj.linear;

import java.lang.Math;

import poj.Logger.Logger;
public class Vector3f
{

	public float x;
	public float y;
	public float z;

	public Vector3f(float x, float y, float z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}
	public Vector3f()
	{
	}


	public Vector3f add(final Vector3f a)
	{
		this.x += a.x;
		this.y += a.y;
		this.z += a.z;

		return this;
	}


	public Vector3f add(float n)
	{
		this.x += n;
		this.y += n;
		this.z += n;

		return this;
	}

	final public Vector3f pureAdd(final Vector3f a)
	{
		return new Vector3f(this.x + a.getX(), this.y + a.getY(),
				    this.z + a.getZ());
	}


	public Vector3f elemMul(final Vector3f a)
	{
		this.x *= a.x;
		this.y *= a.y;
		this.z *= a.z;

		return this;
	}

	public Vector3f elemMul(final float a)
	{
		this.x *= a;
		this.y *= a;
		this.z *= a;

		return this;
	}

	public Vector3f pureElemMul(final float a)
	{
		return new Vector3f(this.x * a, this.y * a, this.z * a);
	}


	public final void subtract(final Vector3f a)
	{

		this.x -= a.x;
		this.y -= a.y;
		this.z -= a.z;
	}

	public final void matrixVector3fProduct(final Matrix<Float> a)
	{

		float xnew, ynew, znew;
		// loop through each row of the matrix
		xnew = a.getDataWithIndex(0) * this.x
		       + a.getDataWithIndex(1) * this.y
		       + a.getDataWithIndex(2) * this.z;
		ynew = a.getDataWithIndex(3) * this.x
		       + a.getDataWithIndex(4) * this.y
		       + a.getDataWithIndex(5) * this.z;
		znew = a.getDataWithIndex(6) * this.x
		       + a.getDataWithIndex(7) * this.y
		       + a.getDataWithIndex(8) * this.z;
		this.x = xnew;
		this.y = ynew;
		this.z = znew;
	}

	public final Vector3f scalarProduct(final Vector3f a,
					    final float scalar)
	{
		return new Vector3f((a.x * scalar), (a.y * scalar),
				    (a.z * scalar));
	}

	public final float dotProduct(final Vector3f a, final Vector3f b)
	{
		return a.x * b.x + a.y * b.y + a.z * b.z;
	}

	static public final float scalarValueOfVector(final Vector3f a)
	{
		return (float)Math.sqrt(a.x * a.x + a.y * a.y + a.z * a.z);
	}

	static public final float abs(final Vector3f a)
	{
		return scalarValueOfVector(a);
	}

	static public Vector3f getUnitVectorByCopy(Vector3f a)
	{
		float abs = Vector3f.abs(a);
		return new Vector3f(a.getX() / abs, a.getY() / abs,
				    a.getZ() / abs);
	}

	public final float angleBetweenTwoVector3(final Vector3f a,
						  final Vector3f b)
	{
		// angle is NOT ABSOLUTE VALUE!! if negative angle then
		// pi/2< theta < pi
		return (float)Math.acos(
			dotProduct(a, b)
			/ (scalarValueOfVector(a) * scalarValueOfVector(b)));
	}

	public final Vector3f normalOfVector3f(final Vector3f a)
	{

		Logger.lassert(
			(a == new Vector3f(0, 0, 0)),
			"MAJOR ERROR in normalOfVector3f!!  the input vectors are 0!!");
		Vector3f tempVec = new Vector3f(1, 0, 0);
		if (a == tempVec) {
			return crossProduct(a, tempVec);
		} else {
			return crossProduct(a, new Vector3f(0, 1, 0));
		}
	}

	public final Vector3f crossProduct(final Vector3f a, final Vector3f b)
	{
		Logger.lassert(
			(a == new Vector3f(0, 0, 0)
			 || b == new Vector3f(0, 0, 0)),
			"MAJOR ERROR in crossProduct!! one of the vectors are 0!!");
		return new Vector3f(a.y * b.z - a.z * b.y,
				    a.z * b.x - a.x * b.z,
				    a.x * b.y - a.y * b.x);
	}

	public final float getX()
	{
		return this.x;
	}

	public final float getY()
	{
		return this.y;
	}

	public final float getZ()
	{
		return this.z;
	}

	static public final Vector3f deepCopyVector3f(Vector3f n)
	{
		return new Vector3f(n.getX(), n.getY(), n.getZ());
	}
}
