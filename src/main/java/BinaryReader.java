import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

// Reference: https://github.com/dotnet/runtime/blob/1d1bf92fcf43aa6981804dc53c5174445069c9e4/src/libraries/System.Private.CoreLib/src/System/IO/BinaryReader.cs

public class BinaryReader implements AutoCloseable{

    private DataInputStream in;

    public BinaryReader(InputStream stream) {
        this.in = new DataInputStream(stream);
    }

    public int readByte() throws IOException {
        return in.read();
    }

    public String readString() throws IOException {

        in.readByte(); // Skip the 0B byte. (Used to tell that the following data is in binary base 2).

        int length = read7BitEncodedInt();

        byte[] bytes = new byte[length];

        try {
            in.readFully(bytes);
        } catch (EOFException e) {
            e.printStackTrace();
        }

        return new String(bytes, "UTF-8"); // Assuming UTF-8 encoding
    }

    public int readInt32() throws IOException {

        byte[] buffer = new byte[4];
        int bytesRead = in.read(buffer, 0, 4);

        // Check if we read the expected number of bytes
        if (bytesRead != 4) {
            throw new IOException("Unexpected End of Stream");
        }

        // Convert the bytes to an integer using little-endian order
        int value = ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN).getInt();

        return value;
    }

    private int read7BitEncodedInt() throws IOException {

        int result = 0;
        int shift = 0;
        int b;

        do {
            if (shift == 35) { // 7 * 5 bits = 35 bits, which is more than enough to handle 32-bit integers.
                throw new IOException("Malformed 7-bit encoded integer.");
            }

            // Read the next byte
            b = in.read();

            if (b == -1) {
                throw new EOFException("Unexpected end of stream.");
            }

            // Add the lower 7 bits of the byte to the result
            result |= (b & 0x7F) << shift;

            // Increase shift for the next 7 bits
            shift += 7;

        } while ((b & 0x80) != 0); // Continue while the high bit is set.

        return result;
    }

    @Override
    public void close() throws Exception {
        in.close();
    }
}