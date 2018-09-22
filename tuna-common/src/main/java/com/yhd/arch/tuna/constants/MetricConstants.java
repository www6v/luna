package com.yhd.arch.tuna.constants;

import java.io.Serializable;

/**
 * Created by root on 2/8/17.
 */
public class MetricConstants implements Serializable{

	public static String LINKID_ID = "LINKID_ID";
	public static String SPAN_ID = "SPAN_ID";

	public static String TAG_APP="caller_app";

	public static String TAG_TARGET_APP="provider_app";

	public static String TAG_RES="client_method_name";

	public static String TAG_TARGET_RES="service_method_name";

	public static String TAG_SERVICE="service_name";

	public static String TAG_GROUP="service_group";

	public static String TAG_APP_TYPE="client_type";

	public static String TAG_TARGET_APP_TYPE="target_type";

	public static String COUNT_RESULT_CODE="result_code";

	public static String COUNT_VALUE_FAILED="failed_counts";

	public static String COUNT_VALUE_SUCCESS="success_counts";

	public static String COUNT_FAST_COUNTS="fast_counts";

	public static String COUNT_COMMON_COUNTS="common_counts";

	public static String COUNT_SLOW_COUNTS="slow_counts";

	public static String RESPONSE_TOTAL_COST_TIME="total_cost_time";

	public static String RESPONSE_MAX_TIME="max_time";

	public static String RESPONSE_MIN_TIME="min_time";

	public static String STAT_SERVER_COUNT = "svr_count";

	public static String STAT_SERVER_RESPONSE_AVG_TIME= "svr_response_avg_time";


}
