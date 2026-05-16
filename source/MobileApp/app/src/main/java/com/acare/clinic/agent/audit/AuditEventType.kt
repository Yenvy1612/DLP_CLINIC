package com.acare.clinic.agent.audit

object AuditEventType {
    const val LOGIN_SUCCESS = "LOGIN_SUCCESS"
    const val LOGIN_FAILED = "LOGIN_FAILED"

    const val VIEW_PATIENT_LIST = "VIEW_PATIENT_LIST"
    const val VIEW_PATIENT_DETAIL = "VIEW_PATIENT_DETAIL"

    const val FORM_DLP_MATCHED = "FORM_DLP_MATCHED"
    const val COPY_PATIENT_DATA = "COPY_PATIENT_DATA"
    const val EXPORT_BLOCKED = "EXPORT_BLOCKED"
    const val EXPORT_ALLOWED = "EXPORT_ALLOWED"
}