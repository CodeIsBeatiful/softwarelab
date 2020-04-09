package com.blackstar.softwarelab.instance.service;

import com.blackstar.softwarelab.instance.entity.Instance;
import com.baomidou.mybatisplus.extension.service.IService;
import com.blackstar.softwarelab.security.SecurityUser;
import com.blackstar.softwarelab.user.entity.SysUser;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author blackstar
 * @since 2020-03-27
 */
public interface IInstanceService extends IService<Instance> {

    boolean start(String userId, String instanceId);

    void add(String userId,Instance instance);

    boolean stop(SecurityUser securityUser, String id);

    boolean delete(String id);
}
