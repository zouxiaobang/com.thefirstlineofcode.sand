package com.firstlinecode.sand.client.dummygateway;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import com.firstlinecode.sand.client.dummything.IDummyThing;

public class DummyThingInfo implements Externalizable {
	private int x;
	private int y;
	private IDummyThing thing;
	
	public DummyThingInfo() {}
	
	public DummyThingInfo(int x, int y, IDummyThing thing) {
		this.x = x;
		this.y = y;
		this.thing = thing;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public int getX() {
		return x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getY() {
		return y;
	}
	
	public IDummyThing getThing() {
		return thing;
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeInt(x);
		out.writeInt(y);
		out.writeObject(thing);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		x = in.readInt();
		y = in.readInt();
		thing = (IDummyThing)in.readObject();
	}
}
