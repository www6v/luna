#!/bin/bash
#description:tuna
. /etc/init.d/functions

BASE=`basename $0`
LOG_DIR="/data/M00/tuna/log"

send_email() {
    IP=`/sbin/ip a | egrep -v 'inet6|127.0.0.1|\/32' | awk -F'[ /]+' '/inet/{print $3}' | head -n1`
    TIME=`date +"%F %T"`

}

check_pid() {
    PID=$(ps -ef | grep com.yhd.arch.tuna.driver.MergeStream | grep -v grep | awk '{print $2}' | head -1)
    [ x"$PID" == x ] && return 0 || return 1  #1:exist; 0:not exist
}

start() {
    check_pid
    if [ x$PID != x ]; then
        echo "${BASE} (pid $PID) is running..."
    else
        echo -n "Starting $BASE:"
	TUNA_OPTS="-Dglobal.config.path=/data/M00/tuna/config"

	#echo
	ps -ef | grep "tuna"|grep -v grep

	CP_OPTS="-cp /var/www/webapps/tuna/*.jar"

	JVM_OPTS="-XX:+UseG1GC -Xss4m -XX:+HeapDumpOnOutOfMemoryError  -XX:+PrintFlagsFinal -XX:+PrintReferenceGC -verbose:gc -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+PrintAdaptiveSizePolicy -XX:+UnlockDiagnosticVMOptions -XX:+G1SummarizeConcMark -XX:InitiatingHeapOccupancyPercent=35 -XX:G1HeapRegionSize=16M -XX:ConcGCThreads=16 -XX:NewRatio=8 -XX:-UseBiasedLocking"	
	GCLOG_OPTS="-Xloggc:${LOG_DIR}/tuna-gc.log"


	OPTS="${CP_OPTS} ${TUNA_OPTS} ${JVM_OPTS} ${GCLOG_OPTS}"

	MAIN="com.yhd.arch.tuna.driver.MergeStream"

        #backup gc log
	if [ -f "${LOG_DIR}/tuna-gc.log" ];then
	    tail -n 3000 ${LOG_DIR}/tuna-gc.log > ${LOG_DIR}/tuna-gc.log.`date +-%Y-%m-%d-%H-%m-%S`.log
	    echo "Gc log backup finished"
	fi
	STD_OUT="${LOG_DIR}/tuna-std.out"

	if [ -f "$STD_OUT" ];then
	    mv $STD_OUT $STD_OUT`date +-%Y-%m-%d-%H-%m-%S`
	fi

	echo ${LOG_DIR}/tuna-boot.log

	exec /usr/java/jdk1.7.0_75/bin/java ${OPTS} ${MAIN} $*> "$STD_OUT" &   


        sleep 1
        check_pid
        if [ x$PID != x ]; then
            echo_success
            echo
        else
            echo_failure
            echo
        fi
    fi
}

stop() {
    check_pid
    if [ x$PID == x ]; then
        echo "${BASE} is stopped."
    else
        echo -n "Stopping $BASE:"
	kill -3 $PID
	sleep 1
	kill -9 $PID
        sleep 1
        check_pid
        if [ x$PID == x ]; then
            echo_success
            echo
        else
            echo_failure
            echo
        fi
    fi
}

status() {
    check_pid
    if [ x$PID != x ]; then
        echo "${BASE} (pid $PID) is running..."
    else
        echo "${BASE} is stopped."
    fi
}

check() {
    check_pid
    if [ x$PID != x ]; then
        echo "${BASE} (pid $PID) is running..."
    else
        echo -e "${BASE} is stopped, trying to start:"
        start
        send_email > /dev/null 2>&1
    fi
}

#main
case $1 in
    start)
        start
        ;;
    stop)
        stop
        ;;
    ''|restart)
        stop
        sleep 30
        start
        ;;
    status)
        status
        ;;
    check)
        check
        ;;
    *)
        echo "Usage: $0 {start|stop|restart|status|check}"
        exit 1
esac

