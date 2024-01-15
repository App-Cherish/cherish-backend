package com.cherish.backend.service;

import com.cherish.backend.controller.dto.request.BackUpDiaryRequest;
import com.cherish.backend.controller.dto.request.DiaryRequest;
import com.cherish.backend.controller.dto.request.FirstTimeBackUpDiaryRequest;
import com.cherish.backend.controller.dto.response.BackUpDairyResponse;
import com.cherish.backend.controller.dto.response.DiaryResponse;
import com.cherish.backend.domain.*;
import com.cherish.backend.exception.ExistBackUpHistory;
import com.cherish.backend.exception.NotExistAvatarException;
import com.cherish.backend.exception.NotExistBackUpException;
import com.cherish.backend.repositroy.AvatarRepository;
import com.cherish.backend.repositroy.BackUpRepository;
import com.cherish.backend.repositroy.DiaryRepository;
import com.cherish.backend.util.DateFormattingUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class DiaryServiceTest {

    Avatar avatar;
    BackUp backUp;

    @Autowired
    AvatarRepository avatarRepository;

    @Autowired
    BackUpRepository backUpRepository;

    @Autowired
    DiaryRepository diaryRepository;

    @Autowired
    DiaryService diaryService;

    @BeforeEach
    public void init() {
        avatar = Avatar.of("name1", LocalDate.now(), Gender.MALE);
        backUp = BackUp.of("os1","device1",3,avatar);

        avatarRepository.save(avatar);
        backUpRepository.save(backUp);
    }

    @Test
    @DisplayName("기존에 백업 기록이 없는 경우 일기를 저장하면 백업과 관련된 정보를 백업과 관련된 정보를 출력한다.")
    public void firstTimeBackUpTest1() throws Exception {
        Avatar avatar2 = Avatar.of("name2", LocalDate.now(), Gender.MALE);
        avatarRepository.save(avatar2);
        //given
        List<DiaryRequest> dtos = new ArrayList<>();

        dtos.add(new DiaryRequest(null, DiaryKind.FREE, "title1", "content1", DateFormattingUtil.localDateTimeToString(LocalDateTime.now())));
        dtos.add(new DiaryRequest(null, DiaryKind.FREE, "title2", "content2", DateFormattingUtil.localDateTimeToString(LocalDateTime.now())));
        dtos.add(new DiaryRequest(null, DiaryKind.FREE, "title3", "content3", DateFormattingUtil.localDateTimeToString(LocalDateTime.now())));

        FirstTimeBackUpDiaryRequest firstTimeBackUpDto = new FirstTimeBackUpDiaryRequest(dtos, "device1", "deviceId1", "os1");

        //when
        BackUpDairyResponse backUpDairyResponse = diaryService.firstTimeBackUp(firstTimeBackUpDto, avatar2.getId());
        //then
        BackUp backUp = backUpRepository.findBackUpByIdLatest(avatar2.getId()).get();
        BackUpDairyResponse expectedResponse = new BackUpDairyResponse(backUp.getOsVersion(), backUp.getDeviceType(), backUp.getId(), backUp.getCreatedDate(), backUp.getDiaryCount());

        assertThat(dtos.size()).isEqualTo(3);
        assertThat(backUpDairyResponse.getBackUpId()).isEqualTo(expectedResponse.getBackUpId());
        assertThat(backUpDairyResponse.getSaveTime()).isEqualTo(expectedResponse.getSaveTime());
        assertThat(backUpDairyResponse.getOsVersion()).isEqualTo(expectedResponse.getOsVersion());
        assertThat(backUpDairyResponse.getDeviceType()).isEqualTo(expectedResponse.getDeviceType());
    }

    @Test
    @DisplayName("처음 백업과 관련된 정보를 저장하면 해당 정보가 데이터베이스 있어야 하고 요청한 요청값과 동일해야 한다.")
    public void firstTimeBackUpTest2() throws Exception {
        Avatar avatar2 = Avatar.of("name2", LocalDate.now(), Gender.MALE);
        avatarRepository.save(avatar2);

        //given
        List<DiaryRequest> diaryRequestList = new ArrayList<>();

        diaryRequestList.add(new DiaryRequest(null, DiaryKind.FREE, "title1", "content1", DateFormattingUtil.localDateTimeToString(LocalDateTime.now())));
        diaryRequestList.add(new DiaryRequest(null, DiaryKind.FREE, "title2", "content2", DateFormattingUtil.localDateTimeToString(LocalDateTime.now())));
        diaryRequestList.add(new DiaryRequest(null, DiaryKind.FREE, "title3", "content3", DateFormattingUtil.localDateTimeToString(LocalDateTime.now())));
        FirstTimeBackUpDiaryRequest firstTimeBackUpDiaryRequest = new FirstTimeBackUpDiaryRequest(diaryRequestList, "device1", "deviceId1", "os1");

        BackUpDairyResponse backUpDairyResponse = diaryService.firstTimeBackUp(firstTimeBackUpDiaryRequest, avatar2.getId());
        //when
        BackUp findBackUp = backUpRepository.findBackUpByIdLatest(avatar2.getId()).get();
        List<Diary> diaryList = diaryRepository.findDiariesByIdAndAvatarIdAndBackUpId(findBackUp.getId(), avatar2.getId());
        //then
        assertThat(diaryList.size()).isEqualTo(3);
        assertThat(diaryList.get(0).getBackUp().getId()).isEqualTo(backUpDairyResponse.getBackUpId());
        assertThat(diaryList).extracting("title", String.class).contains(diaryRequestList.get(0).getTitle(), diaryRequestList.get(1).getTitle(), diaryRequestList.get(2).getTitle());
        assertThat(diaryList).extracting("content", String.class).contains(diaryRequestList.get(0).getContent(), diaryRequestList.get(1).getContent(), diaryRequestList.get(2).getContent());
        assertThat(diaryList.get(0).getBackUp().getId()).isEqualTo(findBackUp.getId());
    }

    @Test
    @DisplayName("만약 처음 백업을 할 시에 이미 백업 기록이 존재하면 예외를 출력한다.")
    public void firstTimeBackUpFailTest1() throws Exception {
        //given
        BackUp back = BackUp.of("iphone15", "iphone13", 13, avatar);
        backUpRepository.save(back);

        //given
        List<DiaryRequest> diaryRequestList = new ArrayList<>();

        diaryRequestList.add(new DiaryRequest(null, DiaryKind.FREE, "title1", "content1", "1999-11-11"));
        diaryRequestList.add(new DiaryRequest(null, DiaryKind.FREE, "title2", "content2", "1999-11-11"));
        diaryRequestList.add(new DiaryRequest(null, DiaryKind.FREE, "title3", "content3", "1999-11-11"));
        FirstTimeBackUpDiaryRequest firstTimeBackUpDiaryRequest = new FirstTimeBackUpDiaryRequest(diaryRequestList, "device1", "deviceId1", "os1");
        //when
        //then
        assertThrows(ExistBackUpHistory.class, () -> diaryService.firstTimeBackUp(firstTimeBackUpDiaryRequest, avatar.getId()));
    }

    @Test
    @DisplayName("만약 처음 백업을 할 시에 아바타 아이디가 존재하지 않으면 예외를 출력한다.")
    public void firstTimeBackUpFailTest2() throws Exception {
        //given
        BackUp back = BackUp.of("iphone15", "iphone13", 13, avatar);
        backUpRepository.save(back);

        //given
        List<DiaryRequest> diaryRequestList = new ArrayList<>();

        diaryRequestList.add(new DiaryRequest(null, DiaryKind.FREE, "title1", "content1", "1999-11-11"));
        diaryRequestList.add(new DiaryRequest(null, DiaryKind.FREE, "title2", "content2", "1999-11-11"));
        diaryRequestList.add(new DiaryRequest(null, DiaryKind.FREE, "title3", "content3", "1999-11-11"));
        FirstTimeBackUpDiaryRequest firstTimeBackUpDiaryRequest = new FirstTimeBackUpDiaryRequest(diaryRequestList, "device1", "deviceId1", "os1");
        //when
        //then
        assertThrows(NotExistAvatarException.class, () -> diaryService.firstTimeBackUp(firstTimeBackUpDiaryRequest, null));
    }

    @Test
    @DisplayName("기존에 백업 기록이 있는 경우 요청에서 기존 백업 정보를 입력하고 백업과 관련된 정보를 출력한다.")
    public void backUpTest1() throws Exception {
        //given
        Diary diary1 = Diary.of(DiaryKind.FREE, "title1", "content1", LocalDateTime.now(), "device1", "deviceId1", avatar, backUp);
        Diary diary2 = Diary.of(DiaryKind.FREE, "title2", "content2", LocalDateTime.now(), "device1", "deviceId1", avatar, backUp);
        Diary diary3 = Diary.of(DiaryKind.FREE, "title3", "content3", LocalDateTime.now(), "device1", "deviceId1", avatar, backUp);

        List<Diary> diaryList = new ArrayList<>();
        diaryList.add(diary1);
        diaryList.add(diary2);
        diaryList.add(diary3);
        diaryRepository.saveAll(diaryList);
        //when
        List<DiaryRequest> diaryRequests = new ArrayList<>();

        diaryRequests.add(new DiaryRequest(diary1.getId(), diary1.getKind(), diary1.getTitle(), diary1.getContent(), DateFormattingUtil.localDateTimeToString(diary1.getWritingDate())));
        diaryRequests.add(new DiaryRequest(diary2.getId(), diary2.getKind(), diary2.getTitle(), diary2.getContent(), DateFormattingUtil.localDateTimeToString(diary2.getWritingDate())));
        diaryRequests.add(new DiaryRequest(diary3.getId(), diary3.getKind(), diary3.getTitle(), diary3.getContent(), DateFormattingUtil.localDateTimeToString(diary3.getWritingDate())));
        BackUp findBackUpBefore = backUpRepository.findBackUpByIdLatest(avatar.getId()).get();
        BackUpDiaryRequest diaryRequest = new BackUpDiaryRequest(diaryRequests, "device1", "deviceId1", "os1", findBackUpBefore.getId());
        BackUpDairyResponse backUpDairyResponse = diaryService.backUp(diaryRequest, avatar.getId());
        BackUp findBackUpAfter = backUpRepository.findBackUpByIdLatest(avatar.getId()).get();

        //then
        assertThat(backUpDairyResponse.getBackUpId()).isEqualTo(findBackUpAfter.getId());
        assertThat(backUpDairyResponse.getDeviceType()).isEqualTo(findBackUpAfter.getDeviceType());
        assertThat(backUpDairyResponse.getOsVersion()).isEqualTo(findBackUpAfter.getOsVersion());
        assertThat(backUpDairyResponse.getSaveTime()).isEqualTo(DateFormattingUtil.localDateTimeToString(findBackUpAfter.getCreatedDate()));
        assertThat(backUpDairyResponse.getCount()).isEqualTo(diaryRequests.size());

    }

    @Test
    @DisplayName("기존에 백업 기록이 있는 경우 요청과 동일한 아이디의 경우 백업 아이디가 변경되어야 하며, 새로운 요청의 경우 정상적으로 등록 되어야 한다.")
    public void backUpTest2() throws Exception {
        //given
        Diary diary1 = Diary.of(DiaryKind.FREE, "title1", "content1", LocalDateTime.now(), "device1", "deviceId1", avatar, backUp);
        Diary diary2 = Diary.of(DiaryKind.FREE, "title2", "content2", LocalDateTime.now(), "device1", "deviceId1", avatar, backUp);
        Diary diary3 = Diary.of(DiaryKind.FREE, "title3", "content3", LocalDateTime.now(), "device1", "deviceId1", avatar, backUp);

        List<Diary> diaryList = new ArrayList<>();
        diaryList.add(diary1);
        diaryList.add(diary2);
        diaryList.add(diary3);
        diaryRepository.saveAll(diaryList);
        //when
        List<DiaryRequest> diaryRequests = new ArrayList<>();
        diaryRequests.add(new DiaryRequest(diary1.getId(),diary1.getKind(),diary1.getTitle(),diary1.getContent(),DateFormattingUtil.localDateTimeToString(diary1.getWritingDate())));
        diaryRequests.add(new DiaryRequest(null,DiaryKind.FREE,"title3","content3",DateFormattingUtil.localDateTimeToString(LocalDateTime.now())));

        BackUp findBackUpBefore = backUpRepository.findBackUpByIdLatest(avatar.getId()).get();
        BackUpDiaryRequest diaryRequest = new BackUpDiaryRequest(diaryRequests, "device1", "deviceId1", "os1", findBackUpBefore.getId());
        BackUpDairyResponse backUpDairyResponse = diaryService.backUp(diaryRequest, avatar.getId());
        BackUp findBackUpAfter = backUpRepository.findBackUpByIdLatest(avatar.getId()).get();

        List<Diary> findDiaryList = diaryRepository.findDiariesByIdAndAvatarIdAndBackUpId(findBackUpAfter.getId(), avatar.getId());
        //then
        assertThat(findDiaryList.size()).isEqualTo(backUpDairyResponse.getCount());
        assertThat(backUpDairyResponse.getBackUpId()).isEqualTo(findBackUpAfter.getId());
        assertThat(findDiaryList).extracting("title").contains(diaryRequests.get(0).getTitle(),diaryRequests.get(1).getTitle());
    }

    @Test
    @DisplayName("백업에 성공한 경우 기존의 백업 엔티티는 비활성화 된다.")
    public void backUpTest3() throws Exception {
        Diary diary1 = Diary.of(DiaryKind.FREE, "title1", "content1", LocalDateTime.now(), "device1", "deviceId1", avatar, backUp);
        Diary diary2 = Diary.of(DiaryKind.FREE, "title2", "content2", LocalDateTime.now(), "device1", "deviceId1", avatar, backUp);
        Diary diary3 = Diary.of(DiaryKind.FREE, "title3", "content3", LocalDateTime.now(), "device1", "deviceId1", avatar, backUp);

        List<Diary> diaryList = new ArrayList<>();
        diaryList.add(diary1);
        diaryList.add(diary2);
        diaryList.add(diary3);
        diaryRepository.saveAll(diaryList);
        //when
        List<DiaryRequest> dtos = new ArrayList<>();
        dtos.add(new DiaryRequest(diary1.getId(),diary1.getKind(),diary1.getTitle(),diary1.getContent(),DateFormattingUtil.localDateTimeToString(diary1.getWritingDate())));
        dtos.add(new DiaryRequest(null,DiaryKind.FREE,"title3","content3",DateFormattingUtil.localDateTimeToString(LocalDateTime.now())));

        BackUp findBackUpBefore = backUpRepository.findBackUpByIdLatest(avatar.getId()).get();
        BackUpDiaryRequest backUpDiaryRequest = new BackUpDiaryRequest(dtos, "device1", "deviceId1", "os1", findBackUpBefore.getId());
        BackUpDairyResponse backUpDairyResponse = diaryService.backUp(backUpDiaryRequest, avatar.getId());
        //then
        BackUp findBackUpAfter = backUpRepository.findBackUpByIdLatest(avatar.getId()).get();
        assertThat(findBackUpBefore.getActive()).isEqualTo(0);
        assertThat(findBackUpAfter.getActive()).isEqualTo(1);
        assertThat(findBackUpAfter.getId()).isNotEqualTo(findBackUpBefore.getId());

    }

    @Test
    @DisplayName("백업 할 떄에 만약 avatar엔티티가 존재하지 않으면 예외를 출력한다.")
    public void backUpFailTest1() throws Exception {
        Diary diary1 = Diary.of(DiaryKind.FREE, "title1", "content1", LocalDateTime.now(), "device1", "deviceId1", avatar, backUp);
        Diary diary2 = Diary.of(DiaryKind.FREE, "title2", "content2", LocalDateTime.now(), "device1", "deviceId1", avatar, backUp);
        Diary diary3 = Diary.of(DiaryKind.FREE, "title3", "content3", LocalDateTime.now(), "device1", "deviceId1", avatar, backUp);

        List<Diary> diaryList = new ArrayList<>();
        diaryList.add(diary1);
        diaryList.add(diary2);
        diaryList.add(diary3);
        diaryRepository.saveAll(diaryList);
        //when
        List<DiaryRequest> dtos = new ArrayList<>();
        dtos.add(new DiaryRequest(diary1.getId(),diary1.getKind(),diary1.getTitle(),diary1.getContent(),DateFormattingUtil.localDateTimeToString(diary1.getWritingDate())));
        dtos.add(new DiaryRequest(null,DiaryKind.FREE,"title3","content3",DateFormattingUtil.localDateTimeToString(LocalDateTime.now())));

        BackUpDiaryRequest backUpDiaryRequest = new BackUpDiaryRequest(dtos, "device1", "deviceId1", "os1", backUp.getId());
        //then
        assertThrows(NotExistAvatarException.class , () -> diaryService.backUp(backUpDiaryRequest, 999999L));
    }

    @Test
    @DisplayName("백업 할 때에 만약 backUp엔티티가 존재하지 않으면 예외를 출력한다.")
    public void backUpFailTest2() throws Exception {
        Diary diary1 = Diary.of(DiaryKind.FREE, "title1", "content1", LocalDateTime.now(), "device1", "deviceId1", avatar, backUp);
        Diary diary2 = Diary.of(DiaryKind.FREE, "title2", "content2", LocalDateTime.now(), "device1", "deviceId1", avatar, backUp);
        Diary diary3 = Diary.of(DiaryKind.FREE, "title3", "content3", LocalDateTime.now(), "device1", "deviceId1", avatar, backUp);

        List<Diary> diaryList = new ArrayList<>();
        diaryList.add(diary1);
        diaryList.add(diary2);
        diaryList.add(diary3);
        diaryRepository.saveAll(diaryList);
        //when
        List<DiaryRequest> dtos = new ArrayList<>();
        dtos.add(new DiaryRequest(diary1.getId(),diary1.getKind(),diary1.getTitle(),diary1.getContent(),DateFormattingUtil.localDateTimeToString(diary1.getWritingDate())));
        dtos.add(new DiaryRequest(null,DiaryKind.FREE,"title3","content3",DateFormattingUtil.localDateTimeToString(LocalDateTime.now())));

        BackUpDiaryRequest backUpDiaryRequest = new BackUpDiaryRequest(dtos, "device1", "deviceId1", "os1", "123123123");
        //then

        assertThrows(NotExistBackUpException.class,()-> diaryService.backUp(backUpDiaryRequest, avatar.getId()));
    }


    @Test
    @DisplayName("백업 아이디와 avatarid로 조회시에 최신 백업으로 저장되어있는 일기들을 가져온다.")
    public void getRecentDiaryListTest() throws Exception {
        //given
        Diary diary1 = Diary.of(DiaryKind.FREE, "title1", "content1", LocalDateTime.now(), "device1", "deviceId1", avatar, backUp);
        Diary diary2 = Diary.of(DiaryKind.FREE, "title2", "content2", LocalDateTime.now(), "device1", "deviceId1", avatar, backUp);
        Diary diary3 = Diary.of(DiaryKind.FREE, "title3", "content3", LocalDateTime.now(), "device1", "deviceId1", avatar, backUp);

        List<Diary> diaryList = new ArrayList<>();
        diaryList.add(diary1);
        diaryList.add(diary2);
        diaryList.add(diary3);
        diaryRepository.saveAll(diaryList);
        //when
        List<DiaryResponse> recentDiaryList = diaryService.getRecentDiaryList(backUp.getId(), avatar.getId());

        //then
        assertThat(recentDiaryList.size()).isEqualTo(diaryList.size());
    }

    @Test
    @DisplayName("백업  아이디로 조회 시에 일기가 존재하지 않을 시에 예외를 출력한다.")
    public void getRecentDiaryListFailTest() throws Exception {
        //given
        //when
        //then
        assertThrows(NotExistBackUpException.class,()->diaryService.getRecentDiaryList("asdasdas",avatar.getId()));
    }


}