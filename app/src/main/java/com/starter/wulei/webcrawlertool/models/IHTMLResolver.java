package com.starter.wulei.webcrawlertool.models;

/**
 * Created by wulei on 2017/2/23.
 */

public interface IHTMLResolver {
    String getCurrentPage();
    boolean haveNextPage();

}
