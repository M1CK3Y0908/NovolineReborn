import java.io.IOException
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.security.MessageDigest
import java.time.Duration
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

plugins {
    java
    application
}

group = "wtf.novoline"
version = "1.0.0"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(fileTree("libs") {
        include("**/*.jar")
        exclude("**/gson-2.2.4.jar")
    })
    implementation("com.google.code.gson:gson:2.10.1")

    testImplementation(platform("org.junit:junit-bom:5.11.4"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

application {
    mainClass.set("net.minecraft.client.main.Main")
}

val runDirectory = layout.projectDirectory.dir("run")
val nativeRoot = layout.projectDirectory.dir("test_natives")
val nativePlatform = System.getProperty("os.name").lowercase().let {
    when {
        it.contains("win") -> "windows"
        it.contains("linux") -> "linux"
        else -> null
    }
}
val nativeDirectory = nativePlatform?.let(nativeRoot::dir)

tasks.named<JavaExec>("run") {
    group = "application"
    description = "Builds OpenNovoline and launches the Minecraft client with local assets and natives"
    workingDir(runDirectory)
    standardInput = System.`in`
    args(
        "--version", "1.8.9",
        "--accessToken", "0",
        "--username", "Player",
        "--gameDir", runDirectory.asFile.absolutePath,
        "--assetsDir", runDirectory.dir("assets").asFile.absolutePath,
        "--resourcePackDir", runDirectory.dir("resourcepacks").asFile.absolutePath,
        "--userProperties", "{}",
        "--profileProperties", "{}",
    )
    if (runDirectory.file("assets/indexes/1.8.json").asFile.isFile) {
        args("--assetIndex", "1.8")
    }
    doFirst {
        val natives = nativeDirectory?.asFile
        if (natives == null || !natives.isDirectory) {
            throw GradleException(
                "OpenNovoline does not have native libraries for OS '${System.getProperty("os.name")}'. " +
                    "Add them under test_natives/ and update the run task."
            )
        }
        runDirectory.asFile.mkdirs()
        runDirectory.dir("assets").asFile.mkdirs()
        runDirectory.dir("resourcepacks").asFile.mkdirs()
        jvmArgs(
            "-Dorg.lwjgl.librarypath=${natives.absolutePath}",
            "-Djava.library.path=${natives.absolutePath}",
        )
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release = 16
    options.compilerArgs.addAll(listOf("-Xlint:none", "-proc:none"))
}

val verifyLibs by tasks.registering {
    group = "verification"
    description = "libs/ 里每个 jar 的 SHA-256 必须与 libs/SHA256SUMS.txt 一致，且目录里没有未登记的 jar"
    val sums = layout.projectDirectory.file("libs/SHA256SUMS.txt")
    val libsRoot = layout.projectDirectory.dir("libs")
    val jars = fileTree("libs") { include("**/*.jar") }
    inputs.file(sums)
    inputs.files(jars)
    doLast {
        val expected = sums.asFile.readLines().filter { it.isNotBlank() }.associate {
            val parts = it.trim().split(Regex("\\s+"))
            parts[1] to parts[0]
        }
        val actual = jars.files.associate { file ->
            val digest = MessageDigest.getInstance("SHA-256").digest(file.readBytes())
            val relativePath = file.relativeTo(libsRoot.asFile).invariantSeparatorsPath
            relativePath to digest.joinToString("") { "%02x".format(it) }
        }
        val problems = mutableListOf<String>()
        for ((name, hash) in actual) {
            when (expected[name]) {
                null -> problems.add("未登记: $name")
                hash -> {}
                else -> problems.add("SHA-256 不符: $name")
            }
        }
        for (name in expected.keys - actual.keys) {
            problems.add("缺少: $name")
        }
        if (problems.isNotEmpty()) {
            throw GradleException("libs 校验失败:\n" + problems.joinToString("\n"))
        }
    }
}

tasks.named("compileJava") {
    dependsOn(verifyLibs)
}

tasks.jar {
    manifest {
        attributes(
            "Main-Class" to "net.minecraft.client.main.Main",
            "Implementation-Title" to "OpenNovoline",
            "Implementation-Version" to project.version,
        )
    }
}

val bundledJar by tasks.registering(Jar::class) {
    group = "build"
    description = "与原始 JAR 形态一致的单文件：业务类 + 资源 + libs 里的第三方类"
    archiveClassifier = "all"
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    isPreserveFileTimestamps = false
    isReproducibleFileOrder = true
    manifest {
        attributes(
            "Main-Class" to "net.minecraft.client.main.Main",
            "Multi-Release" to "true",
        )
    }
    from(sourceSets.main.get().output)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith(".jar") }.map { zipTree(it) }
    }) {
        exclude("META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA", "META-INF/MANIFEST.MF")
    }
}

tasks.test {
    useJUnitPlatform()
    maxHeapSize = "1g"
    testLogging {
        events("passed", "skipped", "failed")
        showStandardStreams = false
    }
    systemProperty("rebuild.projectDir", projectDir.absolutePath)
}

tasks.build {
    dependsOn(bundledJar)
}

val downloadAssets by tasks.registering {
    group = "run"
    description = "Fills run/assets/objects (sounds, language files, icons) from run/assets/indexes/1.8.json: copies from an installed Minecraft " +
        "(-Pnovoline.assetsDir=<assets dir>, default %APPDATA%/.minecraft/assets) or downloads from resources.download.minecraft.net"
    val index = layout.projectDirectory.file("run/assets/indexes/1.8.json")
    val objects = layout.projectDirectory.dir("run/assets/objects")
    val installed = providers.gradleProperty("novoline.assetsDir")
        .orElse(providers.environmentVariable("APPDATA").map { "$it/.minecraft/assets" })
        .orElse("")
    inputs.file(index)
    outputs.dir(objects)
    doLast {
        @Suppress("UNCHECKED_CAST")
        val entries = (groovy.json.JsonSlurper().parse(index.asFile) as Map<String, Any>)["objects"] as Map<String, Map<String, Any>>
        val wanted = entries.values.map { (it["hash"] as String) to (it["size"] as Number).toLong() }.distinct()
        val installedObjects = installed.get().takeIf { it.isNotBlank() }?.let { File(it, "objects") }?.takeIf { it.isDirectory }
        val client = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(20))
            .build()
        fun sha1(bytes: ByteArray) = MessageDigest.getInstance("SHA-1").digest(bytes).joinToString("") { "%02x".format(it) }
        val present = AtomicInteger()
        val copied = AtomicInteger()
        val downloaded = AtomicInteger()
        val failures = ConcurrentLinkedQueue<String>()
        val pool = Executors.newFixedThreadPool(8)
        for ((hash, size) in wanted) {
            pool.execute {
                try {
                    val target = objects.asFile.resolve(hash.take(2)).resolve(hash)
                    if (target.isFile && target.length() == size) {
                        present.incrementAndGet()
                        return@execute
                    }
                    target.parentFile.mkdirs()
                    val temp = File(target.parentFile, "$hash.tmp")
                    val local = installedObjects?.resolve(hash.take(2))?.resolve(hash)
                    if (local != null && local.isFile && sha1(local.readBytes()) == hash) {
                        local.copyTo(temp, overwrite = true)
                        Files.move(temp.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING)
                        copied.incrementAndGet()
                        return@execute
                    }
                    var lastError = "no attempt"
                    for (attempt in 1..3) {
                        try {
                            val request = HttpRequest.newBuilder(URI.create("https://resources.download.minecraft.net/${hash.take(2)}/$hash"))
                                .timeout(Duration.ofSeconds(60))
                                .build()
                            val response = client.send(request, HttpResponse.BodyHandlers.ofByteArray())
                            if (response.statusCode() != 200) {
                                lastError = "HTTP ${response.statusCode()}"
                            } else if (sha1(response.body()) != hash) {
                                lastError = "SHA-1 mismatch"
                            } else {
                                temp.writeBytes(response.body())
                                Files.move(temp.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING)
                                downloaded.incrementAndGet()
                                return@execute
                            }
                        } catch (e: IOException) {
                            lastError = e.toString()
                        }
                    }
                    failures.add("$hash: $lastError")
                } catch (e: Exception) {
                    failures.add("$hash: $e")
                }
            }
        }
        pool.shutdown()
        pool.awaitTermination(2, TimeUnit.HOURS)
        logger.lifecycle("assets: ${wanted.size} objects, ${present.get()} already present, ${copied.get()} copied from ${installedObjects ?: "(no installed Minecraft)"}, ${downloaded.get()} downloaded")
        if (failures.isNotEmpty()) {
            throw GradleException("${failures.size} assets could not be fetched:\n" + failures.take(10).joinToString("\n"))
        }
    }
}
