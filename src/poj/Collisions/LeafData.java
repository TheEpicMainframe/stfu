package poj.Collisions;

// leaf data
public class LeafData<T extends CollisionShape>
{
	public T leftLeaf;
	public T rightLeaf;

	public Rectangle insertInLeaf(T cs)
	{
		// Compleetley empty -- so fill the right side up
		if (this.leftLeaf == null && this.rightLeaf == null) {
			this.rightLeaf = cs;
		} else if (this.leftLeaf == null
			   && this.rightLeaf
				      != null) { // right side should be filled
						 // so fill the left side up

			// new collision shape has greater area than the right
			// node
			if (cs.getBoundingRectangle().getArea()
			    > this.rightLeaf.getBoundingRectangle().getArea()) {
				this.leftLeaf = this.rightLeaf;
				this.rightLeaf = cs;

			}
			// new collision shape has less area than the right node
			else {
				this.leftLeaf = cs;
			}
		}

		return Rectangle.getBoundingRectOfRects(
			this.getLeftBoundingRect(),
			this.getRightBoundingRect());
	}


	public Rectangle getLeftBoundingRect()
	{
		if (leftLeaf == null)
			return null;
		else
			return leftLeaf.getBoundingRectangle();
	}

	public Rectangle getRightBoundingRect()
	{
		if (rightLeaf == null)
			return null;
		else
			return rightLeaf.getBoundingRectangle();
	}

	public void clearLeaf()
	{
		leftLeaf = null;
		rightLeaf = null;
	}

	public void swapLeftAndRight()
	{
		T tmp = leftLeaf;
		leftLeaf = rightLeaf;
		rightLeaf = tmp;
	}

	public boolean leafIsFull()
	{
		return rightLeaf != null && leftLeaf != null;
	}
}
