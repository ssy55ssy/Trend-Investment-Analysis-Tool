package com.felix.trend.service;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.felix.trend.client.IndexDataClient;
import com.felix.trend.pojo.AnnualProfit;
import com.felix.trend.pojo.IndexData;
import com.felix.trend.pojo.Profit;
import com.felix.trend.pojo.Trade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BackTestService {

    @Autowired
    IndexDataClient indexDataClient;

    public List<IndexData> listIndexData(String code){
        List<IndexData> indexDataList = indexDataClient.getIndexData(code);
        Collections.reverse(indexDataList);
        for(IndexData indexData : indexDataList){
            System.out.println(indexData);
        }
        return indexDataList;
    }

    public float getYears(List<IndexData> indexDatas){
        String strBegin = indexDatas.get(0).getDate();
        String strEnd = indexDatas.get(indexDatas.size()-1).getDate();
        Date begin = DateUtil.parse(strBegin);
        Date end = DateUtil.parse(strEnd);
        long days = DateUtil.between(begin,end, DateUnit.DAY);
        return (float)(days / 365.0);
    }

    public Map<String,Object> simulate(int ma, float sellRate, float buyRate, float serviceCharge, List<IndexData> indexDatas)  {
        float initialMoney = 1000;
        float currentMoney = 1000;
        float holdNum = 0;
        float value = 0;
        float init =0;
        int winNum = 0;
        int loseNum = 0;
        float totalWinRate = 0;
        float totalLoseRate = 0;
        float averageWinRate = 0;
        float averageLoseRate = 0;
        if(!indexDatas.isEmpty())
            init = indexDatas.get(0).getClosePoint();
        List<Profit> profits = new ArrayList<>();
        List<Trade> trades = new ArrayList<>();
        for(int i = 0; i < indexDatas.size(); i++){
            float max = getMax(i,ma,indexDatas);
            float average = getMA(i,ma,indexDatas);
            IndexData indexData = indexDatas.get(i);
            float closePoint = indexData.getClosePoint();
            if(closePoint > average * buyRate){
                if(holdNum == 0){
                    holdNum = currentMoney / closePoint;
                    currentMoney = 0;
                    Trade trade = new Trade();
                    trade.setBuyDate(indexData.getDate());
                    trade.setBuyPoint(indexData.getClosePoint());
                    trades.add(trade);
                }
            }else if(closePoint < max * sellRate){
                if(holdNum != 0){
                    currentMoney = holdNum * closePoint * (1-serviceCharge);
                    holdNum = 0;
                    Trade trade = trades.get(trades.size()-1);
                    trade.setSellDate(indexData.getDate());
                    trade.setSellPoint(indexData.getClosePoint());
                    float rate = currentMoney / initialMoney;
                    trade.setRate(rate);
                    if(trade.getSellPoint() - trade.getBuyPoint() > 0){
                        winNum ++;
                        totalWinRate += (trade.getSellPoint() - trade.getBuyPoint()) / trade.getBuyPoint();
                    }else{
                        loseNum ++;
                        totalLoseRate += (trade.getSellPoint() - trade.getBuyPoint()) / trade.getBuyPoint();
                    }
                }
            }
            if(holdNum != 0){
                value = holdNum * closePoint;
            }else{
                value = currentMoney;
            }
            Profit profit = new Profit();
            profit.setDate(indexData.getDate());
            profit.setValue(init * value / initialMoney);
            profits.add(profit);

        }
        averageWinRate = totalWinRate / winNum;
        averageLoseRate = totalLoseRate / loseNum;
        Map<String,Object> map = new HashMap<>();
        map.put("profits", profits);
        map.put("trades",trades);
        map.put("averageWinRate",averageWinRate);
        map.put("averageLoseRate",averageLoseRate);
        map.put("totalWinRate",totalWinRate);
        map.put("totalLoseRate",totalLoseRate);
        map.put("winNum",winNum);
        map.put("loseNum",loseNum);
        return map;
    }

    private static float getMax(int i, int day, List<IndexData> indexData){
        int start = i - 1 - day;
        if(start < 0)
            start = 0;
        int now = i - 1;
        float max = 0;
        for(int j = start; j < now; j++){
            if(indexData.get(j).getClosePoint() > max)
                max = indexData.get(j).getClosePoint();
        }
        return max;
    }

    private static float getMA(int i, int day, List<IndexData> indexData){
        int start = i - 1 - day;
        if(start < 0)
            start = 0;
        int now = i - 1;
        float sum = 0;
        for(int j = start; j < now; j++){
            sum += indexData.get(j).getClosePoint();
        }
        return sum/(now-start);
    }

    private int getYear(String strDate){
        String strYear = StrUtil.subBefore(strDate,"-",false);
        return Integer.parseInt(strYear);
    }

    private float getAnnualIndexProfit(List<IndexData> indexDatas, int year){
        IndexData first = null;
        IndexData last = null;
        for(IndexData indexData : indexDatas){
            if(getYear(indexData.getDate()) == year){
                if(first == null)
                    first = indexData;
                last = indexData;
            }
        }
        return (last.getClosePoint() - first.getClosePoint()) / first.getClosePoint();
    }

    private float getAnnualTrendProfit(List<Profit> trendDatas, int year){
        Profit first = null;
        Profit last = null;
        for(Profit profit : trendDatas){
            if(getYear(profit.getDate()) == year){
                if(first == null)
                    first = profit;
                last = profit;
            }
        }
        return (last.getValue() - first.getValue()) / first.getValue();
    }

    public List<AnnualProfit> getAnnualProfit(List<IndexData> indexDataList, List<Profit> profitList){
        Set<Integer> usedYears = new HashSet<>();
        List<AnnualProfit> annualProfitList = new ArrayList<>();
        for(int i = 0; i < profitList.size(); i++){
            int year = getYear(profitList.get(i).getDate());
            if(usedYears.contains(year)){
                continue;
            }else{
                usedYears.add(year);
            }
            float indexProfit = getAnnualIndexProfit(indexDataList,year);
            float trendProfit = getAnnualTrendProfit(profitList,year);
            AnnualProfit annualProfit = new AnnualProfit();
            annualProfit.setIndexProfit(indexProfit);
            annualProfit.setTrendProfit(trendProfit);
            annualProfit.setYear(year);
            annualProfitList.add(annualProfit);
        }
        return annualProfitList;
    }

}
