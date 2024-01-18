package me.weteam.loghub.bo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * <p></p>
 * <p>-</p>
 *
 * @author 阿古 (larrykoo@126.com)
 * @date 2022/3/15 11:35
 * @since 1.0.0
 */
@Data
@Accessors(chain = true)
public class QueryLogPage implements Serializable {
    private static final long serialVersionUID = -2972878633426274898L;

    /**
     * query duration
     */
    private Long elapsedDuration;

    /**
     * scan row size
     */
    private Long scanRowSize;

    /**
     * query response
     */
    private List<String> jsonLogs;
}
