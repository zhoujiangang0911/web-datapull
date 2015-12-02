package com.mobile.future.util;

/**
 * 功能：分页信息类
 * User: qilei
 * Date: 12-8-24
 * Time: 上午10:51
 */
public class Page {
    //总页数
    private int pageConut = 0;
    //每页显示记录条数
    private int pageSize = 0;
    //是否首页
    private boolean firstPageFlag = false;
    //是否末页
    private boolean lastPageFlag = false;
    //总记录条数
    private int infoCount = 0;
    //当前页数
    private int currentPage = 1;


    public int getPageConut() {
        return pageConut;
    }

    public void setPageConut(int pageConut) {
        this.pageConut = pageConut;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public boolean isFirstPageFlag() {
        if (pageConut==1){
            this.firstPageFlag = false;
        }else
        {
        	if(pageConut>1){
                if (currentPage>1&&currentPage<=pageConut){
                    this.firstPageFlag = true;
                }
            }
        }
        
        return firstPageFlag;
    }

    public void setFirstPageFlag(boolean firstPageFlag) {
        this.firstPageFlag = firstPageFlag;
    }

    public boolean isLastPageFlag() {
        if (pageConut==1){
            this.lastPageFlag = false;
        }
        if(pageConut>1){
            if (currentPage<pageConut){
                this.lastPageFlag = true;
            }else if(currentPage==pageConut){
                this.lastPageFlag=false;
            }
        }
        return lastPageFlag;
    }

    public void setLastPageFlag(boolean lastPageFlag) {
        this.lastPageFlag = lastPageFlag;
    }

    public int getInfoCount() {
        return infoCount;
    }

    public void setInfoCount(int infoCount) {
        this.infoCount = infoCount;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

}
