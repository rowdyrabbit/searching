name := """ebay-search-scraper"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  javaJdbc,
  javaCore,
  javaEbean,
  cache,
  javaWs,
  "org.springframework" % "spring-context" % "3.2.2.RELEASE",
  "javax.inject" % "javax.inject" % "1","org.springframework" % "spring-context" % "3.2.2.RELEASE",
  "org.springframework.data" % "spring-data-jpa" % "1.3.2.RELEASE",
  "org.springframework" % "spring-expression" % "3.2.2.RELEASE",
  "org.mockito" % "mockito-core" % "1.9.5" % "test",
  "org.powermock" % "powermock-module-junit4" % "1.5.1" % "test",
  "org.powermock" % "powermock-api-mockito" % "1.5.1" % "test"
)

libraryDependencies += "org.jsoup" % "jsoup" % "1.7.2"

