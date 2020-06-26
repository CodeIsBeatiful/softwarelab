package com.blackstar.softwarelab.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.blackstar.softwarelab.entity.Instance;
import com.blackstar.softwarelab.security.SecurityUser;

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

    void add(String userId, Instance instance);

    boolean stop(SecurityUser securityUser, String id);

    boolean delete(String id);
}
