package com.cherish.backend.controller.dto.request;

import com.cherish.backend.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class DiaryEventRequestListTest {

    DiaryEventRequestList diaryEventRequestList;
    Avatar avatar = Avatar.of("avatar1", LocalDate.now(), Gender.FEMALE);
    BackUp backUp = BackUp.of("os99", "iphone99", avatar);

    DiaryEventRequest createEventRequest1 = new DiaryEventRequest(createClientId(), LocalDateTime.now(), "title1", "content1", DiaryKind.FREE, LocalDateTime.now());
    DiaryEventRequest createEventRequest2 = new DiaryEventRequest(createClientId(), LocalDateTime.now().plusHours(1L), "title2", "content2", DiaryKind.FREE, LocalDateTime.now());
    DiaryEventRequest createEventRequest3 = new DiaryEventRequest(createClientId(), LocalDateTime.now().plusHours(2L), "title3", "content3", DiaryKind.FREE, LocalDateTime.now());


    DiaryEventRequest editEventRequest1 = new DiaryEventRequest(createEventRequest1.getClientId(), createEventRequest1.getClientWritingDate(), "edittitle1_1", "editcontent1_1", DiaryKind.QUESTION, createEventRequest1.getClientWritingDate().plusHours(1L));
    DiaryEventRequest editEventRequest2 = new DiaryEventRequest(createEventRequest2.getClientId(), createEventRequest2.getClientWritingDate(), "edittitle1_2", "editcontent1_1", DiaryKind.QUESTION, createEventRequest2.getClientWritingDate().plusHours(2L));
    DiaryEventRequest editEventRequest3 = new DiaryEventRequest(createEventRequest3.getClientId(), createEventRequest3.getClientWritingDate(), "edittitleLAST", "editcontentLAST", DiaryKind.QUESTION, createEventRequest3.getClientWritingDate().plusHours(3L));
    DiaryEventRequest editEventRequest1_1 = new DiaryEventRequest(createEventRequest1.getClientId(), createEventRequest1.getClientWritingDate(), "edittitle1_1", "editcontent1_1", DiaryKind.QUESTION, createEventRequest1.getClientWritingDate().plusHours(2L));
    DiaryEventRequest editEventRequest1_Last = new DiaryEventRequest(createEventRequest1.getClientId(), createEventRequest1.getClientWritingDate(), "edittitle1_LAST", "editcontent1_LAST", DiaryKind.QUESTION, createEventRequest1.getClientWritingDate().plusHours(3L));
    DiaryEventRequest editEventRequest2_1 = new DiaryEventRequest(createEventRequest2.getClientId(), createEventRequest2.getClientWritingDate(), "edittitle2_1", "editcontent2_1", DiaryKind.QUESTION, createEventRequest2.getClientWritingDate().plusHours(14L));
    DiaryEventRequest editEventRequest2_Last = new DiaryEventRequest(createEventRequest2.getClientId(), createEventRequest2.getClientWritingDate(), "edittitle1_LAST", "editcontent2_LAST", DiaryKind.QUESTION, createEventRequest2.getClientWritingDate().plusHours(15L));


    DiaryEventRequest deleteEventRequest1 = new DiaryEventRequest(createEventRequest1.getClientId(), createEventRequest1.getClientWritingDate(), "edittitleLAST", "editcontentLAST", DiaryKind.QUESTION, createEventRequest1.getClientWritingDate().plusHours(4L));
    DiaryEventRequest deleteEventRequest2 = new DiaryEventRequest(createEventRequest2.getClientId(), createEventRequest2.getClientWritingDate(), "edittitleLAST", "editcontentLAST", DiaryKind.QUESTION, createEventRequest1.getClientWritingDate().plusHours(5L));

    List<DiaryEventRequest> createList = new ArrayList<>();
    List<DiaryEventRequest> editList = new ArrayList<>();
    List<DiaryEventRequest> deleteList = new ArrayList<>();


    Diary diary1 = Diary.of(createEventRequest1.getDiaryKind(), createEventRequest1.getClientId(), createEventRequest1.getTitle(), createEventRequest1.getContent(), createEventRequest1.getClientWritingDate(), avatar, backUp);
    Diary diary2 = Diary.of(createEventRequest2.getDiaryKind(), createEventRequest2.getClientId(), createEventRequest2.getTitle(), createEventRequest2.getContent(), createEventRequest2.getClientWritingDate(), avatar, backUp);
    Diary diary3 = Diary.of(createEventRequest3.getDiaryKind(), createEventRequest3.getClientId(), createEventRequest3.getTitle(), createEventRequest3.getContent(), createEventRequest3.getClientWritingDate(), avatar, backUp);

    List<Diary> diaryList = new ArrayList<>();

    @BeforeEach
    public void init() {
        createList.add(createEventRequest1);
        createList.add(createEventRequest2);
        createList.add(createEventRequest3);

        editList.add(editEventRequest1);
        editList.add(editEventRequest2);
        editList.add(editEventRequest3);

        deleteList.add(deleteEventRequest1);
        deleteList.add(deleteEventRequest2);

        diaryEventRequestList = new DiaryEventRequestList(createList, editList, deleteList, "ios99", "iphone99");

        diaryList.add(diary1);
        diaryList.add(diary2);
        diaryList.add(diary3);
    }

    String createClientId() {
        return UUID.randomUUID().toString().split("-")[0];
    }

    @Test
    @DisplayName("create request를 create eventList로 변환하여야 한다.")
    public void toCreateDiaryEventEntityTest() throws Exception {
        //given
        //when
        List<DiaryEvent> list = diaryEventRequestList.toCreateDiaryEventEntity(avatar, backUp);
        //then
        assertThat(list).extracting("title").contains(createEventRequest1.getTitle());
        assertThat(list).extracting("title").contains(createEventRequest2.getTitle());
        assertThat(list).extracting("title").contains(createEventRequest3.getTitle());

        assertThat(list).extracting("content").contains(createEventRequest1.getContent());
        assertThat(list).extracting("content").contains(createEventRequest2.getContent());
        assertThat(list).extracting("content").contains(createEventRequest3.getContent());

        assertThat(list).extracting("kind").contains(createEventRequest1.getDiaryKind());
        assertThat(list).extracting("kind").contains(createEventRequest2.getDiaryKind());
        assertThat(list).extracting("kind").contains(createEventRequest3.getDiaryKind());

        assertThat(list.size()).isEqualTo(createList.size());
    }

    @Test
    @DisplayName("edit request를 create eventList로 변환하여야 한다.")
    public void toEditDiaryEventEntityTest() throws Exception {
        //given
        //when
        List<DiaryEvent> list = diaryEventRequestList.toEditDiaryEventEntity(avatar, backUp);
        //then
        assertThat(list).extracting("title").contains(editEventRequest1.getTitle());
        assertThat(list).extracting("title").contains(editEventRequest2.getTitle());
        assertThat(list).extracting("title").contains(editEventRequest3.getTitle());

        assertThat(list).extracting("content").contains(editEventRequest1.getContent());
        assertThat(list).extracting("content").contains(editEventRequest2.getContent());
        assertThat(list).extracting("content").contains(editEventRequest3.getContent());

        assertThat(list).extracting("kind").contains(editEventRequest1.getDiaryKind());
        assertThat(list).extracting("kind").contains(editEventRequest2.getDiaryKind());
        assertThat(list).extracting("kind").contains(editEventRequest3.getDiaryKind());

        assertThat(list.size()).isEqualTo(editList.size());
    }

    @Test
    @DisplayName("delete request를 create eventList로 변환하여야 한다.")
    public void toDeleteDiaryEventEntityTest() throws Exception {
        //given
        //when
        List<DiaryEvent> list = diaryEventRequestList.toDeleteDiaryEventEntity(avatar, backUp);
        //then
        assertThat(list).extracting("title").contains(deleteEventRequest1.getTitle());
        assertThat(list).extracting("title").contains(deleteEventRequest2.getTitle());

        assertThat(list).extracting("content").contains(deleteEventRequest1.getContent());
        assertThat(list).extracting("content").contains(deleteEventRequest2.getContent());

        assertThat(list).extracting("kind").contains(deleteEventRequest1.getDiaryKind());
        assertThat(list).extracting("kind").contains(deleteEventRequest2.getDiaryKind());

        assertThat(list.size()).isEqualTo(deleteList.size());
    }

    @Test
    @DisplayName("만약 create eventList가 null인 경우 빈 arrayList만을 반환하여야 한다.")
    public void toCreateDiaryEventEntityTestIfNull() throws Exception {
        //given
        diaryEventRequestList = new DiaryEventRequestList(null, null, null, "ios100", "ios99");
        //when
        //then
        List<DiaryEvent> list = diaryEventRequestList.toCreateDiaryEventEntity(avatar, backUp);
        assertThat(list.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("만약 create eventList가 null인 경우 빈 arrayList만을 반환하여야 한다.")
    public void toEditDiaryEventEntityTestIfNull() throws Exception {
        //given
        diaryEventRequestList = new DiaryEventRequestList(null, null, null, "ios100", "ios99");
        //when
        //then
        List<DiaryEvent> list = diaryEventRequestList.toEditDiaryEventEntity(avatar, backUp);
        assertThat(list.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("만약 create eventList가 null인 경우 빈 arrayList만을 반환하여야 한다.")
    public void toDeleteDiaryEventEntityTestIfNull() throws Exception {
        //given
        diaryEventRequestList = new DiaryEventRequestList(null, null, null, "ios100", "ios99");
        //when
        //then
        List<DiaryEvent> list = diaryEventRequestList.toDeleteDiaryEventEntity(avatar, backUp);
        assertThat(list.size()).isEqualTo(0);
    }


    @Test
    @DisplayName("동일한 이벤트가 여러개인 경우 가장 마지막에 있는 이벤트를 가진 Map을 반환해야 한다.")
    public void toEditEventMapByLastEventDateTest() throws Exception {
        //given
        editList.add(editEventRequest1_1);
        editList.add(editEventRequest1_Last);
        editList.add(editEventRequest2_1);
        editList.add(editEventRequest2_Last);
        //when
        Map<LocalDateTime, DiaryEventRequest> editMap = diaryEventRequestList.toEditEventMapByLastEventDate();
        //then
        assertThat(editMap.get(editEventRequest1.getClientWritingDate()).getTitle()).isEqualTo(editEventRequest1_Last.getTitle());
        assertThat(editMap.get(editEventRequest1.getClientWritingDate()).getContent()).isEqualTo(editEventRequest1_Last.getContent());
        assertThat(editMap.get(editEventRequest1.getClientWritingDate()).getDiaryKind()).isEqualTo(editEventRequest1_Last.getDiaryKind());

        assertThat(editMap.get(editEventRequest2.getClientWritingDate()).getTitle()).isEqualTo(editEventRequest2_Last.getTitle());
        assertThat(editMap.get(editEventRequest2.getClientWritingDate()).getContent()).isEqualTo(editEventRequest2_Last.getContent());
        assertThat(editMap.get(editEventRequest2.getClientWritingDate()).getDiaryKind()).isEqualTo(editEventRequest2_Last.getDiaryKind());

        assertThat(editMap.get(editEventRequest3.getClientWritingDate()).getTitle()).isEqualTo(editEventRequest3.getTitle());
        assertThat(editMap.get(editEventRequest3.getClientWritingDate()).getContent()).isEqualTo(editEventRequest3.getContent());
        assertThat(editMap.get(editEventRequest3.getClientWritingDate()).getDiaryKind()).isEqualTo(editEventRequest3.getDiaryKind());

        assertThat(editMap.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("수정되는 이벤트가 없는 경우 빈 Map을 반환해야 한다.")
    public void toEditEventMapByLastEventDateTestIfNull() throws Exception {
        //given
        diaryEventRequestList = new DiaryEventRequestList(null, null, null, "ios100", "ios99");
        //when
        Map<LocalDateTime, DiaryEventRequest> editMap = diaryEventRequestList.toEditEventMapByLastEventDate();
        //then
        assertThat(editMap.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("기존에 존재하는 diary Entity 리스트에 EditEventList를 적용한 경우 edit 이벤트의 값으로 변경된 diary 리스트를 반환해야 한다.")
    public void toEditDiaryListFromFindDiaryListTest() throws Exception {
        //given
        //when
        List<Diary> resultList = diaryEventRequestList.toEditDiaryListFromFindDiaryList(diaryList, avatar);
        //then
        assertThat(resultList).extracting("title").contains(editEventRequest1.getTitle());
        assertThat(resultList).extracting("title").contains(editEventRequest2.getTitle());
        assertThat(resultList).extracting("title").contains(editEventRequest3.getTitle());

        assertThat(resultList).extracting("content").contains(editEventRequest1.getContent());
        assertThat(resultList).extracting("content").contains(editEventRequest2.getContent());
        assertThat(resultList).extracting("content").contains(editEventRequest3.getContent());

        assertThat(resultList).extracting("kind").contains(editEventRequest1.getDiaryKind());
        assertThat(resultList).extracting("kind").contains(editEventRequest2.getDiaryKind());
        assertThat(resultList).extracting("kind").contains(editEventRequest3.getDiaryKind());

        assertThat(resultList.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("기존에 존재하는 diary Entity 리스트에 빈 EditEventList를 적용한 경우 빈 array list값을 반환해야 한다.")
    public void toEditDiaryListFromFindDiaryListTestIfNull() throws Exception {
        //given
        diaryEventRequestList = new DiaryEventRequestList(null, null, null, "ios100", "ios99");
        //when
        List<Diary> resultList = diaryEventRequestList.toEditDiaryListFromFindDiaryList(diaryList, avatar);
        //then
        assertThat(resultList.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("기존에 존재하는 diary Entity 리스트에 DeleteEventList를 적용한 경우 이벤트에 포함된 diary를 제거한 array list값을 반환해야 한다.")
    public void toDeleteDiaryListFromFindDiaryListTest() throws Exception {
        //given
        //when
        List<Diary> resultList = diaryEventRequestList.toDeleteDiaryListFromFindDiaryList(diaryList, avatar);
        //then
        assertThat(resultList).extracting("title").contains(diary1.getTitle());
        assertThat(resultList).extracting("content").contains(diary1.getContent());
        assertThat(resultList).extracting("kind").contains(diary1.getKind());

        assertThat(resultList).extracting("title").contains(diary2.getTitle());
        assertThat(resultList).extracting("content").contains(diary2.getContent());
        assertThat(resultList).extracting("kind").contains(diary2.getKind());

        assertThat(resultList.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("기존에 존재하는 diary Entity 리스트에 빈 EditEventList를 적용한 경우 빈 array list값을 반환해야 한다.")
    public void toDeleteDiaryListFromFindDiaryListTestIfNull() throws Exception {
        //given
        diaryEventRequestList = new DiaryEventRequestList(null, null, null, "ios100", "ios99");
        //when
        List<Diary> resultList = diaryEventRequestList.toEditDiaryListFromFindDiaryList(diaryList, avatar);
        //then
        assertThat(resultList.size()).isEqualTo(0);
    }


    @Test
    @DisplayName("기존에 존재하는 diary Entity 리스트에 lasteventmap을 적용한 경우 이가 적용된 arraylist를 반환해야 한다.")
    public void toCreateDiaryEntityModifiedByEditEventListTest() throws Exception {
        //given
        Map<LocalDateTime, DiaryEventRequest> lastEventMap = diaryEventRequestList.toEditEventMapByLastEventDate();
        //when
        List<Diary> diaryList = diaryEventRequestList.toCreateDiaryEntityModifiedByEditEventList(lastEventMap, avatar, backUp);
        //then
        assertThat(diaryList).extracting("title").contains(lastEventMap.get(editEventRequest1.getClientWritingDate()).getTitle());
        assertThat(diaryList).extracting("content").contains(lastEventMap.get(editEventRequest1.getClientWritingDate()).getContent());
        assertThat(diaryList).extracting("kind").contains(lastEventMap.get(editEventRequest1.getClientWritingDate()).getDiaryKind());

        assertThat(diaryList).extracting("title").contains(lastEventMap.get(editEventRequest2.getClientWritingDate()).getTitle());
        assertThat(diaryList).extracting("content").contains(lastEventMap.get(editEventRequest2.getClientWritingDate()).getContent());
        assertThat(diaryList).extracting("kind").contains(lastEventMap.get(editEventRequest2.getClientWritingDate()).getDiaryKind());

        assertThat(diaryList).extracting("title").contains(lastEventMap.get(editEventRequest3.getClientWritingDate()).getTitle());
        assertThat(diaryList).extracting("content").contains(lastEventMap.get(editEventRequest3.getClientWritingDate()).getContent());
        assertThat(diaryList).extracting("kind").contains(lastEventMap.get(editEventRequest3.getClientWritingDate()).getDiaryKind());

        assertThat(diaryList.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("기존에 존재하는 diary Entity 리스트에 빈 createEventRequest가 적용 된 경우 빈 arrayList를 반환해야 한다.")
    public void toCreateDiaryEntityModifiedByEditEventListTestIfCreateListNull() throws Exception {
        //given
        diaryEventRequestList = new DiaryEventRequestList(null, editList, null, "ios100", "ios99");
        Map<LocalDateTime, DiaryEventRequest> lastEventMap = diaryEventRequestList.toEditEventMapByLastEventDate();
        //when
        List<Diary> diaryList = diaryEventRequestList.toCreateDiaryEntityModifiedByEditEventList(lastEventMap, avatar, backUp);
        //then
        assertThat(diaryList.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("기존에 존재하는 diary Entity 리스트에 빈 createEventRequest가 적용 된 경우 create 이벤트 값 수정 없이 diary List를 반환해야 한다.")
    public void toCreateDiaryEntityModifiedByEditEventListTestIfLastEventMapNull() throws Exception {
        //given
        diaryEventRequestList = new DiaryEventRequestList(createList, null, null, "ios100", "ios99");
        Map<LocalDateTime, DiaryEventRequest> lastEventMap = diaryEventRequestList.toEditEventMapByLastEventDate();
        //when
        List<Diary> diaryList = diaryEventRequestList.toCreateDiaryEntityModifiedByEditEventList(lastEventMap, avatar, backUp);
        //then
        assertThat(diaryList.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("diary List에 delete Event를 적용한 경우 해당 이벤트에 해당하는 diary를 제거해야 한다.")
    public void createDiaryListDeleteByDeleteEventListTest() throws Exception {
        //given
        //when
        List<Diary> resultList = diaryEventRequestList.createDiaryListDeleteByDeleteEventList(diaryList);
        //then
        assertThat(resultList.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("diaryList에 빈 delete Event를 적용한 경우 diaryList를 그대로 반환한다.")
    public void createDiaryListDeleteByDeleteEventListTestIfDeleteListNull() throws Exception {
        //given
        diaryEventRequestList = new DiaryEventRequestList(createList, null, null, "ios100", "ios99");
        //when
        List<Diary> resultList = diaryEventRequestList.createDiaryListDeleteByDeleteEventList(diaryList);
        //then
        assertThat(resultList.size()).isEqualTo(3);

    }

}