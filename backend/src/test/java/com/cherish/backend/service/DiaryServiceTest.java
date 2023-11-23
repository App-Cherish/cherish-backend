package com.cherish.backend.service;

import com.cherish.backend.domain.*;
import com.cherish.backend.exception.ExistBackUpHistory;
import com.cherish.backend.exception.NotExistAvatarException;
import com.cherish.backend.exception.NotExistBackUpException;
import com.cherish.backend.repositroy.AvatarRepository;
import com.cherish.backend.repositroy.BackUpRepository;
import com.cherish.backend.repositroy.DiaryRepository;
import com.cherish.backend.service.dto.BackUpDto;
import com.cherish.backend.service.dto.DiaryDto;
import com.cherish.backend.service.dto.DiarySaveResponseDto;
import com.cherish.backend.service.dto.FirstTimeBackUpDto;
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
        backUp = BackUp.of("backUpid1","os1","device1",3,avatar);

        avatarRepository.save(avatar);
        backUpRepository.save(backUp);
    }

    @Test
    @DisplayName("기존에 백업 기록이 없는 경우 일기를 저장하면 백업과 관련된 정보를 백업과 관련된 정보를 출력한다.")
    public void firstTimeBackUpTest1() throws Exception {
        Avatar avatar2 = Avatar.of("name2", LocalDate.now(), Gender.MALE);
        avatarRepository.save(avatar2);
        //given
        List<DiaryDto> dtos = new ArrayList<>();

        dtos.add(new DiaryDto(DiaryKind.FREE,"title1","content1",LocalDateTime.now()));
        dtos.add(new DiaryDto(DiaryKind.FREE,"title2","content2",LocalDateTime.now()));
        dtos.add(new DiaryDto(DiaryKind.FREE,"title3","content3",LocalDateTime.now()));

        FirstTimeBackUpDto firstTimeBackUpDto = new FirstTimeBackUpDto(dtos,"device1","deviceId1","os1");

        //when
        DiarySaveResponseDto diarySaveResponseDto = diaryService.firstTimeBackUp(firstTimeBackUpDto, avatar2.getId());
        //then
        BackUp backUp = backUpRepository.findBackUpByIdLatest(avatar2.getId()).get();
        DiarySaveResponseDto returnDto = new DiarySaveResponseDto("os1", "device1", backUp.getId(), 3,backUp.getCreatedDate());

        assertThat(dtos.size()).isEqualTo(3);
        assertThat(diarySaveResponseDto.getBackUpId()).isEqualTo(returnDto.getBackUpId());
        assertThat(diarySaveResponseDto.getSaveTime()).isEqualTo(returnDto.getSaveTime());
        assertThat(diarySaveResponseDto.getOsVersion()).isEqualTo(returnDto.getOsVersion());
        assertThat(diarySaveResponseDto.getDeviceType()).isEqualTo(returnDto.getDeviceType());
    }

    @Test
    @DisplayName("처음 백업과 관련된 정보를 저장하면 해당 정보가 데이터베이스 있어야 하고 요청한 요청값과 동일해야 한다.")
    public void firstTimeBackUpTest2() throws Exception {
        Avatar avatar2 = Avatar.of("name2", LocalDate.now(), Gender.MALE);
        avatarRepository.save(avatar2);

        //given
        List<DiaryDto> dtos = new ArrayList<>();

        dtos.add(new DiaryDto(DiaryKind.FREE,"title1","content1",LocalDateTime.now()));
        dtos.add(new DiaryDto(DiaryKind.FREE,"title2","content2",LocalDateTime.now()));
        dtos.add(new DiaryDto(DiaryKind.FREE,"title3","content3",LocalDateTime.now()));
        FirstTimeBackUpDto firstTimeBackUpDto = new FirstTimeBackUpDto(dtos,"device1","deviceId1","os1");

        DiarySaveResponseDto diarySaveResponseDto = diaryService.firstTimeBackUp(firstTimeBackUpDto, avatar2.getId());
        //when
        BackUp findBackUp = backUpRepository.findBackUpByIdLatest(avatar2.getId()).get();
        List<Diary> diaryList = diaryRepository.findDiariesByIdAndAvatarIdAndBackUpId(findBackUp.getId(), avatar2.getId());
        //then
        assertThat(diaryList.size()).isEqualTo(3);
        assertThat(diaryList.get(0).getBackUp().getId()).isEqualTo(diarySaveResponseDto.getBackUpId());
        assertThat(diaryList).extracting("title",String.class).contains(dtos.get(0).getTitle(),dtos.get(1).getTitle(),dtos.get(2).getTitle());
        assertThat(diaryList).extracting("content",String.class).contains(dtos.get(0).getContent(),dtos.get(1).getContent(),dtos.get(2).getContent());
        assertThat(diaryList.get(0).getBackUp().getId()).isEqualTo(findBackUp.getId());
    }

    @Test
    @DisplayName("만약 처음 백업을 할 시에 이미 백업 기록이 존재하면 예외를 출력한다.")
    public void firstTimeBackUpFailTest1() throws Exception {
        //given
        BackUp back = BackUp.of("test","iphone15","iphone13",13,avatar);
        backUpRepository.save(back);

        //given
        List<DiaryDto> dtos = new ArrayList<>();

        dtos.add(new DiaryDto(DiaryKind.FREE,"title1","content1",LocalDateTime.now()));
        dtos.add(new DiaryDto(DiaryKind.FREE,"title2","content2",LocalDateTime.now()));
        dtos.add(new DiaryDto(DiaryKind.FREE,"title3","content3",LocalDateTime.now()));
        FirstTimeBackUpDto firstTimeBackUpDto = new FirstTimeBackUpDto(dtos,"device1","deviceId1","os1");
        //when
        //then
        assertThrows(ExistBackUpHistory.class,() -> diaryService.firstTimeBackUp(firstTimeBackUpDto, avatar.getId()));
    }

    @Test
    @DisplayName("만약 처음 백업을 할 시에 아바타 아이디가 존재하지 않으면 예외를 출력한다.")
    public void firstTimeBackUpFailTest2() throws Exception {
        //given
        BackUp back = BackUp.of("test","iphone15","iphone13",13,avatar);
        backUpRepository.save(back);

        //given
        List<DiaryDto> dtos = new ArrayList<>();

        dtos.add(new DiaryDto(DiaryKind.FREE,"title1","content1",LocalDateTime.now()));
        dtos.add(new DiaryDto(DiaryKind.FREE,"title2","content2",LocalDateTime.now()));
        dtos.add(new DiaryDto(DiaryKind.FREE,"title3","content3",LocalDateTime.now()));
        FirstTimeBackUpDto firstTimeBackUpDto = new FirstTimeBackUpDto(dtos,"device1","deviceId1","os1");
        //when
        //then
        assertThrows(NotExistAvatarException.class,() -> diaryService.firstTimeBackUp(firstTimeBackUpDto, null));
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
        List<DiaryDto> dtos = new ArrayList<>();

        dtos.add(new DiaryDto(diary1.getId(),diary1.getKind(),diary1.getTitle(),diary1.getContent(),diary1.getWritingDate(),diary1.getDeviceId(),diary1.getDeviceType()));
        dtos.add(new DiaryDto(diary2.getId(),diary2.getKind(),diary2.getTitle(),diary2.getContent(),diary2.getWritingDate(),diary2.getDeviceId(),diary2.getDeviceType()));
        dtos.add(new DiaryDto(diary3.getId(),diary3.getKind(),diary3.getTitle(),diary3.getContent(),diary3.getWritingDate(),diary3.getDeviceId(),diary3.getDeviceType()));
        BackUp findBackUpBefore = backUpRepository.findBackUpByIdLatest(avatar.getId()).get();
        BackUpDto backUpDto = new BackUpDto(dtos, "device1", "deviceId1", "os1", findBackUpBefore.getId());
        DiarySaveResponseDto responseDto = diaryService.backUp(backUpDto, avatar.getId());
        BackUp findBackUpAfter = backUpRepository.findBackUpByIdLatest(avatar.getId()).get();

        //then
        assertThat(responseDto.getBackUpId()).isEqualTo(findBackUpAfter.getId());
        assertThat(responseDto.getDeviceType()).isEqualTo(findBackUpAfter.getDeviceType());
        assertThat(responseDto.getOsVersion()).isEqualTo(findBackUpAfter.getOsVersion());
        assertThat(responseDto.getSaveTime()).isEqualTo(findBackUpAfter.getCreatedDate());
        assertThat(responseDto.getCount()).isEqualTo(dtos.size());

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
        List<DiaryDto> dtos = new ArrayList<>();
        dtos.add(new DiaryDto(diary1.getId(),diary1.getKind(),diary1.getTitle(),diary1.getContent(),diary1.getWritingDate(),diary1.getDeviceId(),diary1.getDeviceType()));
        dtos.add(new DiaryDto(DiaryKind.FREE,"title3","content3",LocalDateTime.now()));

        BackUp findBackUpBefore = backUpRepository.findBackUpByIdLatest(avatar.getId()).get();
        BackUpDto backUpDto = new BackUpDto(dtos, "device1", "deviceId1", "os1", findBackUpBefore.getId());
        DiarySaveResponseDto responseDto = diaryService.backUp(backUpDto, avatar.getId());
        BackUp findBackUpAfter = backUpRepository.findBackUpByIdLatest(avatar.getId()).get();

        List<Diary> findDiaryList = diaryRepository.findDiariesByIdAndAvatarIdAndBackUpId(findBackUpAfter.getId(), avatar.getId());
        //then
        assertThat(findDiaryList.size()).isEqualTo(responseDto.getCount());
        assertThat(responseDto.getBackUpId()).isEqualTo(findBackUpAfter.getId());
        assertThat(findDiaryList).extracting("title").contains(dtos.get(0).getTitle(),dtos.get(1).getTitle());
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
        List<DiaryDto> dtos = new ArrayList<>();
        dtos.add(new DiaryDto(diary1.getId(),diary1.getKind(),diary1.getTitle(),diary1.getContent(),diary1.getWritingDate(),diary1.getDeviceId(),diary1.getDeviceType()));
        dtos.add(new DiaryDto(DiaryKind.FREE,"title3","content3",LocalDateTime.now()));

        BackUp findBackUpBefore = backUpRepository.findBackUpByIdLatest(avatar.getId()).get();
        BackUpDto backUpDto = new BackUpDto(dtos, "device1", "deviceId1", "os1", findBackUpBefore.getId());
        DiarySaveResponseDto responseDto = diaryService.backUp(backUpDto, avatar.getId());
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
        List<DiaryDto> dtos = new ArrayList<>();
        dtos.add(new DiaryDto(diary1.getId(),diary1.getKind(),diary1.getTitle(),diary1.getContent(),diary1.getWritingDate(),diary1.getDeviceId(),diary1.getDeviceType()));
        dtos.add(new DiaryDto(DiaryKind.FREE,"title3","content3",LocalDateTime.now()));

        BackUpDto backUpDto = new BackUpDto(dtos, "device1", "deviceId1", "os1", backUp.getId());
        //then
        assertThrows(NotExistAvatarException.class , () -> diaryService.backUp(backUpDto, 999999L));
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
        List<DiaryDto> dtos = new ArrayList<>();
        dtos.add(new DiaryDto(diary1.getId(),diary1.getKind(),diary1.getTitle(),diary1.getContent(),diary1.getWritingDate(),diary1.getDeviceId(),diary1.getDeviceType()));
        dtos.add(new DiaryDto(DiaryKind.FREE,"title3","content3",LocalDateTime.now()));

        BackUpDto backUpDto = new BackUpDto(dtos, "device1", "deviceId1", "os1", "asdasdas");
        //then

        assertThrows(NotExistBackUpException.class,()-> diaryService.backUp(backUpDto, avatar.getId()));
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
        List<DiaryDto> findDiaryList = diaryService.getRecentDiaryList(backUp.getId(), avatar.getId());

        //then
        assertThat(findDiaryList.size()).isEqualTo(diaryList.size());
        assertThat(findDiaryList).extracting("id",String.class).contains(diaryList.get(0).getId(),diaryList.get(1).getId(),diaryList.get(2).getId());
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