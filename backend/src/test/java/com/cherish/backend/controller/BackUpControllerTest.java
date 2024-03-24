package com.cherish.backend.controller;


import com.cherish.backend.controller.dto.request.DiaryEventRequest;
import com.cherish.backend.controller.dto.request.DiaryEventRequestList;
import com.cherish.backend.domain.*;
import com.cherish.backend.repositroy.AvatarRepository;
import com.cherish.backend.repositroy.BackUpRepository;
import com.cherish.backend.repositroy.DiaryRepository;
import com.cherish.backend.service.BackUpService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Transactional
@AutoConfigureMockMvc
class BackUpControllerTest {

    MockMvc mockMvc;

    @Autowired
    AvatarRepository avatarRepository;

    @Autowired
    WebApplicationContext context;

    ObjectMapper objectMapper = new ObjectMapper();

    MockHttpSession httpSession;

    Avatar avatar;
    @Autowired
    EntityManager em;
    BackUp backUp;
    Diary diary1, diary2, diary3;
    @Autowired
    BackUpService backUpService;
    @Autowired
    BackUpRepository backUpRepository;
    @Autowired
    DiaryRepository diaryRepository;
    DiaryEventRequest createEventRequest1, createEventRequest2, createEventRequest3;
    DiaryEventRequest editEventRequest1_1, editEventRequest1_2, editEventRequest1Last;
    DiaryEventRequest editEventRequest2, editEventRequest2Last, deleteEventRequest1, editExistEventRequest1, editExistEventRequest1_1, editExistEventRequest1_Last, editExistEventRequest2, deleteExistEventRequest1;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        httpSession = new MockHttpSession();

        avatar = Avatar.of("name1", LocalDate.now(), Gender.MALE);
        avatarRepository.save(avatar);

        httpSession.setAttribute(ConstValue.sessionName, avatar.getId());

        backUp = BackUp.of("os1", "device1", avatar);
        backUp.setCount(3);
        backUp = backUpRepository.save(backUp);

        diary1 = Diary.of(DiaryKind.FREE, "diary1", "title1", "content1", LocalDateTime.now(), avatar, backUp);
        diary2 = Diary.of(DiaryKind.FREE, "diary2", "title2", "content2", LocalDateTime.now(), avatar, backUp);
        diary3 = Diary.of(DiaryKind.FREE, "diary3", "title3", "content3", LocalDateTime.now(), avatar, backUp);

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
        editEventRequest1_1 = new DiaryEventRequest(createEventRequest1.getClientId(), createEventRequest1.getWritingDate(), "edittitle1_1", "editcontent1_1", DiaryKind.QUESTION, LocalDateTime.now().plusHours(1L));
        editEventRequest1_2 = new DiaryEventRequest(createEventRequest1.getClientId(), createEventRequest1.getWritingDate(), "edittitle1_2", "editcontent1_2", DiaryKind.FREE, LocalDateTime.now().plusHours(2L));
        editEventRequest1Last = new DiaryEventRequest(createEventRequest1.getClientId(), createEventRequest1.getWritingDate(), "edittitle1", "editcontent1", DiaryKind.EMOTION, LocalDateTime.now().plusHours(3L));

        editEventRequest2 = new DiaryEventRequest(createEventRequest2.getClientId(), createEventRequest2.getWritingDate(), "edittitle2", "editcontent2", DiaryKind.QUESTION, LocalDateTime.now().plusHours(2L));
        editEventRequest2Last = new DiaryEventRequest(createEventRequest2.getClientId(), createEventRequest2.getWritingDate(), "edittitle2Last", "editcontent2Last", DiaryKind.QUESTION, LocalDateTime.now().plusHours(3L));

        deleteEventRequest1 = new DiaryEventRequest(createEventRequest1.getClientId(), createEventRequest1.getWritingDate(), "title1", "content1", DiaryKind.FREE, LocalDateTime.now().plusHours(4L));

        editExistEventRequest1 = new DiaryEventRequest(diary1.getClientId(), diary1.getClientWritingDate(), "edittitle1_1", "editcontent1_1", DiaryKind.QUESTION, diary1.getClientWritingDate().plusHours(1L));
        editExistEventRequest1_1 = new DiaryEventRequest(diary1.getClientId(), diary1.getClientWritingDate(), "edittitle1_2", "editcontent1_1", DiaryKind.QUESTION, diary1.getClientWritingDate().plusHours(2L));
        editExistEventRequest1_Last = new DiaryEventRequest(diary1.getClientId(), diary1.getClientWritingDate(), "edittitle1LAST", "editcontent1LAST", DiaryKind.QUESTION, diary1.getClientWritingDate().plusHours(3L));
        editExistEventRequest2 = new DiaryEventRequest(diary2.getClientId(), diary2.getClientWritingDate(), "edittitle2LAST", "editcontent2LAST", DiaryKind.QUESTION, diary2.getClientWritingDate().plusHours(3L));

        deleteExistEventRequest1 = new DiaryEventRequest(diary1.getClientId(), diary1.getClientWritingDate(), "edittitleLAST", "editcontentLAST", DiaryKind.QUESTION, diary1.getClientWritingDate().plusHours(4L));
    }

    private String diaryEventRequestToString(DiaryEventRequest diaryEventRequest) {
        return "{" +
                "\"clientId\":\"" + diaryEventRequest.getClientId() + "\"," +
                "\"diaryKind\":\"" + diaryEventRequest.getDiaryKind().getValue() + "\"," +
                "\"title\":\"" + diaryEventRequest.getTitle() + "\"," +
                "\"content\":\"" + diaryEventRequest.getContent() + "\"," +
                "\"writingDate\":\"" + diaryEventRequest.getWritingDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\"," +
                "\"eventDate\":\"" + diaryEventRequest.getEventDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\"" +
                "}";
    }

    private String diaryEventRequestListString(DiaryEventRequestList diaryEventRequestList) {

        String json = "{";

        if (diaryEventRequestList.getCreateEventList() != null) {

            json += "\"createEventList\" : [";

            for (int i = 0; i < diaryEventRequestList.getCreateEventList().size(); i++) {
                if (i == diaryEventRequestList.getCreateEventList().size() - 1) {
                    json += diaryEventRequestToString(diaryEventRequestList.getCreateEventList().get(i)) + "],";
                    break;
                }
                json += diaryEventRequestToString(diaryEventRequestList.getCreateEventList().get(i)) + ",";
            }

        }

        if (diaryEventRequestList.getEditEventList() != null) {
            json += "\"editEventList\" : [";

            for (int i = 0; i < diaryEventRequestList.getEditEventList().size(); i++) {
                if (i == diaryEventRequestList.getEditEventList().size() - 1) {
                    json += diaryEventRequestToString(diaryEventRequestList.getEditEventList().get(i)) + "],";
                    break;
                }
                json += diaryEventRequestToString(diaryEventRequestList.getEditEventList().get(i)) + ",";
            }
        }

        if (diaryEventRequestList.getDeleteEventList() != null) {
            json += "\"deleteEventList\" : [";

            for (int i = 0; i < diaryEventRequestList.getDeleteEventList().size(); i++) {
                if (i == diaryEventRequestList.getDeleteEventList().size() - 1) {
                    json += diaryEventRequestToString(diaryEventRequestList.getDeleteEventList().get(i)) + "],";
                    break;
                }
                json += diaryEventRequestToString(diaryEventRequestList.getDeleteEventList().get(i)) + ",";
            }
        }
        return json + "\"deviceType\":\"" + diaryEventRequestList.getDeviceType() + "\"," +
                "\"osVersion\":\"" + diaryEventRequestList.getOsVersion() + "\"" +
                "}";

    }

    @Test
    @DisplayName("가장 최신의 백업이 존재하는 경우 가장 최신의 백업을 가져와야한다.")
    public void getBackUpAPITest() throws Exception {
        //given
        //when
        //then
        mockMvc.perform(get("/api/backup")
                        .session(httpSession)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.backUpId").exists())
                .andExpect(jsonPath("$.date").exists())
                .andExpect(jsonPath("$.deviceModel").exists())
                .andExpect(jsonPath("$.osVersion").exists())
                .andExpect(jsonPath("count").exists());
    }

    @Test
    @DisplayName("가장 최신의 백업이 존재하지 않는 경우 404 상태코드를 출력하여야 한다.")
    public void getBackUpAPIFailTest() throws Exception {
        //given
        Avatar avatar = Avatar.of("notexist", LocalDate.now(), Gender.FEMALE);
        avatar = avatarRepository.save(avatar);
        httpSession.setAttribute(ConstValue.sessionName, avatar.getId());
        //when
        //then
        mockMvc.perform(get("/api/backup/")
                        .session(httpSession)
                )
                .andExpect(status().isNotFound());

    }

    @Test
    @DisplayName("백업 아이디가 path 변수로 주어진 경우 저장되어있는 일기를 출력하여야한다.")
    public void restoreBackUpTest() throws Exception {
        mockMvc.perform(get("/api/backup/restore?backupId=" + backUp.getId())
                        .session(httpSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recordList[*].clientId").exists())
                .andExpect(jsonPath("$.recordList[*].title").exists())
                .andExpect(jsonPath("$.recordList[*].content").exists())
                .andExpect(jsonPath("$.recordList[*].diaryKind").exists())
                .andExpect(jsonPath("$.recordList[*].date").exists())
                .andExpect(jsonPath("$.backupData.backUpId").exists())
                .andExpect(jsonPath("$.backupData.deviceModel").exists())
                .andExpect(jsonPath("$.backupData.osVersion").exists())
                .andDo(print());

    }

    @Test
    @DisplayName("회원 ID가 존재하지 않는 요청을 수행한 경우 404 상태코드를 출력하여야 한다.")
    public void restoreBackUpFailTest1() throws Exception {
        //given
        Avatar avatar = Avatar.of("notexist", LocalDate.now(), Gender.FEMALE);
        avatar = avatarRepository.save(avatar);
        httpSession.setAttribute(ConstValue.sessionName, avatar.getId());
        //when
        //then
        mockMvc.perform(get("/api/backup/restore?backupId=" + backUp.getId())
                        .session(httpSession)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("백업 ID가 존재하지 않는 경우 404 상태코드를 출력하여야 한다.")
    public void restoreBackUpFailTest2() throws Exception {
        //given
        //when
        //then
        mockMvc.perform(get("/api/backup/restore?backupId=" + "notexist")
                        .session(httpSession)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("백업 요청으로 createList 들어온 경우 200상태코드와 백업 정보를 출력하여야 한다.")
    public void backUpDiaryTest1() throws Exception {
        //given
        List<DiaryEventRequest> createList = new ArrayList<>();
        createList.add(createEventRequest1);
        createList.add(createEventRequest2);
        createList.add(createEventRequest3);

        List<DiaryEventRequest> editList = new ArrayList<>();
        editList.add(editEventRequest1_1);
        editList.add(editEventRequest1_2);
        editList.add(editEventRequest1Last);
        editList.add(editEventRequest2);
        editList.add(editEventRequest2Last);

        List<DiaryEventRequest> deleteList = new ArrayList<>();
        deleteList.add(deleteEventRequest1);
        //when
        //then
        String requestJson = diaryEventRequestListString(new DiaryEventRequestList(createList, editList, deleteList, "os1", "divice1"));

        mockMvc.perform(post("/api/backup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(httpSession)
                        .content(requestJson)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.backUpId").exists())
                .andExpect(jsonPath("$.date").exists())
                .andExpect(jsonPath("$.deviceModel").exists())
                .andExpect(jsonPath("$.osVersion").exists())
                .andExpect(jsonPath("count").exists());
    }

    @Test
    @DisplayName("백업 요청으로 createList 들어온 경우 200상태코드와 백업 정보를 출력하여야 한다.")
    public void backUpDiaryTest2() throws Exception {
        //given
        List<DiaryEventRequest> createList = new ArrayList<>();
        createList.add(createEventRequest1);
        createList.add(createEventRequest2);
        createList.add(createEventRequest3);

        //when
        //then
        String requestJson = diaryEventRequestListString(new DiaryEventRequestList(createList, null, null, "os1", "divice1"));

        mockMvc.perform(post("/api/backup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(httpSession)
                        .content(requestJson)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.backUpId").exists())
                .andExpect(jsonPath("$.date").exists())
                .andExpect(jsonPath("$.deviceModel").exists())
                .andExpect(jsonPath("$.osVersion").exists())
                .andExpect(jsonPath("count").exists());
    }

    @Test
    @DisplayName("백업 요청이 editList만 들어온 경우 200상태코드와 백업 정보를 출력하여야 한다.")
    public void backUpDiaryTest3() throws Exception {
        //given

        List<DiaryEventRequest> editList = new ArrayList<>();
        editList.add(editEventRequest1_1);
        editList.add(editEventRequest1_2);
        editList.add(editEventRequest1Last);
        editList.add(editEventRequest2);
        editList.add(editEventRequest2Last);

        //when
        //then
        String requestJson = diaryEventRequestListString(new DiaryEventRequestList(null, editList, null, "os1", "divice1"));

        mockMvc.perform(post("/api/backup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(httpSession)
                        .content(requestJson)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.backUpId").exists())
                .andExpect(jsonPath("$.date").exists())
                .andExpect(jsonPath("$.deviceModel").exists())
                .andExpect(jsonPath("$.osVersion").exists())
                .andExpect(jsonPath("count").exists());
    }

    @Test
    @DisplayName("백업 요청으로 deleteList 들어온 경우 200상태코드와 백업 정보를 출력하여야 한다.")
    public void backUpDiaryTest4() throws Exception {
        //given
        List<DiaryEventRequest> deleteList = new ArrayList<>();
        deleteList.add(deleteEventRequest1);
        //when
        //then
        String requestJson = diaryEventRequestListString(new DiaryEventRequestList(null, null, deleteList, "os1", "divice1"));

        mockMvc.perform(post("/api/backup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(httpSession)
                        .content(requestJson)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.backUpId").exists())
                .andExpect(jsonPath("$.date").exists())
                .andExpect(jsonPath("$.deviceModel").exists())
                .andExpect(jsonPath("$.osVersion").exists())
                .andExpect(jsonPath("count").exists());
    }

}