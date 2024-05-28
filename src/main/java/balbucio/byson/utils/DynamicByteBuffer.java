package balbucio.byson.utils;

import java.nio.ByteBuffer;

public class DynamicByteBuffer {

    private ByteBuffer buffer;

    public DynamicByteBuffer(int initialCapacity) {
        buffer = ByteBuffer.allocate(initialCapacity);
    }

    public DynamicByteBuffer(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    private void ensureCapacity(int additionalCapacity) {
        if (buffer.remaining() < additionalCapacity) {
            int newCapacity = Math.max(buffer.capacity() * 2, buffer.capacity() + additionalCapacity);
            ByteBuffer newBuffer = ByteBuffer.allocate(newCapacity);
            buffer.flip();
            newBuffer.put(buffer);
            buffer = newBuffer;
        }
    }

    public void put(byte b) {
        ensureCapacity(1);
        buffer.put(b);
    }

    public void put(byte[] bytes) {
        ensureCapacity(bytes.length);
        buffer.put(bytes);
    }

    public void putFlip(byte[] bytes) {
        ensureCapacity(bytes.length);
        buffer.position(0);
        buffer.put(bytes);
    }

    public ByteBuffer getBuffer() {
        buffer.flip();
        return buffer;
    }
}
