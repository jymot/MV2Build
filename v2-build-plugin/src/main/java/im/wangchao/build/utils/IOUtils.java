package im.wangchao.build.utils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLConnection;

/**
 * <p>Description  : IOUtils.</p>
 * <p/>
 * <p>Author       : wangchao.</p>
 * <p>Date         : 15/12/11.</p>
 * <p>Time         : 下午3:09.</p>
 */
public class IOUtils {
    private IOUtils(){
        throw new AssertionError();
    }

    /**
     * Closes {@code closeable}, ignoring any checked exceptions. Does nothing
     * if {@code closeable} is null.
     */
    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (RuntimeException rethrown) {
                throw rethrown;
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * Closes {@code socket}, ignoring any checked exceptions. Does nothing if
     * {@code socket} is null.
     */
    public static void closeQuietly(Socket socket) {
        if (socket != null) {
            try {
                socket.close();
            } catch (RuntimeException rethrown) {
                throw rethrown;
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * Closes {@code serverSocket}, ignoring any checked exceptions. Does nothing if
     * {@code serverSocket} is null.
     */
    public static void closeQuietly(ServerSocket serverSocket) {
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (RuntimeException rethrown) {
                throw rethrown;
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * Closes a URLConnection.
     *
     * @param conn the connection to close.
     */
    public static void closeQuietly(final URLConnection conn) {
        if (conn instanceof HttpURLConnection) {
            ((HttpURLConnection) conn).disconnect();
        }
    }

    /**
     * Read {@code in}, to byte[]
     * @throws IOException
     */
    public static byte[] readBytes(InputStream in) throws IOException {
        if (!(in instanceof BufferedInputStream)) {
            in = new BufferedInputStream(in);
        }
        ByteArrayOutputStream out = null;
        try {
            out = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) != -1) {
                out.write(buf, 0, len);
            }
        } finally {
            closeQuietly(out);
        }
        return out.toByteArray();
    }

    /**
     * Read {@code in}, to byte[]
     * @throws IOException
     */
    public static byte[] readBytes(InputStream in, long skip, long size) throws IOException {
        ByteArrayOutputStream out = null;
        try {
            if (skip > 0) {
                long skipSize = 0;
                while (skip > 0 && (skipSize = in.skip(skip)) > 0) {
                    skip -= skipSize;
                }
            }
            out = new ByteArrayOutputStream();
            for (int i = 0; i < size; i++) {
                out.write(in.read());
            }
        } finally {
            closeQuietly(out);
        }
        return out.toByteArray();
    }

    /**
     * Read {@code in}, to String
     * @throws IOException
     */
    public static String readStr(InputStream in) throws IOException {
        return readStr(in, "UTF-8");
    }

    /**
     * Read {@code in}, to String
     * @throws IOException
     */
    public static String readStr(InputStream in, String charset) throws IOException {
        if (StringUtils.isEmpty(charset)) charset = "UTF-8";

        if (!(in instanceof BufferedInputStream)) {
            in = new BufferedInputStream(in);
        }
        Reader reader = new InputStreamReader(in, charset);
        try {
            StringBuilder sb = new StringBuilder();
            char[] buf = new char[1024];
            int len;
            while ((len = reader.read(buf)) >= 0) {
                sb.append(buf, 0, len);
            }
            return sb.toString().trim();
        } finally {
            closeQuietly(reader);
        }

    }

    /**
     * Write {@code out}, default charset UTF-8
     * @throws IOException
     */
    public static void writeStr(OutputStream out, String str) throws IOException {
        writeStr(out, str, "UTF-8");
    }

    /**
     * Write {@code out}
     * @throws IOException
     */
    public static void writeStr(OutputStream out, String str, String charset) throws IOException {
        if (StringUtils.isEmpty(charset)) charset = "UTF-8";
        Writer writer = null;
        try{
            writer = new OutputStreamWriter(out, charset);
            writer.write(str);
            writer.flush();
        } finally {
            closeQuietly(writer);
        }
    }

    /**
     * Write {@code out}
     * @throws IOException
     */
    public static void writeBytes(OutputStream out, byte[] content) throws IOException {
        if (!(out instanceof BufferedOutputStream)){
            out = new BufferedOutputStream(out);
        }
        out.write(content);
        out.flush();
    }

    /**
     * Copy {@code in} to {@code out}
     * @throws IOException
     */
    public static void copy(InputStream in, OutputStream out) throws IOException {
        if (!(in instanceof BufferedInputStream)) {
            in = new BufferedInputStream(in);
        }
        if (!(out instanceof BufferedOutputStream)) {
            out = new BufferedOutputStream(out);
        }
        int len = 0;
        byte[] buffer = new byte[1024];
        while ((len = in.read(buffer)) != -1) {
            out.write(buffer, 0, len);
        }
        out.flush();
    }
}
