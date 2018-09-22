package com.yhd.arch.tuna.linktree.serialize;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Created by root on 12/2/16.
 */
public class KryoSerializer {
	private Kryo kryo = new Kryo();

	public byte[] serialize(Object data) {
		Output output = new Output(new ByteArrayOutputStream());
		kryo.writeClassAndObject(output, data);
		return output.toBytes();
	}

	public Object deserialize(byte[] data) {
		Input input = new Input(new ByteArrayInputStream(data));
		return kryo.readClassAndObject(input);
	}
}
