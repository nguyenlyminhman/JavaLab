package com.lab.modules.binance.service.impl;

import com.lab.modules.binance.service.IBatchJob;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.launch.NoSuchJobExecutionException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BatchJobServiceImpl implements IBatchJob {

    @Autowired
    private JobOperator jobOperator;

    @Override
    public boolean handleRestartJob(int executionId) throws JobInstanceAlreadyCompleteException, NoSuchJobException, NoSuchJobExecutionException, JobParametersInvalidException, JobRestartException {
        jobOperator.restart(executionId);
        return true;
    }
}
