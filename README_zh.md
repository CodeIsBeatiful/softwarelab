# 软件实验室

软件实验室是一个基于容器技术的SaaS应用，只需简单几步就可以获取各类基础组件



## 开发
### 准备
`Jdk1.8+`、`maven3.2+`、`Docker CE`

## 安装
当前支持MacOS、Linux(CentOS),计划后期支持Windows
### 前提
安装java和DockerCE版本
### 开始
编译源码获取压缩包或直接下载安装包，进行解压
```
unzip /path/to/softwarelab-xx.zip -d /target/path
```
执行启动命令
```
./target/path/softwarelab/bin/startup.sh
```

## 使用
使用浏览器打开地址`http://ip:8080/`,默认用户名密码:`admin/123456`

//todo add video

### 重启
```
./target/path/softwarelab/bin/restart.sh
```

### 停止
```
./target/path/softwarelab/bin/shutdown.sh
```

### 查看日志
```
tail -f /target/path/softwarelab-xx.log
```

### FAQ
//todo

## 未来计划
//todo


