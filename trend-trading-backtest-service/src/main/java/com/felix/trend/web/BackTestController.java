package com.felix.trend.web;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.felix.trend.pojo.AnnualProfit;
import com.felix.trend.pojo.IndexData;
import com.felix.trend.pojo.Profit;
import com.felix.trend.pojo.Trade;
import com.felix.trend.service.BackTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class BackTestController {

    @Autowired
    BackTestService backTestService;

    @GetMapping("/stimulate/{code}/{ma}/{serviceCharge}/{buyThreshold}/{sellThreshold}/{startDate}/{endDate}")
    @CrossOrigin
    public Map<String,Object> backTest(@PathVariable(value = "code") String code,
                                                @PathVariable(value = "ma") int ma,
                                                @PathVariable("serviceCharge") float serviceCharge,
                                                @PathVariable("buyThreshold") float buyThreshold,
                                                @PathVariable("sellThreshold") float sellThreshold,
                                                @PathVariable(value = "startDate") String startDate,
                                                @PathVariable(value = "endDate") String endDate){
        Map<String,Object> map = new HashMap<>();
        List<IndexData> list = backTestService.listIndexData(code);
        float sellRate = sellThreshold;
        float buyRate = buyThreshold;
        Map<String,?> simulateResult= backTestService.simulate(ma,sellRate, buyRate,serviceCharge, list);
        List<Profit> profits = (List<Profit>) simulateResult.get("profits");
        List<Trade> trades = (List<Trade>) simulateResult.get("trades");
        List<AnnualProfit> annualProfitList = backTestService.getAnnualProfit(list,profits);
        int winCount = (Integer) simulateResult.get("winNum");
        int lossCount = (Integer) simulateResult.get("loseNum");
        float avgWinRate = (Float) simulateResult.get("averageWinRate");
        float avgLossRate = (Float) simulateResult.get("averageLoseRate");
        float years = backTestService.getYears(list);
        float indexIncomeTotal = (list.get(list.size()-1).getClosePoint() - list.get(0).getClosePoint()) / list.get(0).getClosePoint();
        float indexIncomeAnnual = (float) Math.pow(1+indexIncomeTotal, 1/years) - 1;
        float trendIncomeTotal = (profits.get(profits.size()-1).getValue() - profits.get(0).getValue()) / profits.get(0).getValue();
        float trendIncomeAnnual = (float) Math.pow(1+trendIncomeTotal, 1/years) - 1;
        String indexStartDate = list.get(0).getDate();
        String indexEndDate = list.get(list.size()-1).getDate();
        list = filter(list,startDate,endDate);
        map.put("years", years);
        map.put("indexIncomeTotal", indexIncomeTotal);
        map.put("indexIncomeAnnual", indexIncomeAnnual);
        map.put("trendIncomeTotal", trendIncomeTotal);
        map.put("trendIncomeAnnual", trendIncomeAnnual);
        map.put("trades",trades);
        map.put("profits",profits);
        map.put("indexDatas",list);
        map.put("indexStartDate",indexStartDate);
        map.put("indexEndDate",indexEndDate);
        map.put("winCount", winCount);
        map.put("lossCount", lossCount);
        map.put("avgWinRate", avgWinRate);
        map.put("avgLossRate", avgLossRate);
        map.put("annualProfits", annualProfitList);
        return map;
    }

    private List<IndexData> filter(List<IndexData> indexDatas, String startDate, String endDate){
        if(StrUtil.isBlankOrUndefined(startDate) || StrUtil.isBlankOrUndefined(endDate) )
            return indexDatas;
        List<IndexData> result = new ArrayList<>();
        Date trueStartDate = DateUtil.parse(startDate);
        Date trueEndDate = DateUtil.parse(endDate);
        for(IndexData indexData : indexDatas){
            Date trueDate = DateUtil.parse(indexData.getDate());
            if(trueDate.compareTo(trueStartDate) >= 0 && trueDate.compareTo(trueEndDate) <= 0)
                result.add(indexData);
        }
        return result;
    }



}
