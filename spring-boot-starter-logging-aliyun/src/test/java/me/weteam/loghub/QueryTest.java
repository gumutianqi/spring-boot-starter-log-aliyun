package me.weteam.loghub;

import com.alibaba.fastjson.JSON;
import com.aliyun.openservices.log.Client;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * <p>A query testcase for SLS</p>
 * <p>-</p>
 *
 * @author 阿古 (larrykoo@126.com)
 * @date 2022/3/15 10:10
 * @since 1.0.0
 */
public class QueryTest {
    private static final String AK = "";
    public static final String SK = "";
    public static final String ENDPOINT = "https://cn-zhangjiakou.log.aliyuncs.com";
    public static final String PROJECT = "omni-log-hub-dev";
    private AliyunLogQuery logQuery;

    @BeforeEach
    void setUp() {
        var client = new Client(ENDPOINT, AK, SK);
        logQuery = new AliyunLogQuery(client, PROJECT);
    }

    @Test
    void queryBySql() {
        var logStore = "omni-point-apns";
        var logTopic = "apns-event-v1";
        var fromTime = 1647041335;
        var toTime = 1647344113;
        var sql = "* | select _event_id, _event_occurred_time, " +
                "meta__app_delay_duration, meta__app_version, meta__push_account, " +
                "meta__push_ent_id, meta__push_agent_id, meta__received, meta__push_platform " +
                "from log " +
                "WHERE __topic__='" + logTopic + "' " +
                "order by _event_occurred_time desc limit 0, 10";
        System.out.println("sql = " + sql);
        var response = logQuery.getLogsBySql(logStore, fromTime, toTime, sql);
        System.out.println("Query Response: \n" + JSON.toJSONString(response, true));
    }
}
