package com.softwarelab.application.common;

/**
 * container status constant, follow docker doc description
 * https://docs.docker.com/engine/reference/commandline/ps/
 */
public class ContainerStatusConst {


    public static final String CREATED = "created";

    public static final String RESTARTING = "restarting";

    public static final String RUNNING = "running";

    public static final String REMOVING = "removing";

    public static final String PAUSED = "paused";

    public static final String EXITED = "exited";

    public static final String DEAD = "dead";
}
