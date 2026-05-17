package com.acare.clinic.utils

import android.content.Context
import android.os.Environment
import com.itextpdf.text.Document
import com.itextpdf.text.Font
import com.itextpdf.text.Paragraph
import com.itextpdf.text.pdf.PdfWriter
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfExportUtil {

    fun exportTextAsPdf(
        context: Context,
        title: String,
        lines: List<String>,
        prefix: String
    ): File {
        val folder = File(
            context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),
            "ClinicExports"
        )
        if (!folder.exists()) folder.mkdirs()

        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val file = File(folder, "${prefix}_${timestamp}.pdf")

        val doc = Document()
        PdfWriter.getInstance(doc, FileOutputStream(file))
        doc.open()

        val titleFont = Font(Font.FontFamily.HELVETICA, 16f, Font.BOLD)
        val bodyFont = Font(Font.FontFamily.HELVETICA, 11f, Font.NORMAL)

        doc.add(Paragraph(title, titleFont))
        doc.add(Paragraph("Generated at: $timestamp", bodyFont))
        doc.add(Paragraph(" "))
        lines.forEach { line ->
            doc.add(Paragraph(line, bodyFont))
        }

        doc.close()
        return file
    }
}

