import java.io.FileInputStream
import java.util.Properties

plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "thick2.truongthimylinh.mywallet"
    compileSdk = 36

    defaultConfig {
        applicationId = "thick2.truongthimylinh.mywallet"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // GIẢI PHÁP AN TOÀN TUYỆT ĐỐI CHO KOTLIN DSL
        val properties = Properties()
        val propertiesFile = project.rootProject.file("local.properties")

        if (propertiesFile.exists()) {
            // Sử dụng FileInputStream truyền thống giúp trình biên dịch không bị rối kiểu dữ liệu (Type Parameter 'R')
            val inputStream = FileInputStream(propertiesFile)
            properties.load(inputStream)
            inputStream.close()
        }

        // Đọc giá trị từ file cấu hình, nếu trống sẽ mặc định là chuỗi rỗng bọc trong dấu nháy kép
        val geminiApiKey = properties.getProperty("GEMINI_API_KEY") ?: "\"\""
        buildConfigField("String", "GEMINI_API_KEY", geminiApiKey)
    }

    // Bật tính năng tự động tạo lớp BuildConfig
    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    // 1. Thư viện OCR của Google ML Kit (Nhận diện văn bản trên thiết bị)
    implementation("com.google.mlkit:text-recognition:16.0.0")

    // 2. Thư viện OkHttp để gọi API gửi văn bản lên Gemini AI xử lý
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    // 3. Hệ sinh thái Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.1.2"))
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-auth")

    // 4. Các thư viện giao diện và thành phần bổ trợ
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation(libs.constraintlayout)
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("com.github.PhilJay:MPAndroidChart:3.1.0")
}