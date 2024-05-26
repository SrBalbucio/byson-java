package balbucio.byson.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;
import java.util.zip.InflaterOutputStream;

public class BysonCompressHelper {

    public static byte[] compress(byte[] bArray) {
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            try (DeflaterOutputStream dos = new DeflaterOutputStream(os)) {
                dos.write(bArray);
            }
            return os.toByteArray();
        } catch (Exception ignored) {
            return bArray;
        }
    }

    public static byte[] decompress(byte[] byts) {
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            try (OutputStream ios = new InflaterOutputStream(os)) {
                ios.write(byts);
            }

            return os.toByteArray();
        } catch (Exception ignored) {
            return byts;
        }
    }

    public static ByteBuffer compress(ByteBuffer inputBuffer) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(byteArrayOutputStream)) {
            while (inputBuffer.hasRemaining()) {
                deflaterOutputStream.write(inputBuffer.get());
            }
        }
        byte[] compressedBytes = byteArrayOutputStream.toByteArray();
        return ByteBuffer.wrap(compressedBytes);
    }

    public static ByteBuffer decompress(ByteBuffer compressedBuffer) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(compressedBuffer.array());
             InflaterInputStream inflaterInputStream = new InflaterInputStream(byteArrayInputStream)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inflaterInputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }
        }
        byte[] decompressedBytes = byteArrayOutputStream.toByteArray();
        return ByteBuffer.wrap(decompressedBytes);
    }
}
