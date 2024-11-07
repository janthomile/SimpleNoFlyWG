plugins {
    id("java")
}

group = "supermemnon.simplenofly"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation(files("../spigot-api-1.16.5-R0.1-20210604.221538-95-shaded.jar"))
    compileOnly(files("../worldedit-bukkit-7.2.17.jar"))
    compileOnly(files("../worldguard-bukkit-7.0.5-dist.jar"))
}

tasks.test {
    useJUnitPlatform()
}