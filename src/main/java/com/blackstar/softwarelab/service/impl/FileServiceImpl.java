package com.blackstar.softwarelab.service.impl;

import com.blackstar.softwarelab.entity.File;
import com.blackstar.softwarelab.mapper.FileMapper;
import com.blackstar.softwarelab.service.IFileService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author blackstar
 * @since 2020-06-26
 */
@Service
public class FileServiceImpl extends ServiceImpl<FileMapper, File> implements IFileService {

}
