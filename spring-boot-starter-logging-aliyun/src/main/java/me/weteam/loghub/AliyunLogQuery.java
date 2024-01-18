package me.weteam.loghub;

import com.aliyun.openservices.log.Client;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.request.GetLogsRequest;
import lombok.extern.slf4j.Slf4j;
import me.weteam.loghub.bo.QueryLogPage;

import java.util.stream.Collectors;

/**
 * <p>阿里云 SLS 日志查询 Service</p>
 * <p>-</p>
 *
 * @author 阿古 (larrykoo@126.com)
 * @date 2022/3/14 20:26
 * @since 1.0.0
 */
@Slf4j
public class AliyunLogQuery {

    private final Client client;
    private final String projectName;

    public AliyunLogQuery(Client client, String projectName) {
        this.client = client;
        this.projectName = projectName;
    }

    /**
     * Find the sls logs by sql
     *
     * @param logStore log store name of a project
     * @param fromTime begin time
     * @param toTime   end time
     * @param sql      user query sql
     * @return {@link QueryLogPage}
     */
    public QueryLogPage getLogsBySql(String logStore, Integer fromTime, int toTime, String sql) {
        try {
            var getLogsReq = new GetLogsRequest(projectName, logStore, fromTime, toTime, "", sql, 0, 0, false);
            var response = client.GetLogs(getLogsReq);
            return new QueryLogPage()
                    .setScanRowSize(response.getProcessedRow())
                    .setElapsedDuration(response.getElapsedMilliSecond())
                    .setJsonLogs(response.GetLogs().stream()
                            .map(qLog -> qLog.GetLogItem().ToJsonString())
                            .collect(Collectors.toList())
                    );
        } catch (LogException e) {
            throw new RuntimeException(e);
        }
    }


}
