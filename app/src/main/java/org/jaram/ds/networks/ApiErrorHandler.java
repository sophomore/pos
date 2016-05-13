package org.jaram.ds.networks;

import android.text.TextUtils;

import org.jaram.ds.models.ErrorResponse;

import retrofit.ErrorHandler;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ApiErrorHandler implements ErrorHandler {

    public static final String TAG = ApiErrorHandler.class.getSimpleName();

    public static final int BAD_REQUEST_ERROR = 400;
    public static final int SESSION_EXPIRED_ERROR = 401;
    public static final int FORBIDDEN_ERROR = 403;
    public static final int NOT_FOUND_ERROR = 404;
    public static final int PRECONDITION_FAILED_ERROR = 412;
    public static final int METHOD_NOT_ALLOWED_ERROR = 405;
    public static final int INTERNAL_SERVER_ERROR = 500;

    public ApiErrorHandler() {

    }

    @Override
    public Throwable handleError(RetrofitError cause) {
        switch (cause.getKind()) {
            case HTTP:
                handleHttpError(cause);
                break;
            case NETWORK:
                handleNetworkError(cause);
                break;
            case CONVERSION:
                handleConversionError(cause);
                break;
            case UNEXPECTED:
                handleUnexpectedError(cause);
                break;
            default:
                throw new AssertionError("Unknown error kind: " + cause.getKind());
        }

        return cause;
    }

    protected void handleNetworkError(RetrofitError cause) {
        // TODO: handle network error
        // Do nothing...
    }

    protected void handleConversionError(RetrofitError cause) {
        // TODO: handle conversion error
        // Do nothing...
    }

    protected void handleUnexpectedError(RetrofitError cause) {
        // TODO: handle unexpected error
        // Do nothing...
    }

    protected void handleHttpError(RetrofitError cause) {
        Response response = cause.getResponse();
        int statusCode = response.getStatus();

        if (statusCode >= BAD_REQUEST_ERROR) {
            String errorMessage = "";
            try {
                errorMessage = ((ErrorResponse) cause.getBodyAs(ErrorResponse.class)).detail;
            } catch (Exception e) {
                // Do nothings
            }
            switch (statusCode) {
                case BAD_REQUEST_ERROR:
                case NOT_FOUND_ERROR:
                case PRECONDITION_FAILED_ERROR:
                case METHOD_NOT_ALLOWED_ERROR:
                    if (TextUtils.isEmpty(errorMessage)) {
                        return;
                    }

//                    Observable.just(errorMessage)
//                            .observeOn(AndroidSchedulers.mainThread())
//                            .subscribe(msg -> showErrorMessage(msg), SLog::e);
                    break;
                case SESSION_EXPIRED_ERROR:
                case FORBIDDEN_ERROR:
//                    Observable.just(Api.context)
//                            .observeOn(AndroidSchedulers.mainThread())
//                            .subscribe(this::expireSession, SLog::e);
                    break;
                case INTERNAL_SERVER_ERROR:
                    break;
            }
        }
    }
//
//    /**
//     * API 요청에 대해 Session Expired(401), Forbidden(403) 등의 응답이 오면 호출된다.
//     * 현재 사용자 정보를 제거(로그아웃)하고, {@link Context}가 {@link BaseActivity}를 확장한 뷰일 경우,
//     * 세션 만료 다이얼로그를 보여준다.
//     */
//    private void showErrorMessage(String errorMessage) {
//        if (Api.context instanceof BaseActivity) {
//            new AlertDialog.Builder(Api.context)
//                    .setMessage(errorMessage)
//                    .setPositiveButton(R.string.label_confirm, null)
//                    .show();
//        }
//    }
//
//    /**
//     * API 요청에 대해 Session Expired(401), Forbidden(403) 등의 응답이 오면 호출된다.
//     * 현재 사용자 정보를 제거(로그아웃)하고, {@link Context}가 {@link BaseActivity}를 확장한 뷰일 경우,
//     * 세션 만료 다이얼로그를 보여준다.
//     *
//     * @param context API 요청이 호출된 시점의 Context
//     */
//
//    private void expireSession(Context context) {
//        UserManager.getInstance(context).logout();
//
//        if (context instanceof BaseActivity && !(context instanceof LoginActivity)) {
//            showSessionExpiredDialog((BaseActivity) context);
//        }
//    }
//
//    /**
//     * 세션 만료 다이얼로그를 보여준다.
//     *
//     * @param activity 다이얼로그를 보여줄 {@link BaseActivity}
//     */
//    private void showSessionExpiredDialog(BaseActivity activity) {
//        new AlertDialog.Builder(activity)
//                .setMessage(R.string.message_session_expired)
//                .setCancelable(false)
//                .setPositiveButton(R.string.label_confirm, (dialog, which) -> {
//                    Intent intent = new Intent(activity, LoginActivity.class);
//                    ComponentName componentName = intent.getComponent();
//                    Intent loginIntent = IntentCompat.makeRestartActivityTask(componentName);
//                    activity.startActivity(loginIntent);
//                }).show();
//    }
}