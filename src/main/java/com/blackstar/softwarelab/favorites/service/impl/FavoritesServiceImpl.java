package com.blackstar.softwarelab.favorites.service.impl;

import com.blackstar.softwarelab.favorites.entity.Favorites;
import com.blackstar.softwarelab.favorites.mapper.FavoritesMapper;
import com.blackstar.softwarelab.favorites.service.IFavoritesService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author blackstar
 * @since 2020-04-10
 */
@Service
public class FavoritesServiceImpl extends ServiceImpl<FavoritesMapper, Favorites> implements IFavoritesService {

}
