package com.hotel.reservation.web.paging;

import lombok.Getter;
import org.springframework.data.domain.Page;
@Getter
public class PageDto<T> {
    private int nowPage;
    private int totalPage;
    private int startPage;
    private int maxPage;
    private int endPage;

    public PageDto(Page<T> result) {
        this.nowPage = result.getNumber() + 1;
        this.totalPage = result.getTotalPages();
        this.startPage = ((nowPage-1)/10)*10+1;
        this.maxPage = (((nowPage-1)/10)+1)*10;
        this.endPage = Math.min(maxPage, totalPage);
    }
}
