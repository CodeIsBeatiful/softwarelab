# Software Lab

The software laboratory is a SaaS application based on container technology, and all kinds of basic components can be obtained in a few simple steps

## Development

### Preparation

`Jdk1.8+`, `maven3.2+`, `Docker CE`

## Installation

Currently supports MacOS, Linux (CentOS), and plans to support Windows in the future

### Premise

Install java and DockerCE version

### Start

Compile the source code to get the compressed package or download the installation package directly to decompress

```

unzip /path/to/softwarelab-xx.zip -d /target/path

```

Execute start command

```

./target/path/softwarelab/bin/startup.sh

```

## Use

Use a browser to open the address `http://ip:8080/`, the default username and password: ʻadmin/123456`

//todo add video

### Restart

```

./target/path/softwarelab/bin/restart.sh

```

### stop

```

./target/path/softwarelab/bin/shutdown.sh

```

### View log

```

tail -f /target/path/softwarelab-xx.log

```

### FAQ

//todo

## Future plan

//todo