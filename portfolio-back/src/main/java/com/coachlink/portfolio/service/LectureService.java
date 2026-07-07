package com.coachlink.portfolio.service;

import com.coachlink.portfolio.dto.*;
import com.coachlink.portfolio.entity.Lecture;
import com.coachlink.portfolio.entity.LectureImg;
import com.coachlink.portfolio.util.LectureStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface LectureService {

    Long registerLecture(LectureDTO lectureDTO) throws Exception;

    Long updateLecture(LectureDTO lectureDTO);

    void cancelByLectureId(Long lectureId, String username);

    void deleteByLectureId(Long lectureId, String username);

    PageBlockDTO<LectureDTO, Object[]> getLectureList(PageRequestDTO pageRequestDTO) throws Exception;

    default Map<String, Object> dtoToEntity(LectureDTO lectureDTO) {
        Map<String, Object> result = new HashMap<>();

        Lecture lecture = Lecture.builder()
                .lectureId(lectureDTO.getLectureId())
                .facilityName(lectureDTO.getFacilityName())
                .lectureName(lectureDTO.getLectureName())
                .lectureContent(lectureDTO.getLectureContent())
                .lectureStartTime(lectureDTO.getLectureStartTime())
                .lectureEndTime(lectureDTO.getLectureEndTime())
                .minPeople(lectureDTO.getMinPeople())
                .maxPeople(lectureDTO.getMaxPeople())
                .coordsX((Double) lectureDTO.getCoordsX())
                .coordsY((Double) lectureDTO.getCoordsY())
                .build();
        result.put("lecture", lecture);

        List<String> images = lectureDTO.getImages();
        Integer mainIndex = lectureDTO.getMainIndex(); // 메인 이미지 index (프론트에서 준 거)

        if (images != null && !images.isEmpty()) {
            List<LectureImg> lectureImgList = new ArrayList<>();

            for (int i = 0; i < images.size(); i++) {
                String dataUrl = images.get(i);

                if (dataUrl == null || !dataUrl.startsWith("data:"))
                    continue;

                // "data:image/jpeg;base64,..." 분리
                String[] parts = dataUrl.split(",", 2);
                if (parts.length != 2)
                    continue;

                String meta = parts[0];
                String base64 = parts[1];

                // contentType 추출
                String contentType = null;
                int colon = meta.indexOf(":");
                int semi = meta.indexOf(";");
                if (colon != -1 && semi != -1) {
                    contentType = meta.substring(colon + 1, semi);
                }

                // originalName 자동 생성
                String originalName = "upload_" + (i + 1) + "." + getExtFromContentType(contentType);

                boolean isMainImg = (mainIndex != null && mainIndex == i);

                LectureImg lectureImg = LectureImg.builder()
                        .originalName(originalName)
                        .contentType(contentType)
                        .size((long) base64.length()) // byte 길이 필요하면 decode 해서 계산 가능
                        .base64(base64)
                        .isMainImg(isMainImg)
                        .lecture(lecture)
                        .build();

                lectureImgList.add(lectureImg);
            }

            result.put("lectureImgList", lectureImgList);
        }
        return result;
    }

    default String getExtFromContentType(String contentType) {
        if (contentType == null)
            return "jpg";
        switch (contentType) {
            case "image/png":
                return "png";
            case "image/jpeg":
                return "jpg";
            case "image/jpg":
                return "jpg";
            case "image/webp":
                return "webp";
            default:
                return "jpg";
        }
    };

    default LectureDTO entityToDTO(Lecture lecture, List<LectureImg> lectureImgs) {
        LectureDTO lectureDTO = LectureDTO.builder()
                .lectureId(lecture.getLectureId())
                .username(lecture.getMember().getUsername())
                .facilityName(lecture.getFacilityName())
                .mainName(lecture.getSportName().getMainName())
                .subName(lecture.getSportName().getSubName())
                .lectureName(lecture.getLectureName())
                .lectureContent(lecture.getLectureContent())
                .lectureStartTime(lecture.getLectureStartTime())
                .lectureEndTime(lecture.getLectureEndTime())
                .lectureStatus(String.valueOf(lecture.getLectureStatus()))
                .minPeople(lecture.getMinPeople())
                .maxPeople(lecture.getMaxPeople())
                .createdAt(lecture.getCreatedAt())
                .updatedAt(lecture.getUpdatedAt())
                .coordsX(lecture.getCoordsX())
                .coordsY(lecture.getCoordsY())
                .build();

        List<LectureImgDTO> lectureImageDTOList = new ArrayList<>();
        if (lectureImgs != null && !lectureImgs.isEmpty()) {
            lectureImageDTOList = lectureImgs.stream()
                    .map(lectureImg -> LectureImgDTO.builder()
                            .lectureImgId(lectureImg.getLectureImgId())
                            .originalName(lectureImg.getOriginalName())
                            .contentType(lectureImg.getContentType())
                            .size(lectureImg.getSize())
                            .base64(lectureImg.getBase64())
                            .isMainImg(lectureImg.isMainImg())
                            .build())
                    .collect(Collectors.toList());
        }

        lectureDTO.setLecImgList(lectureImageDTOList);
        return lectureDTO;
    }

    LectureDTO getLectureByLectureId(Long lectureId);

    PageBlockDTO<LectureDTO, Object[]> searchLecture(PageRequestDTO pageRequestDTO, Double coordsXUser,
                                                        Double coordsYUser, List<Long> sports,
                                                        String query, LocalDateTime startTimeRange, LocalDateTime endTimeRange, String sort, int page);

    void changeLectureState(Long lectureId, LectureStatus status) throws Exception;

    PageBlockDTO<LectureDTO, Object[]> getPlayerLectureList(PageRequestDTO pageRequestDTO, String playerName);
}
