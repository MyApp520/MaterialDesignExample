package com.smile.commonlib.util;

import java.nio.charset.Charset;

/**
 * @author greensun
 * @date 2020/4/10
 * @desc L 小端  B 大端
 */
public class ByteUtil {

    public static byte[] getBBytes(short data) {
        byte[] bytes = new byte[2];
        bytes[1] = (byte) (data & 0xff);
        bytes[0] = (byte) ((data >> 8) & 0xff);
        return bytes;
    }

    public static byte[] getBBytes(char data) {
        byte[] bytes = new byte[2];
        bytes[1] = (byte) (data & 0xff);
        bytes[0] = (byte) ((data >> 8) & 0xff);
        return bytes;
    }

    public static byte[] getBBytes(int data) {
        byte[] bytes = new byte[4];
        bytes[3] = (byte) (data & 0xff);
        bytes[2] = (byte) ((data >> 8) & 0xff);
        bytes[1] = (byte) ((data >> 16) & 0xff);
        bytes[0] = (byte) ((data >> 24) & 0xff);
        return bytes;
    }

    public static short getBShort(byte[] bytes, int offset) {
        return (short) (((0xff & bytes[offset]) << 8) | (0xff & bytes[offset]));
    }

    public static char getBChar(byte[] bytes, int offset) {
        return (char) (((0xff & bytes[offset]) << 8) | (0xff & bytes[offset]));
    }

    public static int getBInt(byte[] bytes, int offset) {
        return ((0xff & bytes[offset]) << 24) | ((0xff & bytes[offset + 1]) << 16) | ((0xff & bytes[offset + 2]) << 8) | (0xff & bytes[offset + 3]);
    }

    public static byte[] getLBytes(short data) {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) (data & 0xff);
        bytes[1] = (byte) ((data >> 8) & 0xff);
        return bytes;
    }

    public static byte[] getLBytes(char data) {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) (data & 0xff);
        bytes[1] = (byte) ((data >> 8) & 0xff);
        return bytes;
    }

    public static byte[] getLBytes(int data) {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) (data & 0xff);
        bytes[1] = (byte) ((data >> 8) & 0xff);
        bytes[2] = (byte) ((data >> 16) & 0xff);
        bytes[3] = (byte) ((data >> 24) & 0xff);
        return bytes;
    }

    public static short getLShort(byte[] bytes, int offset) {
        return (short) ((0xff & bytes[0]) | ((0xff & bytes[offset + 1]) << 8));
    }

    public static char getLChar(byte[] bytes, int offset) {
        return (char) ((0xff & bytes[offset]) | ((0xff & bytes[offset + 1]) << 8));
    }

    public static int getLInt(byte[] bytes, int offset) {
        return (0xff & bytes[offset]) | ((0xff & bytes[offset + 1]) << 8) | ((0xff & bytes[offset + 2]) << 16) | ((0xff & bytes[offset + 3]) << 24);
    }

    public static byte[] getBytes(String data, String charsetName) {
        Charset charset = Charset.forName(charsetName);
        return data.getBytes(charset);
    }


    private static final char[] HEX_CHAR = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static String bytesToHexFun2(byte[] bytes) {
        char[] buf = new char[bytes.length * 2];
        int index = 0;
        for (byte b : bytes) { // 利用位运算进行转换，可以看作方法一的变种
            buf[index++] = HEX_CHAR[b >>> 4 & 0xf];
            buf[index++] = HEX_CHAR[b & 0xf];
        }
        return new String(buf);
    }
}
