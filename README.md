# Byson Java
Quickly convert JSON to binary in Java using ByteBuffer.

### Motivation
I needed to convert JSON (specifically org.json) into binary (more specifically ByteBuffer to travel over the network).

### Some Warnings
I haven't yet invested enough time to ensure everything will be serialized and deserialized correctly, so work carefully.

During deserialization and serialization (mainly) there is a memory experiment (this is because it is necessary to have the JSONObject and the ByteBuffer for any operation), I intend to work on reducing this or offering some replacement for both cases (like writing JSON directly in Byson) .

### Get Started
Working with Byson is very simple and is available in Java 8 or later.

Quick code example:

```java
import balbucio.byson.BysonParser;
import org.json.JSONObject;
import java.nio.ByteBuffer;

// este é nosso JSON
JSONObject json = new JSONObject();

ByteBuffer buffer = BysonParser.serialize(json);
JSONObject deserialized = BysonParser.deserialize(buffer);
```

Understand more about the classes here: [JavaDocs](https://srbalbucio.github.io/byson-java/)