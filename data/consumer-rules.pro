# Gson이 사용하는 DTO 필드와 기본 생성 경로만 보존합니다.
-keep,allowobfuscation class com.dimje.data.remote.model.WorryResponseRequest
-keep,allowobfuscation class com.dimje.data.remote.model.WorryResponseDto
-keepclassmembers,allowobfuscation class com.dimje.data.remote.model.** {
    @com.google.gson.annotations.SerializedName <fields>;
}
