import com.github.robfletcher.compass.CompassExtension

buildscript {

  extra["kotlinVersion"] = "1.1-M02"

  repositories {
    maven { setUrl("http://dl.bintray.com/kotlin/kotlin-eap-1.1")}
    jcenter()
    maven { setUrl("http://dl.bintray.com/robfletcher/gradle-plugins") }
    mavenCentral()
  }
  dependencies {
    classpath(kotlinModule("gradle-plugin", extra["kotlinVersion"] as String))
    classpath("com.github.robfletcher:compass-gradle-plugin:2.0.6")
  }
}

apply {
  plugin("kotlin")
  plugin("application")
  plugin("com.github.robfletcher.compass")
}

group = "io.projectreactor"
version = "1.0.0.BUILD-SNAPSHOT"

configure<ApplicationPluginConvention> {
    mainClassName = "io.projectreactor.Application"
}

configure<CompassExtension> {
  sassDir = file("$projectDir/src/main/sass")
  cssDir = file("$projectDir/src/main/resources/static/assets/css")
}

repositories {
  mavenCentral()
  maven { setUrl("http://dl.bintray.com/kotlin/kotlin-eap-1.1") }
  maven { setUrl("http://repo.spring.io/libs-milestone") }
  maven { setUrl("https://repo.spring.io/snapshot") }
}

dependencies {
  compile("org.springframework:spring-web-reactive:5.0.0.BUILD-SNAPSHOT")
  // TODO Remove the spring-context-support dependency when the extended mime types will be moved in spring-core or spring-web
  compile("org.springframework:spring-context-support:5.0.0.BUILD-SNAPSHOT")
  compile("io.projectreactor.ipc:reactor-netty:0.5.2.RELEASE")
  compile(kotlinModule("stdlib", extra["kotlinVersion"] as String))
}
