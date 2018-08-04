package net.jahhan.extension.serialization;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.inject.Singleton;

import net.jahhan.com.alibaba.dubbo.common.serialize.ObjectInput;
import net.jahhan.com.alibaba.dubbo.common.serialize.ObjectOutput;
import net.jahhan.com.alibaba.dubbo.common.serialize.support.json.JacksonObjectInput;
import net.jahhan.com.alibaba.dubbo.common.serialize.support.json.JacksonObjectOutput;
import net.jahhan.common.extension.annotation.Extension;
import net.jahhan.spi.common.Serialization;

/**
 * JsonSerialization
 * 
 * @author dylan
 */
@Extension("jackson")
@Singleton
public class JacksonSerialization implements Serialization {

	public byte getContentTypeId() {
		return 20;
	}

	public String getContentType() {
		return "text/json";
	}

	public ObjectOutput serialize(OutputStream output) throws IOException {
		return new JacksonObjectOutput(output);
	}

	public ObjectInput deserialize(InputStream input) throws IOException {
		return new JacksonObjectInput(input);
	}

}