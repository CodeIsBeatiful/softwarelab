package com.softwarelab.application.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.softwarelab.application.bean.HardwareInfo;
import com.softwarelab.application.checker.HardwareChecker;
import com.softwarelab.application.entity.Instance;
import com.softwarelab.application.service.IAppService;
import com.softwarelab.application.service.IAppSourceService;
import com.softwarelab.application.service.IInstanceService;
import com.softwarelab.application.common.BaseController;
import com.softwarelab.application.common.DbConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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

    @Autowired
    private IAppSourceService appSourceService;

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
    public Map<String,Object> getSoftwareInfo() {

        HashMap<String, Object> map = new HashMap<>();
        map.put("appStoreVersion",appSourceService.list().get(0).getVersion());
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
