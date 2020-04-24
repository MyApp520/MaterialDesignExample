package org.smile.mde.oksocket.constant;

/**
 * Created by xh_smile on 2020/4/16.
 */

public interface SocketServerConstant {

    /**
     * 对服务器发起请求
     */
    interface RequestCode {

        /**
         * 心跳包请求
         */
        String REQUEST_TYPE_HEARTBEAT_BAG = "20000";

        /**
         * 登录认证请求
         */
        String REQUEST_TYPE_LOGIN_AUTH = "30001";
    }

    /**
     * 接收到服务器的响应数据
     */
    interface ResponseCode {
        /**
         * socket登录认证成功
         */
        String RESPONSE_TYPE_LOGIN_AUTH_SUCCESS = "30002";

        /**
         * socket推送的执法记录仪数据
         */
        String RESPONSE_TYPE_LAW_ENFORCEMENT = "30003";

        /**
         * socket推送的警情数据
         */
        String RESPONSE_TYPE_ALARM_POLICEMENT = "30007";
    }
}
