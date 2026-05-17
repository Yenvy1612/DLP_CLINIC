package com.acare.clinic.agent.dlp

object MaskingUtil {
    fun mask(text: String): String {
        if (text.isBlank()) return text

        var out = text

        // CCCD/CMND: match 9 to 12 digits
        out = out.replace(Regex("\\b\\d{9,12}\\b")) { m ->
            val v = m.value
            "${v.take(3)}******${v.takeLast(2)}"
        }

        // Phone VN: keep first 4 and last 2 digits.
        out = out.replace(Regex("\\b(84|0[35789])\\d{8}\\b")) { m ->
            val v = m.value
            if (v.length <= 6) "******" else "${v.take(4)}****${v.takeLast(2)}"
        }

        // Email: keep first 2 chars and domain.
        out = out.replace(Regex("\\b[\\w.-]+@[\\w.-]+\\.\\w{2,4}\\b")) { m ->
            val v = m.value
            val at = v.indexOf('@')
            if (at <= 2) "****${v.substring(at)}" else "${v.take(2)}****${v.substring(at)}"
        }

        return out
    }
}
