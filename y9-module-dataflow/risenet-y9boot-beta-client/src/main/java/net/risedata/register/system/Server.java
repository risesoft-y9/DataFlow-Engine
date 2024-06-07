package net.risedata.register.system;

import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.NumberUtil;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.CentralProcessor.TickType;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;
import oshi.util.Util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

/**
 * 主机信息
 */
public class Server {
    private static final int OSHI_WAIT_SECOND = 1000;

    /**
     * CPU信息
     */
    private Cpu cpu = new Cpu();

    /**
     * 內存信息
     */
    private Mem mem = new Mem();

    /**
     * 系统信息
     */
    private System sys = new System();

    /**
     * 磁盘信息
     */
    private List<Disk> disks = new LinkedList<Disk>();

    public Cpu getCpu() {
        return cpu;
    }

    public void setCpu(Cpu cpu) {
        this.cpu = cpu;
    }

    public Mem getMem() {
        return mem;
    }

    public void setMem(Mem mem) {
        this.mem = mem;
    }

    public System getSys() {
        return sys;
    }

    public void setSys(System sys) {
        this.sys = sys;
    }

    public List<Disk> getDisks() {
        return disks;
    }

    public void setDisks(List<Disk> disks) {
        this.disks = disks;
    }

    private static final SystemInfo si = new SystemInfo();
    private static HardwareAbstractionLayer hal;

    static {
        hal = si.getHardware();
    }

    public synchronized void serverInfo() throws Exception {
        setCpuInfo(hal.getProcessor());
        setMemInfo(hal.getMemory());
        setSysInfo();
        setDisksInfo(si.getOperatingSystem());
    }

    public void refreshCpu() {
        setCpuInfo(hal.getProcessor());
    }

    public void refreshMem() {
        setMemInfo(hal.getMemory());
    }

    public void refreshSys() {
        setSysInfo();
    }

    public void refreshDisks() {
        setDisksInfo(si.getOperatingSystem());
    }

    /**
     * 设置CPU信息
     */
    private void setCpuInfo(CentralProcessor processor) {
        // CPU信息
        long[] prevTicks = processor.getSystemCpuLoadTicks();
        Util.sleep(OSHI_WAIT_SECOND);
        long[] ticks = processor.getSystemCpuLoadTicks();
        long nice = ticks[TickType.NICE.getIndex()] - prevTicks[TickType.NICE.getIndex()];
        long irq = ticks[TickType.IRQ.getIndex()] - prevTicks[TickType.IRQ.getIndex()];
        long softirq = ticks[TickType.SOFTIRQ.getIndex()] - prevTicks[TickType.SOFTIRQ.getIndex()];
        long steal = ticks[TickType.STEAL.getIndex()] - prevTicks[TickType.STEAL.getIndex()];
        long cSys = ticks[TickType.SYSTEM.getIndex()] - prevTicks[TickType.SYSTEM.getIndex()];
        long user = ticks[TickType.USER.getIndex()] - prevTicks[TickType.USER.getIndex()];
        long iowait = ticks[TickType.IOWAIT.getIndex()] - prevTicks[TickType.IOWAIT.getIndex()];
        long idle = ticks[TickType.IDLE.getIndex()] - prevTicks[TickType.IDLE.getIndex()];
        long totalCpu = user + nice + cSys + idle + iowait + irq + softirq + steal;
        cpu.setCpuNum(processor.getLogicalProcessorCount());
        cpu.setTotal(totalCpu);
        cpu.setSys(cSys);
        cpu.setUsed(user);
        cpu.setWait(iowait);
        cpu.setFree(idle);
    }

    /**
     * 设置内存信息
     */
    private void setMemInfo(GlobalMemory memory) {
        mem.setTotal(memory.getTotal());
        mem.setUsed(memory.getTotal() - memory.getAvailable());
        mem.setFree(memory.getAvailable());
    }

    /**
     * 设置服务器信息
     */
    private void setSysInfo() {
        InetAddress addr = null;
        try {
            addr = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        Properties props = java.lang.System.getProperties();
        assert addr != null;
        sys.setComputerName(addr.getHostName());
        sys.setComputerIp(NetUtil.getLocalhostStr());
        sys.setOsName(props.getProperty("os.name"));
        sys.setOsArch(props.getProperty("os.arch"));
        sys.setUserDir(props.getProperty("security.dir"));
    }

    /**
     * 设置磁盘信息
     */
    private void setDisksInfo(OperatingSystem os) {
        FileSystem fileSystem = os.getFileSystem();
        disks.clear();
        OSFileStore[] fsArray = fileSystem.getFileStores();
        for (OSFileStore fs : fsArray) {
            long free = fs.getUsableSpace();
            long total = fs.getTotalSpace();
            long used = total - free;
            Disk disk = new Disk();
            disk.setDirName(fs.getMount());
            disk.setSysTypeName(fs.getType());
            disk.setTypeName(fs.getName());
            disk.setTotal(convertFileSize(total));
            disk.setFree(convertFileSize(free));
            disk.setUsed(convertFileSize(used));
            disk.setUsage(NumberUtil.mul(NumberUtil.div(used, total, 4), 100));
            disks.add(disk);
        }
    }

    /**
     * 字节转换
     */
    public String convertFileSize(long size) {
        long kb = 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;
        if (size >= gb) {
            return String.format("%.1f GB", (float) size / gb);
        } else if (size >= mb) {
            float f = (float) size / mb;
            return String.format(f > 100 ? "%.0f MB" : "%.1f MB", f);
        } else if (size >= kb) {
            float f = (float) size / kb;
            return String.format(f > 100 ? "%.0f KB" : "%.1f KB", f);
        } else {
            return String.format("%d B", size);
        }
    }


}
