package com.yang.crm.workbench.service;

import com.yang.crm.workbench.domain.Tran;
import com.yang.crm.workbench.domain.TranHistory;

import java.util.List;
import java.util.Map;

public interface TranService {
    boolean save(Tran t, String customerName);

    Tran detail(String id);

    List<TranHistory> getHistoryListByTranId(String tranId);

    boolean changStage(Tran t);

    Map<String, Object> getCharts();
}
