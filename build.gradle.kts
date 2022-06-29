plugins {
    val kotlinVersion = "1.5.10"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion

    id("net.mamoe.mirai-console") version "2.11.1"
}

group = "com.cytern"
version = "1.0-demo"

repositories {
    maven("https://maven.aliyun.com/repository/public")
    mavenCentral()
}

dependencies {
    // https://mvnrepository.com/artifact/com.alibaba/fastjson
    implementation("com.alibaba:fastjson:2.0.8")
    // https://mvnrepository.com/artifact/cn.hutool/hutool-all
    implementation("cn.hutool:hutool-all:5.8.4")

    // https://mvnrepository.com/artifact/ws.schild/jave-all-deps
    // https://mvnrepository.com/artifact/ws.schild/jave-all-deps
    implementation("ws.schild:jave-all-deps:2.6.0")

    // https://mvnrepository.com/artifact/com.tencentcloudapi/tencentcloud-sdk-java
    implementation("com.tencentcloudapi:tencentcloud-sdk-java:4.0.11")

}
