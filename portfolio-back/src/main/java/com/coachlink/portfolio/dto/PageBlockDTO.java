package com.coachlink.portfolio.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
public class PageBlockDTO<DTO, EN> {

    private List<DTO> dtoList;

    private int pageNo;               // 현재 페이지
    private int pageSize;             // 페이지당 요소 수
    private long totalPostCnt;        // 전체 게시물 수
    private int totalPageCnt;         // 전체 페이지 수

    private int pageCntPerBlock = 5; // 블록당 페이지 수
    private int totalPagingBlockCnt;  // 전체 블록 수
    private int pageBlockOfCurrent;   // 현재 블록 번호
    private int startNumOfCurrentPagingBlock; // 현재 블록 시작 페이지
    private int endNumOfCurrentPagingBlock;   // 현재 블록 끝 페이지

    private Long newNotice;

    public PageBlockDTO(Page<EN> result, Function<EN, DTO> fn, long newNotice) {
        this.dtoList = result.stream().map(fn).collect(Collectors.toList());

        this.pageNo = result.getNumber() + 1;
        this.pageSize = result.getSize();
        this.totalPostCnt = result.getTotalElements();
        this.totalPageCnt = result.getTotalPages();

        this.totalPagingBlockCnt = (int) Math.ceil((double) totalPageCnt / pageCntPerBlock);
        this.pageBlockOfCurrent = (int) Math.ceil((double) pageNo / pageCntPerBlock);
        this.startNumOfCurrentPagingBlock = (pageBlockOfCurrent - 1) * pageCntPerBlock + 1;
        this.endNumOfCurrentPagingBlock = Math.min(totalPageCnt, pageBlockOfCurrent * pageCntPerBlock);

        this.newNotice = newNotice;
    }

    @Builder
    public PageBlockDTO(List<DTO> dtoList, int pageNo, int pageSize, long totalPostCnt, int totalPageCnt,
                           int pageCntPerBlock, int totalPagingBlockCnt, int pageBlockOfCurrent,
                           int startNumOfCurrentPagingBlock, int endNumOfCurrentPagingBlock, long newNotice) {
        this.dtoList = dtoList;
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.totalPostCnt = totalPostCnt;
        this.totalPageCnt = totalPageCnt;
        this.pageCntPerBlock = pageCntPerBlock;
        this.totalPagingBlockCnt = totalPagingBlockCnt;
        this.pageBlockOfCurrent = pageBlockOfCurrent;
        this.startNumOfCurrentPagingBlock = startNumOfCurrentPagingBlock;
        this.endNumOfCurrentPagingBlock = endNumOfCurrentPagingBlock;
        this.newNotice = newNotice;
    }
}

