package com.bsuir.balashenka;

import jssc.SerialPort;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

public class Serial implements SerialInterface {
    private SerialPort serialPort;
    private boolean opened;

    public Serial(String portName) {
        this.serialPort = new SerialPort(portName);
        this.opened = false;
        encapsulate(null);
        decapsulate(null);
    }

    public boolean open() {
        try {
            this.opened = serialPort.openPort();
            System.out.println("Port is opened.");
            return this.opened;
        } catch (SerialPortException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean close() {
        try {
            if (this.opened) {
                this.opened = false;
                System.out.println("Port is closed.");
                return serialPort.closePort();
            }
            return false;
        } catch (SerialPortException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean write(byte[] bytes, boolean flagCRC) {
        if(flagCRC){
            try {
                return serialPort.writeBytes(crcCoding(bytes));
            } catch (SerialPortException ex) {
                ex.printStackTrace();
                return false;
            }
        } else {
            try {
                return serialPort.writeBytes(bytes);
            } catch (SerialPortException ex) {
                ex.printStackTrace();
                return false;
            }
        }

    }

    public byte[] read(int byteCount, boolean flagCRC) {
        if(flagCRC) {
            try {
                byte[] in = serialPort.readBytes(byteCount);
                return crcDecoding(in);
            } catch (SerialPortException ex) {
                ex.printStackTrace();
                return null;
            }
        } else {
            try {
                byte[] in = serialPort.readBytes(byteCount);
                return in;
            } catch (SerialPortException ex) {
                ex.printStackTrace();
                return null;
            }
        }
    }

    public void setParams(int baudRate, int dataBits, int stopBits, int parity) {
        try {
            serialPort.setParams(baudRate, dataBits, stopBits, parity);
        } catch (SerialPortException ex) {
            ex.printStackTrace();
        }
    }

    public void addListener(SerialPortEventListener listener) {
        try {
            serialPort.setEventsMask(SerialPort.MASK_RXCHAR);
            serialPort.addEventListener(listener);
        } catch (SerialPortException ex) {
            ex.printStackTrace();
        }
    }

    private static String toBinary( byte[] bytes ) {
        StringBuilder sb = new StringBuilder(bytes.length * Byte.SIZE);
        for( int i = 0; i < Byte.SIZE * bytes.length; i++ )
            sb.append((bytes[i / Byte.SIZE] << i % Byte.SIZE & 0x80) == 0 ? '0' : '1');
        return sb.toString();
    }

    private static byte[] fromBinary( String s ) {
        int sLen = s.length();
        byte[] toReturn = new byte[(sLen + Byte.SIZE - 1) / Byte.SIZE];
        char c;
        for (int i = 0; i < sLen; i++ )
            if( (c = s.charAt(i)) == '1' )
                toReturn[i / Byte.SIZE] = (byte) (toReturn[i / Byte.SIZE] | (0x80 >>> (i % Byte.SIZE)));
            else if ( c != '0' )
                throw new IllegalArgumentException();
        return toReturn;
    }

    private byte[] encapsulate(byte[] raw) {
        if(raw == null) return null;
        String resString = toBinary(raw).replaceAll("11111","111110");
        resString = "01111110" + resString;
        return fromBinary(resString);
    }

    private byte[] decapsulate(byte[] complex) {
        if(complex == null) return null;
        String temp = toBinary(complex);
        int start = temp.indexOf("01111110");
        if (start >= 0) {
            temp = temp.substring(start + 8);
            temp = temp.replaceAll("111110", "11111");
            return fromBinary(temp);
        }
        else {
            return null;
        }
    }

    public static byte[] crcCoding(byte[] bytes) {
        String raw = toBinary(bytes);
        raw = raw + getMod(raw + "000","1101");
        return fromBinary(raw);
    }

    public static byte[] crcDecoding(byte[] bytes) {
        String s = toBinary(bytes);
        s = s.substring(0, s.length() - 5);
        s = repair(s);
        return fromBinary(s.substring(0, s.length() - 3));
    }

    public static String getMod(String raw, String div) {
        StringBuilder sb = new StringBuilder(raw);
        for (int i = 0; i <= raw.length() - div.length(); i++) {
            String s = divide(sb.substring(i, i + div.length()),div);
            if (s != null) {
                sb.delete(i, i + div.length());
                sb.insert(i, s);
            }
        }
        return sb.substring(sb.length() - 3);
    }

    public static String divide(String s1, String s2) {
        if (s1.length() == s2.length()) {
            if (s1.charAt(0) == '1') {
                return xorString(s1,s2);
            }
        }
        return null;
    }

    public static String xorString(String s1, String s2) {
        StringBuilder sb = new StringBuilder();
        int len = s1.length() > s2.length()? s2.length() : s1.length();
        for (int i = 0; i < len; i++) {
            if (s1.charAt(i) != s2.charAt(i)) {
                sb.append('1');
            }
            else {
                sb.append('0');
            }
        }
        return sb.toString();
    }

    public static String repair(String raw) {
        String mod = getMod(raw,"1101");
        if (countOfOne(mod) > 1) {
            return shiftRight(repair(shiftLeft(raw)));
        }
        else {
            return xorEndsString(mod,raw);
        }
    }

    public static int countOfOne(String raw) {
        int count = 0;
        for (int i = 0; i < raw.length(); i++) {
            if (raw.charAt(i) == '1') {
                count++;
            }
        }
        return count;
    }

    public static String xorEndsString(String s1, String s2) {
        StringBuilder sb1 = new StringBuilder(s1);
        StringBuilder sb2 = new StringBuilder(s2);
        int delta;
        if (sb1.length() > sb2.length()) {
            delta = sb1.length() - sb2.length();
            for (int i = 0; i < delta; i++) {
                sb2.insert(0,'0');
            }
        }
        else {
            delta = sb2.length() - sb1.length();
            for (int i = 0; i < delta; i++) {
                sb1.insert(0,'0');
            }
        }
        return xorString(sb1.toString(), sb2.toString());
    }

    public static String shiftLeft(String raw) {
        StringBuilder res = new StringBuilder(raw);
        char temp = res.charAt(0);
        res.deleteCharAt(0);
        res.append(temp);
        return res.toString();
    }

    public static String shiftRight(String raw) {
        StringBuilder res = new StringBuilder(raw);
        char temp = res.charAt(res.length() - 1);
        res.deleteCharAt(res.length() - 1);
        res.insert(0,temp);
        return res.toString();
    }

}
