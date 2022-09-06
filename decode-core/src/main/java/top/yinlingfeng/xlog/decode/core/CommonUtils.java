package top.yinlingfeng.xlog.decode.core;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.zip.Inflater;
import java.util.zip.InflaterOutputStream;

public class CommonUtils {

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    public static boolean containsHexPrefix(String input) {
        return input.length() > 1 && input.charAt(0) == '0' && input.charAt(1) == 'x';
    }

    public static String cleanHexPrefix(String input) {
        if (containsHexPrefix(input)) {
            return input.substring(2);
        } else {
            return input;
        }
    }

    public static byte[] hexStringToByteArray(String input) {
        String cleanInput = cleanHexPrefix(input);

        int len = cleanInput.length();

        if (len == 0) {
            return new byte[] {};
        }

        byte[] data;
        int startIdx;
        if (len % 2 != 0) {
            data = new byte[(len / 2) + 1];
            data[0] = (byte) Character.digit(cleanInput.charAt(0), 16);
            startIdx = 1;
        } else {
            data = new byte[len / 2];
            startIdx = 0;
        }

        for (int i = startIdx; i < len; i += 2) {
            data[(i + 1) / 2] = (byte) ((Character.digit(cleanInput.charAt(i), 16) << 4)
                    + Character.digit(cleanInput.charAt(i+1), 16));
        }
        return data;
    }

    public static String bytesToHexString(byte[] src){
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    public static byte[] longToArray(long x, long y) {
        ByteBuffer buffer = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);
        buffer.putInt((int)x);
        buffer.putInt((int)y);
        return buffer.array();
    }

    public static int bytesToInt(byte[] b, int offset) {
        int v = b[offset] & 0xFF |
                (b[offset + 1] & 0xFF) << 8 |
                (b[offset + 2] & 0xFF) << 16 |
                (b[offset + 3] & 0xFF) << 24;
        return v;
    }

    public static byte[] tea_decipher(byte[] byte_array, byte[] k) {
        long op = 0xffffffffL;
        long delta = 0x9E3779B9L;
        long s = (delta << 4) & op;

        long v0 = bytesToInt(byte_array, 0) & 0x0FFFFFFFFL;
        long v1 = bytesToInt(byte_array, 4) & 0x0FFFFFFFFL;

        long k1 = bytesToInt(k, 0) & 0x0FFFFFFFFL;
        long k2 = bytesToInt(k, 4) & 0x0FFFFFFFFL;
        long k3 = bytesToInt(k, 8) & 0x0FFFFFFFFL;
        long k4 = bytesToInt(k, 12) & 0x0FFFFFFFFL;

        int cnt = 16;
        while (cnt > 0) {
            v1 = (v1 - (((v0 << 4) + k3) ^ (v0 + s) ^ ((v0 >> 5) + k4))) & op;
            v0 = (v0 - (((v1 << 4) + k1) ^ (v1 + s) ^ ((v1 >> 5) + k2))) & op;
            s = (s - delta) & op;
            cnt--;
        }

        return longToArray(v0, v1);
    }

    public static byte[] tea_decrypt(byte[] v, byte[] k){
        int num = v.length >> 3 << 3;
        ByteBuffer ret = ByteBuffer.allocate(v.length).order(ByteOrder.LITTLE_ENDIAN);
        for (int i =0; i < num; i += 8) {
            byte[] sv = new byte[8];
            ByteBuffer.wrap(v, i, 8).get(sv);
            byte[] x = tea_decipher(sv, k);
            ret.put(x);
        }
        byte[] remain = new byte[v.length-num];
        ByteBuffer.wrap(v, num, v.length-num).get(remain);
        ret.put(remain);
        return ret.array();
    }

    public static byte[] decompress(byte[] encdata) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            InflaterOutputStream zos = new InflaterOutputStream(bos, new Inflater(true));
            zos.write(encdata);
            zos.close();
            return bos.toByteArray();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static byte[] zstdDecompress(byte[] encodeData) {
        byte[] writeByte = new byte[1];
        return writeByte;
    }
}
