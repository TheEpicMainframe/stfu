package EntitySets;

import Components.*;
import poj.EntitySet.*;

public class A extends EntitySet
{
	public A()
	{
		super();
		addComponent(new Physics(0));
	}
}
