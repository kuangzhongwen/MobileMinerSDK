package waterhole.commonlibs.net.apache;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 重写FilterInputStream，实现进度监听的功能
 *
 * @author kzw on 2015/12/16.
 */
final class CountingInputStream extends FilterInputStream {

    // Http传输进度监听
    private final HttpProgressListener mHttpProgressListener;

    // 传输的字节
    private long mTransferred;

    /**
     * Constructs a new {@code FilterInputStream} with the specified input
     * stream as source.
     * <p/>
     * <p><strong>Warning:</strong> passing a null source creates an invalid
     * {@code FilterInputStream}, that fails on every method that is not
     * overridden. Subclasses should check for null in their constructors.
     *
     * @param in the input stream to filter reads on.
     */
    CountingInputStream(InputStream in, HttpProgressListener httpProgressListener) {
        super(in);
        mHttpProgressListener = httpProgressListener;
        mTransferred = 0L;
    }

    /**
     * Reads a single byte from the filtered stream and returns it as an integer
     * in the range from 0 to 255. Returns -1 if the end of this stream has been
     * reached.
     *
     * @return the byte read or -1 if the end of the filtered stream has been
     * reached.
     * @throws IOException if the stream is closed or another IOException occurs.
     */
    @Override
    public int read() throws IOException {
        int read = in.read();
        readCount(read);
        return read;
    }

    /**
     * Equivalent to {@code read(buffer, 0, buffer.length)}.
     */
    @Override
    public int read(byte[] buffer) throws IOException {
        int read = in.read(buffer);
        readCount(read);
        return read;
    }

    @Override
    public int read(byte[] buffer, int byteOffset, int byteCount) throws IOException {
        int read = in.read(buffer, byteOffset, byteCount);
        readCount(read);
        return read;
    }

    /**
     * Skips {@code byteCount} bytes in this stream. Subsequent
     * calls to {@code read} will not return these bytes unless {@code reset} is
     * used. This implementation skips {@code byteCount} bytes in the
     * filtered stream.
     *
     * @return the number of bytes actually skipped.
     * @throws IOException if this stream is closed or another IOException occurs.
     * @see #mark(int)
     * @see #reset()
     */
    @Override
    public long skip(long byteCount) throws IOException {
        long skip = in.skip(byteCount);
        readCount(skip);
        return skip;
    }

    private void readCount(long read) {
        if (read > 0) {
            mTransferred += read;
            mHttpProgressListener.transferred(mTransferred);
        }
    }
}
