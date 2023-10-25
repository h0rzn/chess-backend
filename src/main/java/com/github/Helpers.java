package com.github;

public class Helpers {
    static public String outAs64String(int value) {
        String binString = Integer.toBinaryString( value);
        return String.format("%32s", binString).replaceAll(" ", "0");
    }

    static public String outAs64String(byte value) {
        int unsigned = Byte.toUnsignedInt(value);
        return outAs64String(unsigned);
    }
}
