package balbucio.byson.utils;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class SplittedByteBuffer {
    private static final int BUFFER_SIZE = 65550;
    private List<ByteBuffer> buffers = new ArrayList<>();
    private ByteBuffer currentBuffer;

    public SplittedByteBuffer() {
        addNewBuffer();
    }

    private void addNewBuffer() {
        currentBuffer = ByteBuffer.allocate(BUFFER_SIZE);
        buffers.add(currentBuffer);
    }

    public void put(byte b) {
        if (!currentBuffer.hasRemaining()) {
            addNewBuffer();
        }
        currentBuffer.put(b);
    }

    public void put(byte[] bytes) {
        int offset = 0;
        while (offset < bytes.length) {
            if (!currentBuffer.hasRemaining()) {
                addNewBuffer();
            }
            int length = Math.min(currentBuffer.remaining(), bytes.length - offset);
            currentBuffer.put(bytes, offset, length);
            offset += length;
        }
    }

    public List<ByteBuffer> getBuffers() {
        for (ByteBuffer buffer : buffers) {
            buffer.flip();
        }
        return buffers;
    }
}

