package com.ravinada.mvvmarchitecture.utils

class AppConstant {
    companion object {
        //define all projects constant here
        const val USER_TYPE = "user_type"

    }

    //define fragment/feature/API params wise constant like this
    abstract class UserKeySaveENUM private constructor() {
        init {
            throw IllegalStateException("")
        }

        companion object {
            const val USER_TYPE = "user_type"
            const val USER_LOGIN_TYPE = "user_login_type"
            const val USER_ID = "id"
            const val USER_ROLE_ID = "user_role_id"
            const val USER_NAME = "user_name"
            const val USER_EMAIL = "user_email"
            const val USER_MOBILE = "user_mobile"

        }
    }

    abstract class DateUtilKey private constructor() {
        init {
            throw IllegalStateException("")
        }

        companion object {
            const val TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'"
            const val LIST_DISPLAY_FORMAT = "dd MMM, hh:mm a"
            const val FORMAT_WITH_TIME = "yyyy-MM-dd HH:mm:ss"
            const val FORMAT_WITHOUT_SECONDS = "yyyy-MM-dd HH:mm"
            const val FORMAT_DAY = "MMM dd"
            const val FORMAT_IN_SIMPLE = "yyyy-MM-dd"
            const val FORMAT_OUT_SIMPLE = "yyyy-MM-dd"
            const val FORMAT_CREATED_DATE = "dd MMM yyyy hh:mm a"
            const val FORMAT_UPDATED_DATE = "EEE, dd MMM yy"
            const val FORMAT_SIMPLE_YEAR_FIRST = "yyyy-MM-dd"
            const val FORMAT_SIMPLE_DATE_FIRST = "dd/MM/yyyy"
        }
    }
}