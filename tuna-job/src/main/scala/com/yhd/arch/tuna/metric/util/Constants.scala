package com.yhd.arch.tuna.metric.util

/**
 * Created by wangwei14 on 2016/9/22.
 */
object Constants extends Serializable {

  val SUCCESS: String = "success";
  val FAIL: String = "fail";

  val TRUE: String = "true";
  val FALSE: String = "false";


  //////////////  svr_count
  val TAG_RESULT_CODE = "result_code";
  val TAG_RESULT_CODE_VALUE_ERROR = "fail";
  val TAG_RESULT_CODE_VALUE_SUCCESS = "success";

  val TAG_RESULT_SPEED = "result_speed";
  val TAG_RESULT_SPEED_VALUE_SLOW = "slow";
  val TAG_RESULT_SPEED_VALUE_VERY_SLOW = "very_slow";
  val TAG_RESULT_SPEED_VALUE_NOMAL = "normal";

  var STAT_SERVER_COUNT = "svr_count";

  //// for tags
  //  var TAG_TARGET_APP = "target_app";
  //  var TAG_TARGET_API = "api";

  var TAG_TARGET_APP = "target_pool";
  var TAG_TARGET_API = "target_service";
  var TAG_TARGET_HOST = "target_host";

  //  var TAG_APP = "app";
  //  var NAME = "name";  // caller.serviceId
  var TAG_APP = "caller_pool";
  var NAME = "caller_service"; // caller.serviceId

  ///////   response_time
  val TAG_RESPONSE_PERCENT: String = "response_percent"
  val TAG_RESPONSE_PERCENT_AVG: String = "avg"
  val TAG_RESPONSE_MAX: String = "max"
  val TAG_RESPONSE_MIN: String = "min"

  val STAT_SERVER_RESPONSE_TIME_APP: String = "svr_response_time_app"
  /////////
  val TRACE_ID = "TRACE_ID";
  val SPAN_ID = "SPAN_ID"
}
