package com.skywilling.cn.web.model.view;

import java.io.Serializable;
import java.util.List;

public class PageView implements Serializable {

  private static final long serialVersionUID = 8573116001542395787L;
  private List list;
  private int pageNum;
  private int pageSize;
  private int size;
  private long total;
  private int pages;

  public List getList() {
    return list;
  }

  public void setList(List list) {
    this.list = list;
  }

  public int getPageNum() {
    return pageNum;
  }

  public void setPageNum(int pageNum) {
    this.pageNum = pageNum;
  }

  public int getPageSize() {
    return pageSize;
  }

  public void setPageSize(int pageSize) {
    this.pageSize = pageSize;
  }

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }

  public long getTotal() {
    return total;
  }

  public void setTotal(long total) {
    this.total = total;
  }

  public int getPages() {
    return pages;
  }

  public void setPages(int pages) {
    this.pages = pages;
  }
}
