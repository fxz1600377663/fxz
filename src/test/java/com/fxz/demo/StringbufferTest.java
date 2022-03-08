package com.fxz.demo;

public class StringbufferTest {
    public static int d;

    public static int getD() {
        return d;
    }

    public static void main(String[] args) {
        String str = new String();
        StringBuffer stringBuffer = new StringBuffer(3);
        stringBuffer.append("1");
        stringBuffer.append("2");
        stringBuffer.append("3");
        stringBuffer.append("4");
        stringBuffer.append("5");
        stringBuffer.append("6");
        stringBuffer.append("7");
        stringBuffer.append("7");
        stringBuffer.append("7");
        System.out.println(stringBuffer);
        System.out.println(stringBuffer.capacity());

        Object a = new Object();
        Object b = a;
        int e = 6;
        System.out.println(new StringbufferTest().getD());
        System.out.println(e);
        System.out.println(a.equals(b));
        System.out.println(a.hashCode() == b.hashCode());


    }
}
