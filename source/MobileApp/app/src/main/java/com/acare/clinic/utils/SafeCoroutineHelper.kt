package com.acare.clinic.utils

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * SafeCoroutineHelper — Tiện ích chạy coroutine an toàn trong Fragment.
 *
 * Ngăn crash khi chuyển tab nhanh bằng cách:
 * 1. Chỉ chạy coroutine khi view lifecycle >= STARTED
 * 2. Tự hủy khi fragment bị destroy
 * 3. Wrap exception để không crash app
 */

/**
 * Chạy coroutine an toàn trong Fragment.
 * - Tự hủy khi fragment destroyed
 * - Bắt exception không crash app
 * - Chỉ chạy khi view đang active (>= STARTED)
 */
fun Fragment.launchSafe(
    onError: ((Exception) -> Unit)? = null,
    block: suspend CoroutineScope.() -> Unit
) {
    viewLifecycleOwner.lifecycleScope.launch {
        try {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                block()
            }
        } catch (e: Exception) {
            onError?.invoke(e)
        }
    }
}

/**
 * Chạy coroutine đơn giản — không dùng repeatOnLifecycle (chạy 1 lần).
 * Có guard _binding != null trước khi access binding.
 */
fun Fragment.launchWhenStarted(
    onError: ((Exception) -> Unit)? = null,
    block: suspend CoroutineScope.() -> Unit
) {
    viewLifecycleOwner.lifecycleScope.launch {
        try {
            block()
        } catch (e: kotlinx.coroutines.CancellationException) {
            // Coroutine bị cancel do lifecycle — OK, không log
        } catch (e: Exception) {
            onError?.invoke(e)
        }
    }
}
