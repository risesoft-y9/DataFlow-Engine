package net.risedata.rpc.model;


import java.util.Arrays;

/**
 * 一条信息 用于解决粘包问题
 */
public class Msg {

    private int length;
    private byte[] msg;

    public Msg(int length, byte[] msg) {
        this.length = length;
        this.msg = msg;
    }

    public int getLength() {
        return length;
    }

    public byte[] getMsg() {
        return msg;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public void setMsg(byte[] msg) {
        this.msg = msg;
    }


    @Override
    public String toString() {
        return "Msg{" +
                "length=" + length +
                ", msg=" + Arrays.toString(msg) +
                '}';
    }
}
