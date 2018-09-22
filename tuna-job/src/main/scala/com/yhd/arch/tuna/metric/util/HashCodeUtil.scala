package com.yhd.arch.tuna.metric.util

/**
  * Created by wangwei14 on 2016/9/21.
  */
 object HashCodeUtil {

    def getHashCode( args: String * ): Long = {
    if (args != null) {
      var hash = 0;
      for ( arg <- args) {
        if (arg != null && arg.length() > 0) {
          if (hash == 0) {
            hash = arg.hashCode();
          } else {
            val h = hash << 32 | arg.hashCode();
            hash = h ^ hash;
          }
        }
      }
      return hash;
    } else {
      return 0;
    }
  }
}
