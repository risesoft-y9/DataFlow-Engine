package net.risedata.rpc.consumer.model;
/**
* @description: 包装了port 和host 的信息
* @Author lb176
* @Date 2021/4/29==15:16
*/
public class PortAndHost {

    private String host;
    private int port;

    public PortAndHost(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }


    public int getPort() {
        return port;
    }

    @Override
    public String toString() {
        return "PortAndHost{" +
                "host='" + host + '\'' +
                ", port=" + port +
                '}';
    }
}
