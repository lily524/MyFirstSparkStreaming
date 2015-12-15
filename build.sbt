import AssemblyKeys._

assemblySettings

name := "MyFirstSparkStreaming"

version := "0.1"

scalaVersion := "2.11.5"

resolvers += "Cloudera Repos" at
"https://repository.cloudera.com/artifactory/cloudera-repos/"

libraryDependencies ++= Seq(
    "org.apache.spark" % "spark-streaming_2.10" % "1.2.0",
    "org.apache.spark" % "spark-core_2.10" % "1.2.0" % "provided"
)

javaOptions in compile ++= Seq("-source", "1.7", "-target", "1.7")

mainClass in (Compile, run) := Some("NetworkWordCount")

mainClass in assembly := Some("NetworkWordCount")

jarName in assembly := "NetworkWordCount.jar"

mergeStrategy in assembly <<= (mergeStrategy in assembly) { mergeStrategy => {
 case entry => {
   val strategy = mergeStrategy(entry)
   if (strategy == MergeStrategy.deduplicate) MergeStrategy.first
   else strategy
 }
}}
