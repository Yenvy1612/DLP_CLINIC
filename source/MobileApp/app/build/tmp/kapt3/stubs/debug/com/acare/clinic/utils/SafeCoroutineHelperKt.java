package com.acare.clinic.utils;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u00002\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\u001aT\u0010\u0000\u001a\u00020\u0001*\u00020\u00022\u001a\b\u0002\u0010\u0003\u001a\u0014\u0012\b\u0012\u00060\u0005j\u0002`\u0006\u0012\u0004\u0012\u00020\u0001\u0018\u00010\u00042\'\u0010\u0007\u001a#\b\u0001\u0012\u0004\u0012\u00020\t\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00010\n\u0012\u0006\u0012\u0004\u0018\u00010\u000b0\b\u00a2\u0006\u0002\b\f\u00a2\u0006\u0002\u0010\r\u001aT\u0010\u000e\u001a\u00020\u0001*\u00020\u00022\u001a\b\u0002\u0010\u0003\u001a\u0014\u0012\b\u0012\u00060\u0005j\u0002`\u0006\u0012\u0004\u0012\u00020\u0001\u0018\u00010\u00042\'\u0010\u0007\u001a#\b\u0001\u0012\u0004\u0012\u00020\t\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00010\n\u0012\u0006\u0012\u0004\u0018\u00010\u000b0\b\u00a2\u0006\u0002\b\f\u00a2\u0006\u0002\u0010\r\u00a8\u0006\u000f"}, d2 = {"launchSafe", "", "Landroidx/fragment/app/Fragment;", "onError", "Lkotlin/Function1;", "Ljava/lang/Exception;", "Lkotlin/Exception;", "block", "Lkotlin/Function2;", "Lkotlinx/coroutines/CoroutineScope;", "Lkotlin/coroutines/Continuation;", "", "Lkotlin/ExtensionFunctionType;", "(Landroidx/fragment/app/Fragment;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function2;)V", "launchWhenStarted", "app_debug"})
public final class SafeCoroutineHelperKt {
    
    /**
     * Chạy coroutine an toàn trong Fragment.
     * - Tự hủy khi fragment destroyed
     * - Bắt exception không crash app
     * - Chỉ chạy khi view đang active (>= STARTED)
     */
    public static final void launchSafe(@org.jetbrains.annotations.NotNull()
    androidx.fragment.app.Fragment $this$launchSafe, @org.jetbrains.annotations.Nullable()
    kotlin.jvm.functions.Function1<? super java.lang.Exception, kotlin.Unit> onError, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function2<? super kotlinx.coroutines.CoroutineScope, ? super kotlin.coroutines.Continuation<? super kotlin.Unit>, ? extends java.lang.Object> block) {
    }
    
    /**
     * Chạy coroutine đơn giản — không dùng repeatOnLifecycle (chạy 1 lần).
     * Có guard _binding != null trước khi access binding.
     */
    public static final void launchWhenStarted(@org.jetbrains.annotations.NotNull()
    androidx.fragment.app.Fragment $this$launchWhenStarted, @org.jetbrains.annotations.Nullable()
    kotlin.jvm.functions.Function1<? super java.lang.Exception, kotlin.Unit> onError, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function2<? super kotlinx.coroutines.CoroutineScope, ? super kotlin.coroutines.Continuation<? super kotlin.Unit>, ? extends java.lang.Object> block) {
    }
}