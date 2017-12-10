package com.jakdor.labday.rx;

import android.support.annotation.Nullable;

import static com.jakdor.labday.rx.RxStatus.ERROR;
import static com.jakdor.labday.rx.RxStatus.NO_DB;
import static com.jakdor.labday.rx.RxStatus.NO_INTERNET;
import static com.jakdor.labday.rx.RxStatus.SUCCESS;

/**
 * Observable response wrapper template
 * @param <T> dataType
 */
public class RxResponse<T> {

    public final RxStatus status;

    @Nullable
    public final T data;

    @Nullable
    public final Throwable error;

    public RxResponse(RxStatus status, @Nullable T data, @Nullable Throwable error) {
        this.status = status;
        this.data = data;
        this.error = error;
    }

    public static <T> RxResponse<T> success(T data) {
        return new RxResponse<>(SUCCESS, data, null);
    }

    public static <T> RxResponse<T> error(Throwable error) {
        return new RxResponse<>(ERROR, null, error);
    }

    public static <T> RxResponse<T> noDb(T data) {
        return new RxResponse<>(NO_DB, data, null);
    }

    public static <T> RxResponse<T> noInternet(T data) {
        return new RxResponse<>(NO_INTERNET, data, null);
    }
}
