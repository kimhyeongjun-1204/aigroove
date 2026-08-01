package com.game4men.aigroove.admin.service;

import org.springframework.stereotype.Service;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import com.sun.management.OperatingSystemMXBean;

@Service
public class ServerStatusSvc {
    
    public Map<String, Object> getServerStatus() {
        Map<String, Object> status = new HashMap<>();
        
        // CPU 사용량
        OperatingSystemMXBean osBean =  (OperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean();
        double cpuLoad = osBean.getSystemCpuLoad() * 100;
        status.put("cpu", Math.round(cpuLoad));
        
        // 메모리 사용량
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        long usedMemory = memoryBean.getHeapMemoryUsage().getUsed();
        long maxMemory = memoryBean.getHeapMemoryUsage().getMax();
        double memoryUsage = (double) usedMemory / maxMemory * 100;
        status.put("memory", Math.round(memoryUsage));
        
        // 디스크 사용량
        File root = new File("/");
        long totalSpace = root.getTotalSpace();
        long usableSpace = root.getUsableSpace();
        double diskUsage = ((double) (totalSpace - usableSpace) / totalSpace) * 100;
        status.put("disk", Math.round(diskUsage));
        
        // 서버 가동 시간
        long uptime = ManagementFactory.getRuntimeMXBean().getUptime();
        long days = uptime / (1000 * 60 * 60 * 24);
        long hours = (uptime % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        String uptimeStr = String.format("%dday %dhours", days, hours);
        status.put("uptime", uptimeStr);
        
        return status;
    }
}
