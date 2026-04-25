# sherpa-ncnn-android

[![](https://jitpack.io/v/shiyunjin/sherpa-ncnn.svg)](https://jitpack.io/#shiyunjin/sherpa-ncnn)

Android library wrapper for [sherpa-ncnn](https://github.com/k2-fsa/sherpa-ncnn) — a speech recognition framework based on NCNN.

This repo uses a git submodule to track the upstream project and builds an AAR via JitPack with 16KB page-aligned native libraries.

## Usage

### Step 1. Add the JitPack repository

Add it in your `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

### Step 2. Add the dependency

```kotlin
dependencies {
    implementation("com.github.shiyunjin:sherpa-ncnn:Tag")
}
```

Replace `Tag` with the desired version (see [releases](https://jitpack.io/#shiyunjin/sherpa-ncnn)).

### Step 3. Use the API

```kotlin
import com.k2fsa.sherpa.ncnn.SherpaNcnn
import com.k2fsa.sherpa.ncnn.RecognizerConfig
import com.k2fsa.sherpa.ncnn.FeatureExtractorConfig
import com.k2fsa.sherpa.ncnn.ModelConfig
import com.k2fsa.sherpa.ncnn.DecoderConfig

val featConfig = FeatureExtractorConfig(sampleRate = 16000f, featureDim = 80)
val modelConfig = ModelConfig(
    encoderParam = "model/encoder_jit_trace-pnnx.ncnn.param",
    encoderBin = "model/encoder_jit_trace-pnnx.ncnn.bin",
    decoderParam = "model/decoder_jit_trace-pnnx.ncnn.param",
    decoderBin = "model/decoder_jit_trace-pnnx.ncnn.bin",
    joinerParam = "model/joiner_jit_trace-pnnx.ncnn.param",
    joinerBin = "model/joiner_jit_trace-pnnx.ncnn.bin",
    tokens = "model/tokens.txt",
)
val decoderConfig = DecoderConfig(
    method = "greedy_search",
    numActivePaths = 4,
)
val config = RecognizerConfig(
    featConfig = featConfig,
    modelConfig = modelConfig,
    decoderConfig = decoderConfig,
    enableEndpoint = true,
    rule1MinTrailingSilence = 2.4f,
    rule2MinTrailingSilence = 1.0f,
    rule3MinUtteranceLength = 30.0f,
)

val recognizer = SherpaNcnn(config)
recognizer.acceptSamples(samples)
if (recognizer.isReady()) {
    recognizer.decode()
}
val text = recognizer.text
```

## Building Locally

```bash
# Clone with submodules
git clone --recurse-submodules https://github.com/shiyunjin/sherpa-ncnn.git

# Build AAR (all ABIs)
./gradlew :sherpa-ncnn:assembleRelease

# Build AAR (specific ABI only)
./gradlew :sherpa-ncnn:assembleRelease -PSHERPA_NCNN_ABIS=arm64-v8a

# Publish to local Maven
./gradlew :sherpa-ncnn:publishToMavenLocal
```

## Supported ABIs

`arm64-v8a` · `armeabi-v7a` · `x86_64` · `x86`

Default: all four. Override with `-PSHERPA_NCNN_ABIS=arm64-v8a` etc.

## 16KB Page Alignment

Native libraries are built with NDK r28+ and linked with `-Wl,-z,max-page-size=16384` to ensure 16KB ELF segment alignment, combined with AGP 9.x zip alignment for full compliance with Android 15+ requirements.
