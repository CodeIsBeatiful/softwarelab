package com.blackstar.softwarelab.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.blackstar.softwarelab.bean.HardwareInfo;
import com.blackstar.softwarelab.checker.HardwareChecker;
import com.blackstar.softwarelab.common.BaseController;
import com.blackstar.softwarelab.common.DbConst;
import com.blackstar.softwarelab.entity.Instance;
import com.blackstar.softwarelab.service.IAppService;
import com.blackstar.softwarelab.service.IInstanceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import oshi.SystemInfo;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author blackstar
 * @since 2020-09-05
 */
@Slf4j
@RestController
@RequestMapping("/api/statistics")
public class StatisticsController extends BaseController {

    @Autowired
    private IInstanceService instanceService;

    @Autowired
    private IAppService appService;


    @Autowired
    private HardwareChecker hardwareChecker;

    public StatisticsController() {
    }


    @RequestMapping(method = RequestMethod.GET, value = "/hardware/history")
    public List<HardwareInfo> getHistoryHardwareInfo() {
        return hardwareChecker.getList();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/hardware/last")
    public HardwareInfo getLastHardwareInfo() {
        return hardwareChecker.getLast();
    }

    @RequestMapping(method = RequestMethod.GET, value = "os")
    public Map<String,String> getOsInfo() {
        HashMap<String, String> map = new HashMap<>();
        Properties props = System.getProperties();
        map.put("name",props.getProperty("os.name"));
        return map;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/software")
    public Map<String,Integer> getSoftwareInfo() {

        HashMap<String, Integer> map = new HashMap<>();
        map.put("appTotal",appService.count());
        map.put("instanceTotal",instanceService.count());
        map.put("runningInstanceTotal",instanceService.count(new QueryWrapper<Instance>().eq(DbConst.COLUMN_RUNNING_STATUS, DbConst.RUNNING_STATUS_START)));
        return map;
    }

    public static String formatByte(long byteNumber) {
        //换算单位
        double FORMAT = 1024.0;
        double kbNumber = byteNumber / FORMAT;
        if (kbNumber < FORMAT) {
            return new DecimalFormat("#.##KB").format(kbNumber);
        }
        double mbNumber = kbNumber / FORMAT;
        if (mbNumber < FORMAT) {
            return new DecimalFormat("#.##MB").format(mbNumber);
        }
        double gbNumber = mbNumber / FORMAT;
        if (gbNumber < FORMAT) {
            return new DecimalFormat("#.##GB").format(gbNumber);
        }
        double tbNumber = gbNumber / FORMAT;
        return new DecimalFormat("#.##TB").format(tbNumber);
    }
}
