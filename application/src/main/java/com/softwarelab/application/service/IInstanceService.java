package com.softwarelab.application.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.softwarelab.application.entity.Instance;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author blackstar
 * @since 2020-03-27
 */
public interface IInstanceService extends IService<Instance> {

    boolean start(String userId, String instanceId);

    void add(String userId, Instance instance);

    boolean stop(String userId, String id);

    boolean delete(String id);

    boolean startByInstance(Instance instance);
}
