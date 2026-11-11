package com.example.b2capp.data.local.search

import java.text.Normalizer
import java.util.Locale
import java.util.regex.Pattern

/**
 * Tiện ích chuẩn hóa tiếng Việt và xây dựng câu truy vấn Full-Text Search (FTS) an toàn cho SQLite.
 */
object VietnameseNormalizer {

    private val DIACRITICS_PATTERN = Pattern.compile("\\p{InCombiningDiacriticalMarks}+")

    /**
     * Chuyển chuỗi tiếng Việt có dấu thành không dấu, chuyển chữ thường.
     * Ví dụ: "Áo thun nam Đẹp" -> "ao thun nam dep"
     */
    fun removeAccents(text: String?): String {
        if (text.isNullOrBlank()) return ""
        var normalized = text.trim().lowercase(Locale.ROOT)
        // Thay thế ký tự đ, Đ đặc thù của tiếng Việt trước khi phân rã Unicode
        normalized = normalized.replace('đ', 'd').replace('Đ', 'd')
        // Phân rã NFD để tách các dấu thanh ra khỏi nguyên âm
        val decomposed = Normalizer.normalize(normalized, Normalizer.Form.NFD)
        return DIACRITICS_PATTERN.matcher(decomposed).replaceAll("")
    }

    /**
     * Xây dựng câu truy vấn an toàn cho SQLite FTS MATCH.
     * - Loại bỏ ký tự đặc biệt có thể phá vỡ cú pháp SQLite FTS (' * " : ( ) NEAR OR AND NOT).
     * - Tách từng từ và thêm hậu tố '*' để hỗ trợ prefix search (ví dụ: gõ "ao thu" -> "ao* thu*").
     */
    fun buildFtsMatchQuery(rawQuery: String?): String {
        if (rawQuery.isNullOrBlank()) return ""

        // Giữ lại chữ cái, số, khoảng trắng và ký tự tiếng Việt
        val sanitized = rawQuery
            .replace(Regex("[^\\p{L}\\p{Nd}\\s]"), " ")
            .trim()

        if (sanitized.isBlank()) return ""

        val tokens = sanitized.split(Regex("\\s+")).filter { it.isNotBlank() }
        if (tokens.isEmpty()) return ""

        // Kết hợp cả từ gốc và từ không dấu để MATCH được trên cả cột name có dấu và normalizedName không dấu
        val terms = mutableListOf<String>()
        for (token in tokens) {
            val unaccented = removeAccents(token)
            if (unaccented.isNotEmpty() && unaccented != token.lowercase(Locale.ROOT)) {
                // Ví dụ: (áo* OR ao*)
                terms.add("($token* OR $unaccented*)")
            } else {
                terms.add("$token*")
            }
        }

        return terms.joinToString(" AND ")
    }
}
