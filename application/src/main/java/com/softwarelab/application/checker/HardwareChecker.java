package com.softwarelab.application.checker;

import com.softwarelab.application.bean.HardwareInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;

import javax.annotation.PostConstruct;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class HardwareChecker {

    private long sleepMillSeconds = 3000;

    private final LinkedList<HardwareInfo> hardwareInfos = new LinkedList();

    private final SystemInfo systemInfo = new SystemInfo();

    public HardwareInfo getLast() {
        return hardwareInfos.getLast();
    }

    public List<HardwareInfo> getList() {
        return hardwareInfos;
    }

    @PostConstruct
    public void init() {

        new Thread(() -> {
            try {
                for (; ; ) {
                    check();
                    TimeUnit.MILLISECONDS.sleep(sleepMillSeconds);
                }
            } catch (InterruptedException e) {
                log.error("checker Interrupted", e);
            }
        }, "checkHardwareThread").start();

    }

    private void check() {
        if (hardwareInfos.size() > 50) {
            hardwareInfos.removeFirst();
        }
        HardwareInfo hardwareInfo = getHardwareInfo();
        hardwareInfos.addLast(hardwareInfo);
    }

    private HardwareInfo getHardwareInfo() {

        CentralProcessor processor = systemInfo.getHardware().getProcessor();
        long[] prevTicks = processor.getSystemCpuLoadTicks();
        // sleep 500 mill seconds
        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            //do nothing
        }
        long ts = System.currentTimeMillis();

        long[] ticks = processor.getSystemCpuLoadTicks();
        long nice = ticks[CentralProcessor.TickType.NICE.getIndex()] - prevTicks[CentralProcessor.TickType.NICE.getIndex()];
        long irq = ticks[CentralProcessor.TickType.IRQ.getIndex()] - prevTicks[CentralProcessor.TickType.IRQ.getIndex()];
        long softirq = ticks[CentralProcessor.TickType.SOFTIRQ.getIndex()] - prevTicks[CentralProcessor.TickType.SOFTIRQ.getIndex()];
        long steal = ticks[CentralProcessor.TickType.STEAL.getIndex()] - prevTicks[CentralProcessor.TickType.STEAL.getIndex()];
        long cSys = ticks[CentralProcessor.TickType.SYSTEM.getIndex()] - prevTicks[CentralProcessor.TickType.SYSTEM.getIndex()];
        long user = ticks[CentralProcessor.TickType.USER.getIndex()] - prevTicks[CentralProcessor.TickType.USER.getIndex()];
        long iowait = ticks[CentralProcessor.TickType.IOWAIT.getIndex()] - prevTicks[CentralProcessor.TickType.IOWAIT.getIndex()];
        long idle = ticks[CentralProcessor.TickType.IDLE.getIndex()] - prevTicks[CentralProcessor.TickType.IDLE.getIndex()];
        long totalCpu = user + nice + cSys + idle + iowait + irq + softirq + steal;
        String cpu = new DecimalFormat("#.##").format(user * 100.0 / totalCpu);

        GlobalMemory globalMemory = systemInfo.getHardware().getMemory();
        long totalByte = globalMemory.getTotal();
        long availableByte = globalMemory.getAvailable();
        String memory = new DecimalFormat("#.##").format((totalByte - availableByte) * 100.0 / totalByte);

        return HardwareInfo.builder().ts(ts).cpu(cpu).memory(memory).build();
    }
}
