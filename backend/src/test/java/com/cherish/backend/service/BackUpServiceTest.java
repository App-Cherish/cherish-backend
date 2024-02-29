package com.cherish.backend.service;

import com.cherish.backend.controller.dto.request.DiaryEventRequest;
import com.cherish.backend.controller.dto.request.DiaryEventRequestList;
import com.cherish.backend.domain.*;
import com.cherish.backend.repositroy.AvatarRepository;
import com.cherish.backend.repositroy.BackUpRepository;
import com.cherish.backend.repositroy.DiaryRepository;
import jakarta.persistence.EntityManager;
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


@SpringBootTest
@Transactional
class BackUpServiceTest {

    @Autowired
    BackUpService backUpService;

    @Autowired
    BackUpRepository backUpRepository;

    @Autowired
    AvatarRepository avatarRepository;

    @Autowired
    DiaryRepository diaryRepository;

    @Autowired
    EntityManager em;

    Avatar avatar;

    BackUp backUp;

    Diary diary1, diary2, diary3;

    DiaryEventRequest createEventRequest1, createEventRequest2, createEventRequest3;
    DiaryEventRequest editEventRequest1_1, editEventRequest1_2, editEventRequest1Last;

    DiaryEventRequest editEventRequest2, editEventRequest2Last, deleteEventRequest1, editExistEventRequest1, editExistEventRequest1_1, editExistEventRequest1_Last, editExistEventRequest2, newEditEventRequest1, deleteExistEventRequest1, newDeleteEventRequest1;

    @BeforeEach
    public void init() {
        avatar = Avatar.of("name1", LocalDate.now(), Gender.MALE);
        avatar = avatarRepository.save(avatar);

        backUp = BackUp.of("os1", "device1", avatar);
        backUp = backUpRepository.save(backUp);

        diary1 = Diary.of(DiaryKind.FREE, "clientId1", "title1", "content1", LocalDateTime.now(), avatar, backUp);
        diary2 = Diary.of(DiaryKind.FREE, "clientId2", "title2", "content2", LocalDateTime.now(), avatar, backUp);
        diary3 = Diary.of(DiaryKind.FREE, "clientId3", "title3", "content3", LocalDateTime.now(), avatar, backUp);

        diary1 = diaryRepository.save(diary1);
        diary2 = diaryRepository.save(diary2);
        diary3 = diaryRepository.save(diary3);

        em.flush();
        em.clear();

        diary1 = em.find(Diary.class, diary1.getId());
        diary2 = em.find(Diary.class, diary2.getId());
        diary3 = em.find(Diary.class, diary3.getId());

        createEventRequest1 = new DiaryEventRequest("clientId1", LocalDateTime.now(), "title1", "content1", DiaryKind.FREE, LocalDateTime.now());
        createEventRequest2 = new DiaryEventRequest("clientId2", LocalDateTime.now().plusHours(1L), "title2", "content2", DiaryKind.FREE, LocalDateTime.now());
        createEventRequest3 = new DiaryEventRequest("clientId3", LocalDateTime.now().plusHours(2L), "title3", "content3", DiaryKind.FREE, LocalDateTime.now());
        editEventRequest1_1 = new DiaryEventRequest(createEventRequest1.getClientId(), createEventRequest1.getClientWritingDate(), "edittitle1_1", "editcontent1_1", DiaryKind.QUESTION, LocalDateTime.now().plusHours(1L));
        editEventRequest1_2 = new DiaryEventRequest(createEventRequest1.getClientId(), createEventRequest1.getClientWritingDate(), "edittitle1_2", "editcontent1_2", DiaryKind.FREE, LocalDateTime.now().plusHours(2L));
        editEventRequest1Last = new DiaryEventRequest(createEventRequest1.getClientId(), createEventRequest1.getClientWritingDate(), "edittitle1", "editcontent1", DiaryKind.EMOTION, LocalDateTime.now().plusHours(3L));

        editEventRequest2 = new DiaryEventRequest(createEventRequest2.getClientId(), createEventRequest2.getClientWritingDate(), "edittitle2", "editcontent2", DiaryKind.QUESTION, LocalDateTime.now().plusHours(2L));
        editEventRequest2Last = new DiaryEventRequest(createEventRequest2.getClientId(), createEventRequest2.getClientWritingDate(), "edittitle2Last", "editcontent2Last", DiaryKind.QUESTION, LocalDateTime.now().plusHours(3L));

        deleteEventRequest1 = new DiaryEventRequest(createEventRequest1.getClientId(), createEventRequest1.getClientWritingDate(), "title1", "content1", DiaryKind.FREE, LocalDateTime.now().plusHours(4L));

        editExistEventRequest1 = new DiaryEventRequest(diary1.getClientId(), diary1.getClientWritingDate(), "edittitle1_1", "editcontent1_1", DiaryKind.QUESTION, diary1.getClientWritingDate().plusHours(1L));
        editExistEventRequest1_1 = new DiaryEventRequest(diary1.getClientId(), diary1.getClientWritingDate(), "edittitle1_2", "editcontent1_1", DiaryKind.QUESTION, diary1.getClientWritingDate().plusHours(2L));
        editExistEventRequest1_Last = new DiaryEventRequest(diary1.getClientId(), diary1.getClientWritingDate(), "edittitleLAST", "editcontentLAST", DiaryKind.QUESTION, diary1.getClientWritingDate().plusHours(3L));
        editExistEventRequest2 = new DiaryEventRequest(diary2.getClientId(), diary2.getClientWritingDate(), "edittitle2LAST", "editcontent2LAST", DiaryKind.QUESTION, diary2.getClientWritingDate().plusHours(3L));

        newEditEventRequest1 = new DiaryEventRequest(createEventRequest1.getClientId(), createEventRequest1.getClientWritingDate(), "edittitle3", "editcontent3", DiaryKind.QUESTION, LocalDateTime.now().plusHours(12L));
        deleteExistEventRequest1 = new DiaryEventRequest(diary1.getClientId(), diary1.getClientWritingDate(), "edittitleLAST", "editcontentLAST", DiaryKind.QUESTION, diary1.getClientWritingDate().plusHours(4L));
        newDeleteEventRequest1 = new DiaryEventRequest(createEventRequest1.getClientId(), createEventRequest1.getClientWritingDate(), "edittitle3", "editcontent3", DiaryKind.QUESTION, LocalDateTime.now().plusHours(13L));

    }

    @Test
    @DisplayName("만약 여러개의 백업이 존재하는 경우 가장 최신의 백업을 가지고 온다. ")
    public void getRecentBackUpTest() throws Exception {
        //given
        BackUp back1 = BackUp.of("os1", "device1", avatar);
        Thread.sleep(3000);
        BackUp back2 = BackUp.of("os1", "device1", avatar);
        Thread.sleep(3000);
        BackUp back3 = BackUp.of("os1", "device1", avatar);
        Thread.sleep(3000);

        backUpRepository.save(back1);
        backUpRepository.save(back2);
        backUpRepository.save(back3);
    }

    @Test
    @DisplayName("만약 백업이 존재하지 않는 경우 예외를 출력한다. ")
    public void getRecentBackUpNullTest() throws Exception {
        //given
        //when
        //then
//        assertThrows(NotExistBackUpHistoryException.class, () -> backUpService.getRecentBackUp(Long.MAX_VALUE));
    }

    /**
     * 기존에 백업한 경우가 없는 일기의 이벤트를 등록하는 경우에 대한 테스트
     */

    @Test
    @DisplayName("기존에 백업이 존재 하지 않는 경우 모든 create 이벤트를 저장한다.")
    void createTestIfNotExistBeforeBackUp() throws Exception {
        //given
        List<DiaryEventRequest> createList = new ArrayList<>();
        createList.add(createEventRequest1);
        createList.add(createEventRequest2);
        createList.add(createEventRequest3);
        DiaryEventRequestList diaryEventRequestList = new DiaryEventRequestList(createList, null, null, "ios100", "iphone15");

        Avatar newPerson = Avatar.of("newPerson", LocalDate.of(2000, 1, 1), Gender.MALE);
        em.persist(newPerson);

        //when
        backUpService.backup(diaryEventRequestList, newPerson.getId());

        //then
        BackUp findBackUp = backUpRepository.findBackUpByIdLatest(newPerson.getId()).get();
        List<Diary> diaryList = diaryRepository.findDiariesByIdAndAvatarIdAndBackUpId(findBackUp.getId(), newPerson.getId());

        assertThat(diaryList).extracting("title").contains(createEventRequest1.getTitle());
        assertThat(diaryList).extracting("title").contains(createEventRequest2.getTitle());
        assertThat(diaryList).extracting("title").contains(createEventRequest3.getTitle());

        assertThat(diaryList).extracting("content").contains(createEventRequest1.getContent());
        assertThat(diaryList).extracting("content").contains(createEventRequest2.getContent());
        assertThat(diaryList).extracting("content").contains(createEventRequest3.getContent());

        assertThat(diaryList).extracting("kind").contains(createEventRequest1.getDiaryKind());
        assertThat(diaryList).extracting("kind").contains(createEventRequest2.getDiaryKind());
        assertThat(diaryList).extracting("kind").contains(createEventRequest3.getDiaryKind());

        assertThat(diaryList.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("기존에 백업이 존재 하지 않는 경우 모든 Create 이벤트를 저장하고 만약 클라이언트 작성시간이 동일한 edit 이벤트가 존재 하는 경우 해당 create이벤트가 수정되어 저장되어야 한다.")
    public void createTestIfNotExistBeforeBackUpAndExistEventList() throws Exception {
        //given
        DiaryEventRequest editEventRequest1 = new DiaryEventRequest(createEventRequest1.getClientId(), createEventRequest1.getClientWritingDate(), "edittitle1", "editcontent1", DiaryKind.QUESTION, LocalDateTime.now().plusHours(1L));
        DiaryEventRequest editEventRequest2 = new DiaryEventRequest(createEventRequest1.getClientId(), createEventRequest1.getClientWritingDate(), "edittitle1", "editcontent1", DiaryKind.QUESTION, LocalDateTime.now().plusHours(1L));

        List<DiaryEventRequest> createList = new ArrayList<>();
        List<DiaryEventRequest> editList = new ArrayList<>();
        createList.add(createEventRequest1);
        createList.add(createEventRequest2);
        createList.add(createEventRequest3);
        editList.add(editEventRequest1);
        editList.add(editEventRequest2);
        DiaryEventRequestList diaryEventRequestList = new DiaryEventRequestList(createList, editList, null, "ios100", "iphone15");

        Avatar newPerson = Avatar.of("newPerson", LocalDate.of(2000, 1, 1), Gender.MALE);
        em.persist(newPerson);
        //when
        backUpService.backup(diaryEventRequestList, newPerson.getId());

        //then
        BackUp findBackUp = backUpRepository.findBackUpByIdLatest(newPerson.getId()).get();
        List<Diary> diaryList = diaryRepository.findDiariesByIdAndAvatarIdAndBackUpId(findBackUp.getId(), newPerson.getId());

        assertThat(diaryList).extracting("title").contains(editEventRequest1.getTitle());
        assertThat(diaryList).extracting("title").contains(editEventRequest2.getTitle());
        assertThat(diaryList).extracting("title").contains(createEventRequest3.getTitle());

        assertThat(diaryList).extracting("content").contains(editEventRequest1.getContent());
        assertThat(diaryList).extracting("content").contains(editEventRequest2.getContent());
        assertThat(diaryList).extracting("content").contains(createEventRequest3.getContent());

        assertThat(diaryList).extracting("kind").contains(editEventRequest1.getDiaryKind());
        assertThat(diaryList).extracting("kind").contains(editEventRequest2.getDiaryKind());
        assertThat(diaryList).extracting("kind").contains(createEventRequest3.getDiaryKind());
    }

    @Test
    @DisplayName("기존에 백업이 존재 하지 않는 경우 모든 Create 이벤트를 저장하고 만약 작성시간이 동일한 edit 이벤트가 여러개 존재 하는 경우 이때 가장 최신인 edit 이벤트가 create이벤트에 수정되어 저장되어야 한다.")
    public void createTestIfNotExistBeforeBackUpAndExistEventLists() throws Exception {
        List<DiaryEventRequest> createList = new ArrayList<>();
        List<DiaryEventRequest> editList = new ArrayList<>();
        createList.add(createEventRequest1);
        createList.add(createEventRequest2);
        createList.add(createEventRequest3);
        editList.add(editEventRequest1_1);
        editList.add(editEventRequest1_2);
        editList.add(editEventRequest1Last);
        editList.add(editEventRequest2);
        editList.add(editEventRequest2Last);
        DiaryEventRequestList diaryEventRequestList = new DiaryEventRequestList(createList, editList, null, "ios100", "iphone15");

        Avatar newPerson = Avatar.of("newPerson", LocalDate.of(2000, 1, 1), Gender.MALE);
        em.persist(newPerson);
        //when
        backUpService.backup(diaryEventRequestList, newPerson.getId());

        //then
        BackUp findBackUp = backUpRepository.findBackUpByIdLatest(newPerson.getId()).get();
        List<Diary> diaryList = diaryRepository.findDiariesByIdAndAvatarIdAndBackUpId(findBackUp.getId(), newPerson.getId());

        assertThat(diaryList).extracting("title").contains(editEventRequest1Last.getTitle());
        assertThat(diaryList).extracting("title").contains(editEventRequest2Last.getTitle());
        assertThat(diaryList).extracting("title").contains(createEventRequest3.getTitle());

        assertThat(diaryList).extracting("content").contains(editEventRequest1Last.getContent());
        assertThat(diaryList).extracting("content").contains(editEventRequest2Last.getContent());
        assertThat(diaryList).extracting("content").contains(createEventRequest3.getContent());

        assertThat(diaryList).extracting("kind").contains(editEventRequest1Last.getDiaryKind());
        assertThat(diaryList).extracting("kind").contains(editEventRequest2Last.getDiaryKind());
        assertThat(diaryList).extracting("kind").contains(createEventRequest3.getDiaryKind());
    }

    @Test
    @DisplayName("기존에 백업이 존재 하지 않는 경우 모든 Create 이벤트를 저장하고 만약 작성시간이 동일한 edit,delete 이벤트가 여러개 존재 하는 경우 이때 가장 최신인 delete 이벤트가 create이벤트에 수정되어 저장되어야 한다.")
    public void createTestIfNotExistBeforeBackUpAndDeleteExistEventLists1() throws Exception {

        List<DiaryEventRequest> createList = new ArrayList<>();
        List<DiaryEventRequest> editList = new ArrayList<>();
        List<DiaryEventRequest> deleteList = new ArrayList<>();

        createList.add(createEventRequest1);
        createList.add(createEventRequest2);
        createList.add(createEventRequest3);

        editList.add(editEventRequest1_1);
        editList.add(editEventRequest1_2);
        editList.add(editEventRequest1Last);
        editList.add(editEventRequest2);
        editList.add(editEventRequest2Last);

        deleteList.add(deleteEventRequest1);

        DiaryEventRequestList diaryEventRequestList = new DiaryEventRequestList(createList, editList, deleteList, "ios100", "iphone15");

        Avatar newPerson = Avatar.of("newPerson", LocalDate.of(2000, 1, 1), Gender.MALE);
        em.persist(newPerson);
        //when
        backUpService.backup(diaryEventRequestList, newPerson.getId());

        //then
        BackUp findBackUp = backUpRepository.findBackUpByIdLatest(newPerson.getId()).get();
        List<Diary> diaryList = diaryRepository.findDiariesByIdAndAvatarIdAndBackUpId(findBackUp.getId(), newPerson.getId());

        assertThat(diaryList).extracting("title").contains(editEventRequest2Last.getTitle());
        assertThat(diaryList).extracting("title").contains(createEventRequest3.getTitle());

        assertThat(diaryList).extracting("content").contains(editEventRequest2Last.getContent());
        assertThat(diaryList).extracting("content").contains(createEventRequest3.getContent());

        assertThat(diaryList).extracting("kind").contains(editEventRequest2Last.getDiaryKind());
        assertThat(diaryList).extracting("kind").contains(createEventRequest3.getDiaryKind());

        assertThat(diaryList.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("기존에 백업이 존재 하지 않는 경우 모든 Create 이벤트를 저장하고 만약 작성시간이 동일한 delete 이벤트가 존재하는 경우 create 이벤트가 비활성화 되어 저장되어야 한다.")
    public void createTestIfNotExistBeforeBackUpAndDeleteExistEventLists2() throws Exception {
        List<DiaryEventRequest> createList = new ArrayList<>();
        List<DiaryEventRequest> editList = new ArrayList<>();
        List<DiaryEventRequest> deleteList = new ArrayList<>();

        createList.add(createEventRequest1);
        createList.add(createEventRequest2);
        createList.add(createEventRequest3);
        deleteList.add(deleteEventRequest1);

        DiaryEventRequestList diaryEventRequestList = new DiaryEventRequestList(createList, editList, deleteList, "ios100", "iphone15");

        Avatar newPerson = Avatar.of("newPerson", LocalDate.of(2000, 1, 1), Gender.MALE);
        em.persist(newPerson);
        //when
        backUpService.backup(diaryEventRequestList, newPerson.getId());

        //then
        BackUp findBackUp = backUpRepository.findBackUpByIdLatest(newPerson.getId()).get();
        List<Diary> diaryList = diaryRepository.findDiariesByIdAndAvatarIdAndBackUpId(findBackUp.getId(), newPerson.getId());

        assertThat(diaryList).extracting("title").contains(createEventRequest2.getTitle());
        assertThat(diaryList).extracting("title").contains(createEventRequest3.getTitle());

        assertThat(diaryList).extracting("content").contains(createEventRequest2.getContent());
        assertThat(diaryList).extracting("content").contains(createEventRequest3.getContent());

        assertThat(diaryList).extracting("kind").contains(createEventRequest2.getDiaryKind());
        assertThat(diaryList).extracting("kind").contains(createEventRequest3.getDiaryKind());

        assertThat(diaryList.size()).isEqualTo(2);
    }

    /**
     * 기존에 백업한 경우 일기의 이벤트를 등록하는 경우에 대한 테스트
     */

    @Test
    @DisplayName("기존에 백업이 존재하는 경우 새로운 create 이벤트가 등록되었을 때에 새로운 일기와 기존의 일기가 새로 생성된 백업 엔티티에 저장된다.")
    public void backUpEntityUpdateTest() throws Exception {
        //given
        List<DiaryEventRequest> createList = new ArrayList<>();
        createList.add(createEventRequest1);
        createList.add(createEventRequest2);
        createList.add(createEventRequest3);
        DiaryEventRequestList diaryEventRequestList = new DiaryEventRequestList(createList, null, null, "ios100", "iphone15");


        //when
        backUpService.backup(diaryEventRequestList, avatar.getId());

        //then
        BackUp findBackUp = backUpRepository.findBackUpByIdLatest(avatar.getId()).get();
        BackUp expect = backUpRepository.findBackUpByIdLatest(avatar.getId()).get();

        assertThat(findBackUp.getId()).isEqualTo(expect.getId());
    }

    @Test
    @DisplayName("기존에 백업이 존재하는 경우 기존의 일기와 작성된 동일한 시간대의 edit 이벤트가 존재하는 경우 수정되어 저장된다.")
    public void diaryTest1IfBackUpExist() throws Exception {
        //given
        List<DiaryEventRequest> editList = new ArrayList<>();
        editList.add(editExistEventRequest1);

        DiaryEventRequestList diaryEventRequestList = new DiaryEventRequestList(null, editList, null, "ios100", "iphone15");
        backUpService.backup(diaryEventRequestList, avatar.getId());
        em.flush();
        em.clear();
        //when
        BackUp findBackUp = backUpRepository.findBackUpByIdLatest(avatar.getId()).get();
        List<Diary> findDiaryList = diaryRepository.findDiariesByIdAndAvatarIdAndBackUpId(findBackUp.getId(), avatar.getId());
        //then


        assertThat(findDiaryList).extracting("title").contains(editExistEventRequest1.getTitle());
        assertThat(findDiaryList).extracting("title").contains(diary2.getTitle());
        assertThat(findDiaryList).extracting("title").contains(diary3.getTitle());

        assertThat(findDiaryList).extracting("content").contains(editExistEventRequest1.getContent());
        assertThat(findDiaryList).extracting("content").contains(diary2.getContent());
        assertThat(findDiaryList).extracting("content").contains(diary3.getContent());

        assertThat(findDiaryList).extracting("kind").contains(editExistEventRequest1.getDiaryKind());
        assertThat(findDiaryList).extracting("kind").contains(diary2.getKind());
        assertThat(findDiaryList).extracting("kind").contains(diary3.getKind());

        assertThat(findDiaryList.size()).isEqualTo(3);

    }

    @Test
    @DisplayName("기존에 백업이 존재하는 경우 기존의 일기와 작성된 동일한 시간대의 edit 이벤트가 여러개 존재하는 경우 가장 최신 이벤트로 수정되어 저장된다.")
    public void diaryTest1IfBackUpExist2() throws Exception {

        List<DiaryEventRequest> editList = new ArrayList<>();
        editList.add(editExistEventRequest1);
        editList.add(editExistEventRequest1_1);
        editList.add(editExistEventRequest1_Last);

        DiaryEventRequestList diaryEventRequestList = new DiaryEventRequestList(null, editList, null, "ios100", "iphone15");
        backUpService.backup(diaryEventRequestList, avatar.getId());
        em.flush();
        em.clear();
        //when
        BackUp findBackUp = backUpRepository.findBackUpByIdLatest(avatar.getId()).get();
        List<Diary> findDiaryList = diaryRepository.findDiariesByIdAndAvatarIdAndBackUpId(findBackUp.getId(), avatar.getId());
        //then


        assertThat(findDiaryList).extracting("title").contains(editExistEventRequest1_Last.getTitle());
        assertThat(findDiaryList).extracting("title").contains(diary2.getTitle());
        assertThat(findDiaryList).extracting("title").contains(diary3.getTitle());

        assertThat(findDiaryList).extracting("content").contains(editExistEventRequest1_Last.getContent());
        assertThat(findDiaryList).extracting("content").contains(diary2.getContent());
        assertThat(findDiaryList).extracting("content").contains(diary3.getContent());

        assertThat(findDiaryList).extracting("kind").contains(editExistEventRequest1_Last.getDiaryKind());
        assertThat(findDiaryList).extracting("kind").contains(diary2.getKind());
        assertThat(findDiaryList).extracting("kind").contains(diary3.getKind());

        assertThat(findDiaryList.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("기존에 백업이 존재하는 경우 기존의 일기와 작성된 동일한 시간대의 edit 이벤트와 delete 이벤트가 여러개 존재하는 경우 edit이벤트가 가장 최신일 때 해당 이벤트로 수정된후 비활성화 되어 저장된다.")
    public void deleteDiaryTest1IfBackUpExist2() throws Exception {
        List<DiaryEventRequest> editList = new ArrayList<>();
        List<DiaryEventRequest> deleteList = new ArrayList<>();
        editList.add(editExistEventRequest1);
        editList.add(editExistEventRequest1_1);
        editList.add(editExistEventRequest1_Last);
        deleteList.add(deleteExistEventRequest1);

        BackUp findBackUp = backUpRepository.findBackUpByIdLatest(avatar.getId()).get();
        List<Diary> findDiaryList = diaryRepository.findDiariesByIdAndAvatarIdAndBackUpId(findBackUp.getId(), avatar.getId());

        Diary deleteDiary = findDiaryList.get(0);

        DiaryEventRequestList diaryEventRequestList = new DiaryEventRequestList(null, editList, deleteList, "ios100", "iphone15");
        backUpService.backup(diaryEventRequestList, avatar.getId());
        em.flush();
        em.clear();
        //when
        findBackUp = backUpRepository.findBackUpByIdLatest(avatar.getId()).get();
        findDiaryList = diaryRepository.findDiariesByIdAndAvatarIdAndBackUpId(findBackUp.getId(), avatar.getId());
        //then
        deleteDiary = em.find(Diary.class, diary1.getId());

        assertThat(deleteDiary.getActive()).isEqualTo(0);

        assertThat(deleteDiary.getTitle()).isEqualTo(editExistEventRequest1_Last.getTitle());
        assertThat(deleteDiary.getContent()).isEqualTo(editExistEventRequest1_Last.getContent());
        assertThat(deleteDiary.getKind()).isEqualTo(editExistEventRequest1_Last.getDiaryKind());

        assertThat(findDiaryList).extracting("title").contains(diary2.getTitle());
        assertThat(findDiaryList).extracting("title").contains(diary3.getTitle());

        assertThat(findDiaryList).extracting("content").contains(diary2.getContent());
        assertThat(findDiaryList).extracting("content").contains(diary3.getContent());

        assertThat(findDiaryList).extracting("kind").contains(diary2.getKind());
        assertThat(findDiaryList).extracting("kind").contains(diary3.getKind());

        assertThat(findDiaryList.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("기존에 백업이 존재하는 경우 기존의 일기 수정 이벤트와 새로운 일기 이벤트 작성이 들어온 경우 모두 적용이 되어야 한다.")
    public void createDiaryTest() throws Exception {
        List<DiaryEventRequest> createList = new ArrayList<>();
        List<DiaryEventRequest> editList = new ArrayList<>();
        createList.add(createEventRequest1);
        editList.add(editExistEventRequest1);
        editList.add(editExistEventRequest1_1);
        editList.add(editExistEventRequest1_Last);

        DiaryEventRequestList diaryEventRequestList = new DiaryEventRequestList(createList, editList, null, "ios100", "iphone15");
        backUpService.backup(diaryEventRequestList, avatar.getId());
        em.flush();
        em.clear();
        //when
        BackUp findBackUp = backUpRepository.findBackUpByIdLatest(avatar.getId()).get();
        List<Diary> findDiaryList = diaryRepository.findDiariesByIdAndAvatarIdAndBackUpId(findBackUp.getId(), avatar.getId());
        //then
        assertThat(findDiaryList).extracting("title").contains(createEventRequest1.getTitle());
        assertThat(findDiaryList).extracting("title").contains(editExistEventRequest1_Last.getTitle());
        assertThat(findDiaryList).extracting("title").contains(diary2.getTitle());
        assertThat(findDiaryList).extracting("title").contains(diary3.getTitle());

        assertThat(findDiaryList).extracting("content").contains(createEventRequest1.getContent());
        assertThat(findDiaryList).extracting("content").contains(editExistEventRequest1_Last.getContent());
        assertThat(findDiaryList).extracting("content").contains(diary2.getContent());
        assertThat(findDiaryList).extracting("content").contains(diary3.getContent());

        assertThat(findDiaryList).extracting("kind").contains(editExistEventRequest1_Last.getDiaryKind());
        assertThat(findDiaryList).extracting("kind").contains(createEventRequest1.getDiaryKind());
        assertThat(findDiaryList).extracting("kind").contains(diary2.getKind());
        assertThat(findDiaryList).extracting("kind").contains(diary3.getKind());

        assertThat(findDiaryList.size()).isEqualTo(4);
    }


    @Test
    @DisplayName("기존에 백업이 존재하는 경우 기존의 일기 수정 이벤트와 새로운 일기 생성 이벤트 수정 이벤트가 들어온 경우 모두 적용이 되어야 한다.")
    public void createDiaryTest2() throws Exception {
        List<DiaryEventRequest> createList = new ArrayList<>();
        List<DiaryEventRequest> editList = new ArrayList<>();
        createList.add(createEventRequest1);
        editList.add(editExistEventRequest1);
        editList.add(editExistEventRequest1_1);
        editList.add(editExistEventRequest1_Last);
        editList.add(newEditEventRequest1);

        DiaryEventRequestList diaryEventRequestList = new DiaryEventRequestList(createList, editList, null, "ios100", "iphone15");
        backUpService.backup(diaryEventRequestList, avatar.getId());
        em.flush();
        em.clear();
        //when
        BackUp findBackUp = backUpRepository.findBackUpByIdLatest(avatar.getId()).get();
        List<Diary> findDiaryList = diaryRepository.findDiariesByIdAndAvatarIdAndBackUpId(findBackUp.getId(), avatar.getId());
        //then
        assertThat(findDiaryList).extracting("title").contains(newEditEventRequest1.getTitle());
        assertThat(findDiaryList).extracting("title").contains(editExistEventRequest1_Last.getTitle());
        assertThat(findDiaryList).extracting("title").contains(diary2.getTitle());
        assertThat(findDiaryList).extracting("title").contains(diary3.getTitle());

        assertThat(findDiaryList).extracting("content").contains(newEditEventRequest1.getContent());
        assertThat(findDiaryList).extracting("content").contains(editExistEventRequest1_Last.getContent());
        assertThat(findDiaryList).extracting("content").contains(diary2.getContent());
        assertThat(findDiaryList).extracting("content").contains(diary3.getContent());

        assertThat(findDiaryList).extracting("kind").contains(newEditEventRequest1.getDiaryKind());
        assertThat(findDiaryList).extracting("kind").contains(createEventRequest1.getDiaryKind());
        assertThat(findDiaryList).extracting("kind").contains(diary2.getKind());
        assertThat(findDiaryList).extracting("kind").contains(diary3.getKind());

        assertThat(findDiaryList.size()).isEqualTo(4);
    }

    @Test
    @DisplayName("기존에 백업이 존재하는 경우 기존의 일기 수정 이벤트와 새로운 일기 생성 이벤트 수정 이벤트가 기존 일기와 새로 작성된 일기의 삭제 이벤트가 들어온 경우 모두 적용이 되어야 한다.")
    public void createDiaryTest3() throws Exception {
        List<DiaryEventRequest> createList = new ArrayList<>();
        List<DiaryEventRequest> editList = new ArrayList<>();
        List<DiaryEventRequest> deleteList = new ArrayList<>();
        createList.add(createEventRequest1);
        createList.add(createEventRequest2);
        createList.add(createEventRequest3);
        editList.add(editExistEventRequest1);
        editList.add(editExistEventRequest1_1);
        editList.add(editExistEventRequest1_Last);
        editList.add(editExistEventRequest2);
        editList.add(newEditEventRequest1);
        deleteList.add(deleteExistEventRequest1);
        deleteList.add(newDeleteEventRequest1);

        DiaryEventRequestList diaryEventRequestList = new DiaryEventRequestList(createList, editList, deleteList, "ios100", "iphone15");
        backUpService.backup(diaryEventRequestList, avatar.getId());
        em.flush();
        em.clear();
        //when
        BackUp findBackUp = backUpRepository.findBackUpByIdLatest(avatar.getId()).get();
        List<Diary> findDiaryList = diaryRepository.findDiariesByIdAndAvatarIdAndBackUpId(findBackUp.getId(), avatar.getId());


        //then
        assertThat(findDiaryList).extracting("title").contains(diary3.getTitle());
        assertThat(findDiaryList).extracting("title").contains(editExistEventRequest2.getTitle());
        assertThat(findDiaryList).extracting("title").contains(createEventRequest3.getTitle());
        assertThat(findDiaryList).extracting("title").contains(createEventRequest2.getTitle());


        assertThat(findDiaryList).extracting("content").contains(editExistEventRequest2.getContent());
        assertThat(findDiaryList).extracting("content").contains(diary3.getContent());
        assertThat(findDiaryList).extracting("content").contains(createEventRequest3.getContent());
        assertThat(findDiaryList).extracting("content").contains(createEventRequest2.getContent());

        assertThat(findDiaryList).extracting("kind").contains(editExistEventRequest2.getDiaryKind());
        assertThat(findDiaryList).extracting("kind").contains(diary3.getKind());
        assertThat(findDiaryList).extracting("kind").contains(createEventRequest3.getDiaryKind());
        assertThat(findDiaryList).extracting("kind").contains(createEventRequest2.getDiaryKind());

        assertThat(findDiaryList.size()).isEqualTo(4);
    }

    @Test
    @DisplayName("이벤트가 없는 경우에는 백업 아이디만 변경이 된다.")
    public void createDiaryTest4() throws Exception {
        //when
        DiaryEventRequestList diaryEventRequestList = new DiaryEventRequestList(null, null, null, "ios100", "iphone15");
        backUpService.backup(diaryEventRequestList, avatar.getId());
        em.flush();
        em.clear();
        BackUp lastestBackUp = backUpRepository.findBackUpByIdLatest(avatar.getId()).get();
        List<Diary> findDiaryList = diaryRepository.findDiariesByIdAndAvatarIdAndBackUpId(lastestBackUp.getId(), avatar.getId());

        //then
        assertThat(findDiaryList).extracting("title").contains(diary1.getTitle());
        assertThat(findDiaryList).extracting("title").contains(diary2.getTitle());
        assertThat(findDiaryList).extracting("title").contains(diary3.getTitle());

        assertThat(findDiaryList).extracting("content").contains(diary1.getContent());
        assertThat(findDiaryList).extracting("content").contains(diary3.getContent());
        assertThat(findDiaryList).extracting("content").contains(diary3.getContent());

        assertThat(findDiaryList).extracting("kind").contains(diary1.getKind());
        assertThat(findDiaryList).extracting("kind").contains(diary3.getKind());
        assertThat(findDiaryList).extracting("kind").contains(diary3.getKind());

        assertThat(findDiaryList.size()).isEqualTo(3);

        assertThat(findDiaryList.get(0).getBackUp().getId()).isEqualTo(lastestBackUp.getId());
    }


    @Test
    @DisplayName("기존에 백업이 존재할 때에 새로운 이벤트가 들어오면 이전의 백업은 비활성화 된다.")
    public void backUpActiveTest() throws Exception {
        //when
        BackUp backUp = backUpRepository.findBackUpByIdLatest(avatar.getId()).get();

        DiaryEventRequestList diaryEventRequestList = new DiaryEventRequestList(null, null, null, "ios100", "iphone15");
        backUpService.backup(diaryEventRequestList, avatar.getId());
        em.flush();
        em.clear();

        BackUp findBackUp = em.find(BackUp.class, backUp.getId());

        //then
        assertThat(findBackUp.getActive()).isEqualTo(0);
    }
}