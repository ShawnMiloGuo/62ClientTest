# 代码说明
## 用途
该程序是用于远程调用中国科学院先进技术研究院发布的卫星影像深度学习程序。

## 文件说明
### 该目录树如下：
```bash
.
|-- ClientForTile_linux.sh #如何客户端是linux用这个调用
|-- ClientForTile_win.bat  #如何客户端是windows用这个调用
|-- ClientTest-1.0.jar #客户端主程序
|-- dlpredictonline-0.0.1-SNAPSHOT.jar #使用的架构包
|-- lib #架构包需要的依赖项
|   |-- animal-sniffer-annotations-1.17.jar
|   |-- checker-compat-qual-2.5.2.jar
|   |-- dlpredictonline-0.0.1-SNAPSHOT.jar
|   |-- error_prone_annotations-2.2.0.jar
|   |-- fastjson-1.2.53.jar
|   |-- grpc-context-1.17.1.jar
|   |-- grpc-core-1.17.1.jar
|   |-- grpc-netty-1.17.1.jar
|   |-- grpc-protobuf-1.17.1.jar
|   |-- grpc-protobuf-lite-1.17.1.jar
|   |-- grpc-stub-1.17.1.jar
|   |-- gson-2.7.jar
|   |-- guava-26.0-android.jar
|   |-- hamcrest-core-1.3.jar
|   |-- j2objc-annotations-1.1.jar
|   |-- jsr305-3.0.2.jar
|   |-- log4j-1.2.17.jar
|   |-- log4j-api-2.3.jar
|   |-- log4j-core-2.3.jar
|   |-- log4j-slf4j-impl-2.3.jar
|   |-- netty-buffer-4.1.30.Final.jar
|   |-- netty-codec-4.1.30.Final.jar
|   |-- netty-codec-http2-4.1.30.Final.jar
|   |-- netty-codec-http-4.1.30.Final.jar
|   |-- netty-codec-socks-4.1.30.Final.jar
|   |-- netty-common-4.1.30.Final.jar
|   |-- netty-handler-4.1.30.Final.jar
|   |-- netty-handler-proxy-4.1.30.Final.jar
|   |-- netty-resolver-4.1.30.Final.jar
|   |-- netty-transport-4.1.30.Final.jar
|   |-- opencensus-api-0.17.0.jar
|   |-- opencensus-contrib-grpc-metrics-0.17.0.jar
|   |-- protobuf-java-3.3.1.jar
|   |-- proto-google-common-protos-1.0.0.jar
|   |-- slf4j-api-1.7.12.jar
|   |-- slf4j-log4j12-1.7.21.jar
|   `-- tensorflow-client-1.4-1.jar
`-- Readme.md #本文档
```
## 调用代码说明
### Linux
```bash
dir=lib
libs=""
for file in $dir/*; do
    libs=$libs:$file
done

libs=ClientTest-1.0.jar:$libs
java -classpath $libs ClientTest.WpaiClient 210.75.252.106 10151 4 /home/guoshanxin/Work/TestTiles/ /home/guoshanxin/code/ClientTest/target/result false
```
其中参数列表如下：
1. IP地址
2. 端口
3. 调用的深度学习模型ID
4. 本地的切片数据根目录。
5. 返回结果的存储路径
6. 是否将本地数据传输给服务器处理，如果为fales：不发送数据到服务器，只发送文件名加快速度； 如果为ture：传输本地数据到服务器

#### 特别注意 ：
``` 
当参数6为false时，参数4的最后一级目录需要与服务器上的数据文件夹名称保持一致，例如上面的例子中，表示调用服务器上的数据名称为TestTiles
```
### Windows
```bash
java -classpath ClientTest-1.0.jar;dlpredictonline-0.0.1-SNAPSHOT.jar;\lib\*.jar ClientTest.WpaiClient 210.75.252.106 10151 4 E:\Result\Geoserver\Code\06_DockerClient\TestTiles E:\Result\Geoserver\Code\06_DockerClient\Result false
```
其中参数顺序与linux中的含义是一致的。

# 服务列表
``` bash
# this file reconds the all available deeplearning network for Land cover classification
# taskId = Max Time, IP : Port # explations

1=1000,10.10.1.62:8801 # bareland GF2-NRG Unet
2=1000,10.10.1.62:8802 # bareland TM-NRG Unet
3=1000,10.10.1.62:8803 # city GF1-RGB SegNet
4=1000,10.10.1.62:8804 # farmland GF1_All HRUNet
5=1000,10.10.1.62:8805 # farmland GF2_ALL HRUNet
6=1000,10.10.1.62:8806 # farmland TM_ALL HRUNet
7=1000,10.10.1.62:8807 # forest GF1_All HRUNet
8=1000,10.10.1.62:8808 # forest GF2_NRG Unet
9=1000,10.10.1.62:8809 # garden GF1_All HRUNet
10=1000,10.10.1.62:8810 # garden GF2_NRG Unet
11=1000,10.10.1.62:8811 # grassland GF1_All HRUNet
12=1000,10.10.1.62:8812 # grassland GF2_NRG Unet
13=1000,10.10.1.62:8813 # grassland TM_NRG Unet
14=1000,10.10.1.62:8814 # road GF1_RGB Unet
15=1000,10.10.1.62:8815 # water GF1_All HRUNet
16=1000,10.10.1.62:8816 # water TM_NRG UNet
```