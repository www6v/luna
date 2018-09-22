package com.yhd.arch.tuna.dto;

/**
 * Created by root on 4/21/16.
 */
public class InvokeClass {
    private long invokeCount;
    private int costTime;
    private long totalCostTime;

    public InvokeClass(){
        this.invokeCount = 0;
        this.costTime = 0;
        this.totalCostTime = 0;
    }

    public InvokeClass(long invokeCount, int costTime, long totalCostTime){
        this.invokeCount = invokeCount;
        this.costTime = costTime;
        this.totalCostTime = totalCostTime;
    }

    public long getInvokeCount(){
        return invokeCount;
    }

    public void setInvokeCount(long invokeCount){
        this.invokeCount = invokeCount;
    }

    public int getCostTime(){
        return costTime;
    }

    public void setCostTime(int costTime){
        this.costTime = costTime;
    }

    public long getTotalCostTime(){
        return totalCostTime;
    }

    public void setTotalCostTime(long totalCostTime){
        this.totalCostTime = totalCostTime;
    }

    public long addAndGet(long totalCostTime){
        return this.totalCostTime + totalCostTime;
    }


    public String toString(){
        return "InvokeClass:"+"["+"invokeCount = "+ invokeCount+", costTime = "+costTime+", totalCostTime = "+totalCostTime+"]";
    }
}
