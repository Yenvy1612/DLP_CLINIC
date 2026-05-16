package com.acare.clinic.agent.audit
import java.time.Instant
import com.acare.clinic.agent.device.DeviceIdProvider
import com.acare.clinic.agent.dlp.DlpScanner
import com.acare.clinic.agent.dlp.MaskingUtil
import com.acare.clinic.agent.queue.EventQueueRepository

class AgentEventTracker(
    private val deviceIdProvider: DeviceIdProvider,
    private val dlpScanner: DlpScanner,
    private val queueRepository: EventQueueRepository
) {
    suspend fun trackViewPatientDetail(
        userId: Long,
        patientId: Long
    ) {
        enqueue(
            userId = userId,
            eventType = AuditEventType.VIEW_PATIENT_DETAIL,
            action = "VIEW",
            severity = "LOW",
            violationType = null,
            contentSnippet = null,
            details = mapOf("patientId" to patientId)
        )
    }

    suspend fun trackFormSubmit(
        userId: Long,
        formName: String,
        formText: String
    ): Boolean {
        val result = dlpScanner.scan(formText)

        if (result.isViolation) {
            enqueue(
                userId = userId,
                eventType = AuditEventType.FORM_DLP_MATCHED,
                action = "SUBMIT_FORM",
                severity = result.severity,
                violationType = result.violations.joinToString(","),
                contentSnippet = MaskingUtil.mask(formText),
                details = mapOf("formName" to formName)
            )

            return false
        }

        return true
    }

    suspend fun trackCopy(
        userId: Long,
        patientId: Long,
        copiedText: String
    ): Boolean {
        val result = dlpScanner.scan(copiedText)

        if (result.isViolation) {
            enqueue(
                userId = userId,
                eventType = AuditEventType.COPY_PATIENT_DATA,
                action = "COPY",
                severity = result.severity,
                violationType = result.violations.joinToString(","),
                contentSnippet = MaskingUtil.mask(copiedText),
                details = mapOf("patientId" to patientId)
            )

            return false
        }

        return true
    }

    suspend fun trackExport(
        userId: Long,
        patientId: Long,
        exportText: String
    ): Boolean {
        val result = dlpScanner.scan(exportText)

        if (result.isViolation) {
            enqueue(
                userId = userId,
                eventType = AuditEventType.EXPORT_BLOCKED,
                action = "EXPORT",
                severity = result.severity,
                violationType = result.violations.joinToString(","),
                contentSnippet = MaskingUtil.mask(exportText),
                details = mapOf("patientId" to patientId)
            )

            return false
        }

        enqueue(
            userId = userId,
            eventType = AuditEventType.EXPORT_ALLOWED,
            action = "EXPORT",
            severity = "LOW",
            violationType = null,
            contentSnippet = null,
            details = mapOf("patientId" to patientId)
        )

        return true
    }

    private suspend fun enqueue(
        userId: Long?,
        eventType: String,
        action: String,
        severity: String,
        violationType: String?,
        contentSnippet: String?,
        details: Map<String, Any?>
    ) {
        val event = AgentEventRequest(
            deviceId = deviceIdProvider.getDeviceId(),
            userId = userId,
            eventType = eventType,
            action = action,
            severity = severity,
            violationType = violationType,
            contentSnippet = contentSnippet,
            details = details,
            timestamp = Instant.now().toString()
        )

        queueRepository.enqueue(event)
    }
}