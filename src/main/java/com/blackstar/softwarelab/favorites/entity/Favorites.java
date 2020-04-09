package com.blackstar.softwarelab.favorites.entity;

import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author blackstar
 * @since 2020-04-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Favorites implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private String userId;

    private String appId;

    private String additionalInfo;


}
