import com.acare.clinic.agent.dlp.MaskingUtil

fun main() {
    val text1 = "BN: Nguyen Van A - 034123456789 | Email: test@gmail.com | CCCD: 123456789012"
    println("Original: " + text1)
    println("Masked: " + MaskingUtil.mask(text1))
}
