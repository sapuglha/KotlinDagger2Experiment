package com.robotsandpencils.kotlindaggerexperiment.app.model

import timber.log.Timber

data class ErrorResponse(val error: ErrorResponseError)

data class ErrorResponseError(val code: String? = null,
                              val detail: String? = null,
                              val warnings: List<ErrorResponseWarning>? = null)

val ErrorResponseError.codeEnum: ErrorCodeEnum
    get() = try {
        ErrorCodeEnum.valueOf(code?.toUpperCase() ?: "unknown")
    } catch (t: Throwable) {
        Timber.w("server returned unknown error code: $code")
        ErrorCodeEnum.UNKNOWN
    }

data class ErrorResponseWarning(val code: String? = null,
                                val detail: String? = null)

val ErrorResponseWarning.codeEnum: WarningCodeEnum
    get() = try {
        WarningCodeEnum.valueOf(code?.toUpperCase() ?: "unknown")
    } catch (t: Throwable) {
        Timber.w("server returned unknown warning code: $code")
        WarningCodeEnum.UNKNOWN
    }


enum class ErrorCodeEnum {
    INVALID_API_KEY,
    ACCOUNT_EXISTS_IN_DB,
    ACCOUNT_INACTIVE,
    ACCOUNT_SUSPENDED,
    TARIFF_NOT_SUPPORTED,
    NUMBER_DOES_NOT_EXIST,
    OTAC_EXPIRED,
    OTAC_ALREADY_USED,
    EXCEEDED_OTAC_ATTEMPTS,
    INVALID_OTAC,
    INVALID_CREDENTIALS,
    FORBIDDEN,
    PIN_INCORRECT_RETRY_ALLOWED,
    EXCEEDED_PIN_ATTEMPTS,
    MUST_ACCEPT_WARNINGS,
    PENDING_CHANGE_CANNOT_CONTINUE,
    MAXIMUM_BUNDLE_QUANTITY_PER_MONTH,
    OVERDUE_INVOICE,
    SUPPORT_REQUIRED,
    RETRY,
    NO_RETRY,
    MPN_ACCOUNT_MISMATCH,
    KILL_SIGNAL,
    NOT_MASTER,
    ACCOUNT_NUMBER_MISMATCH,
    ACCOUNT_NUMBER_ERROR,
    MAINTENANCE,
    INVALID_NUMBER_FORMAT,
    INVALID_URL,
    BADLY_FORMED_REQUEST,
    UNKNOWN
}

enum class WarningCodeEnum {
    PENDING_CHANGE_SAME_TYPE_CAN_CONTINUE,
    CLOSE_TO_BILL_DATE,
    PENDING_ACCOUNT_DEACTIVATION,
    PENDING_SUBSCRIPTION_DEACTIVATION,
    CANNOT_BE_RE_ADDED,
    UNKNOWN
}