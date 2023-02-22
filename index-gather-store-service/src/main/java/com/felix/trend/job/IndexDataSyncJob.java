package com.felix.trend.job;

import cn.hutool.core.date.DateUtil;
import com.felix.trend.pojo.Index;
import com.felix.trend.service.IndexDataService;
import com.felix.trend.service.IndexService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.List;

public class IndexDataSyncJob extends QuartzJobBean {

    @Autowired
    private IndexService indexService;

    @Autowired
    private IndexDataService indexDataService;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        System.out.println("timer begin：" + DateUtil.now());
        List<Index> indexes = indexService.fresh();
        for (Index index : indexes) {
            indexDataService.fresh(index.getCode());
        }
        System.out.println("timer end:：" + DateUtil.now());

    }

}
