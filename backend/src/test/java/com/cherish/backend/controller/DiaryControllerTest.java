package com.cherish.backend.controller;

import com.cherish.backend.controller.dto.request.BackUpDiaryRequest;
import com.cherish.backend.controller.dto.request.DiaryRequest;
import com.cherish.backend.controller.dto.request.FirstTimeBackUpDiaryRequest;
import com.cherish.backend.domain.*;
import com.cherish.backend.repositroy.AccountRepository;
import com.cherish.backend.repositroy.AvatarRepository;
import com.cherish.backend.repositroy.BackUpRepository;
import com.cherish.backend.repositroy.DiaryRepository;
import com.cherish.backend.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
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
class DiaryControllerTest {

    MockMvc mockMvc;

    @Autowired
    WebApplicationContext context;

    ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;
    @Autowired
    AvatarRepository avatarRepository;
    @Autowired
    DiaryRepository diaryRepository;

    @Autowired
    BackUpRepository backUpRepository;

    MockHttpSession httpSession;

    Avatar avatar;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        httpSession = new MockHttpSession();

        avatar = Avatar.of("name1", LocalDate.now(), Gender.MALE);
        avatarRepository.save(avatar);

        httpSession.setAttribute(ConstValue.sessionName, avatar.getId());
    }

    @AfterEach
    void clean() {
        httpSession.clearAttributes();
    }

    private String firstTimeBackUpDiaryRequestToString(FirstTimeBackUpDiaryRequest request) {
        List<String> list = request.getDiaryRequestList().stream().map(req -> diaryRequestToString(req))
                .toList();

        String json = "{\"diaryRequestList\" : [";

        for (int i = 0; i < list.size(); i++) {
            if (i == list.size() - 1) {
                json += diaryRequestToString(request.getDiaryRequestList().get(i)) + "],";
                break;
            }
            json += diaryRequestToString(request.getDiaryRequestList().get(i)) + ",";
        }
        return json + "\"deviceType\":\"" + request.getDeviceType() + "\"," +
                "\"deviceId\":\"" + request.getDeviceId() + "\"," +
                "\"osVersion\":\"" + request.getOsVersion() + "\"}";
    }

    private String backUpDiaryRequestToString(BackUpDiaryRequest request) {
        List<String> list = request.getDiaryRequestList().stream().map(req -> diaryRequestToString(req))
                .toList();

        String json = "{\"diaryRequestList\" : [";

        for (int i = 0; i < list.size(); i++) {
            if (i == list.size() - 1) {
                json += diaryRequestToString(request.getDiaryRequestList().get(i)) + "],";
                break;
            }
            json += diaryRequestToString(request.getDiaryRequestList().get(i)) + ",";
        }
        return json + "\"deviceType\":\"" + request.getDeviceType() + "\"," +
                "\"deviceId\":\"" + request.getDeviceId() + "\"," +
                "\"osVersion\":\"" + request.getOsVersion() + "\"," +
                "\"backUpId\":\"" + request.getBackUpId() + "\"" +
                "}";
    }

    private String diaryRequestToString(DiaryRequest diaryRequest) {

        if (diaryRequest.equals("null")) {
            return "{" +
                    "\"kind\":\"" + diaryRequest.getKind().getValue() + "\"," +
                    "\"title\":\"" + diaryRequest.getTitle() + "\"," +
                    "\"content\":\"" + diaryRequest.getContent() + "\"," +
                    "\"date\":\"" + diaryRequest.getDate() + "\"" +
                    "}";
        } else {
            return "{" +
                    "\"id\":\"" + diaryRequest.getId() + "\"," +
                    "\"kind\":\"" + diaryRequest.getKind().getValue() + "\"," +
                    "\"title\":\"" + diaryRequest.getTitle() + "\"," +
                    "\"content\":\"" + diaryRequest.getContent() + "\"," +
                    "\"date\":\"" + diaryRequest.getDate() + "\"" +
                    "}";
        }
    }


    @Test
    @DisplayName("[API][DIARY] 처음 백업하는 API 성공 테스트")
    public void firstTimeBackUpControllerTest() throws Exception {
        //given
        DiaryRequest diaryRequest1 = new DiaryRequest("test1", DiaryKind.FREE, "testtitle1", "testcontent", "2022-07-25 12:45:09 +0000");
        DiaryRequest diaryRequest2 = new DiaryRequest("test2", DiaryKind.FREE, "testtitle2", "testcontent", "2022-07-25 12:45:09 +0000");
        List<DiaryRequest> diaryRequests = new ArrayList<>();
        diaryRequests.add(diaryRequest1);
        diaryRequests.add(diaryRequest2);
        FirstTimeBackUpDiaryRequest request = new FirstTimeBackUpDiaryRequest(diaryRequests, "device1", "deviceId1", "osVersion");
        //when
        //then
        mockMvc.perform(post("/api/diary/firsttimebackup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(firstTimeBackUpDiaryRequestToString(request))
                        .session(httpSession)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deviceType").exists())
                .andExpect(jsonPath("$.backUpId").exists())
                .andExpect(jsonPath("$.saveTime").exists())
                .andExpect(jsonPath("$.osVersion").exists())
                .andExpect(jsonPath("$.count").exists());
    }

    LocalDateTime stringToLocalDateTime(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss +SSSS");
        return LocalDateTime.parse(date, formatter);
    }

    @Test
    @DisplayName("[API][DIARY] 기존에 백업 기록이 존재하는 데 처음 백업하는 API 실패 테스트")
    public void firstTimeBackUpControllerFailTest() throws Exception {
        //given
        BackUp backUp = BackUp.of( "os1", "devicetype1", 2, avatar);
        Diary diary1 = Diary.of(DiaryKind.FREE, "testtitle1", "testcontent", stringToLocalDateTime("2022-07-25 12:45:09 +0000"), "devicetype1", "device1", avatar, backUp);
        Diary diary2 = Diary.of(DiaryKind.FREE, "testtitle1", "testcontent", stringToLocalDateTime("2022-07-25 12:45:09 +0000"), "devicetype1", "device1", avatar, backUp);

        backUpRepository.save(backUp);
        diaryRepository.save(diary1);
        diaryRepository.save(diary2);

        DiaryRequest diaryRequest1 = new DiaryRequest("test1", DiaryKind.FREE, "testtitle1", "testcontent", "2022-07-25 12:45:09 +0000");
        DiaryRequest diaryRequest2 = new DiaryRequest("test2", DiaryKind.FREE, "testtitle2", "testcontent", "2022-07-25 12:45:09 +0000");
        List<DiaryRequest> diaryRequests = new ArrayList<>();

        diaryRequests.add(diaryRequest1);
        diaryRequests.add(diaryRequest2);
        FirstTimeBackUpDiaryRequest request = new FirstTimeBackUpDiaryRequest(diaryRequests, "device1", "deviceId1", "osVersion");
        //when
        //then


        mockMvc.perform(post("/api/diary/firsttimebackup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(firstTimeBackUpDiaryRequestToString(request))
                        .session(httpSession)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("[API][DIARY] 이미 백업기록이 존재하는 상태로 백업하는 API 테스트")
    public void backUpControllerTest() throws Exception {
        BackUp backUp = BackUp.of("os1", "devicetype1", 2, avatar);
        Diary diary1 = Diary.of(DiaryKind.FREE, "testtitle1", "testcontent", stringToLocalDateTime("2022-07-25 12:45:09 +0000"), "devicetype1", "device1", avatar, backUp);
        Diary diary2 = Diary.of(DiaryKind.FREE, "testtitle1", "testcontent", stringToLocalDateTime("2022-07-25 12:45:09 +0000"), "devicetype1", "device1", avatar, backUp);

        backUpRepository.save(backUp);
        diaryRepository.save(diary1);
        diaryRepository.save(diary2);

        //given
        DiaryRequest diaryRequest1 = new DiaryRequest(diary1.getId(), DiaryKind.FREE, diary1.getTitle(), diary1.getContent(), "2022-07-25 12:45:09 +0000");
        DiaryRequest diaryRequest2 = new DiaryRequest(diary2.getId(), DiaryKind.FREE, diary2.getTitle(), diary2.getContent(), "2022-07-25 12:45:09 +0000");
        DiaryRequest diaryRequest3 = new DiaryRequest(null, DiaryKind.FREE, "testtitle3", "testcontent", "2022-07-25 12:45:09 +0000");
        List<DiaryRequest> diaryRequests = new ArrayList<>();

        diaryRequests.add(diaryRequest1);
        diaryRequests.add(diaryRequest2);
        diaryRequests.add(diaryRequest3);

        BackUp findBackUp = backUpRepository.findBackUpByIdLatest(avatar.getId()).get();
        BackUpDiaryRequest backUpDiaryRequest = new BackUpDiaryRequest(diaryRequests, "devicetype1", "device1", "os1", findBackUp.getId());

        //when
        //then
        mockMvc.perform(post("/api/diary/backup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(backUpDiaryRequestToString(backUpDiaryRequest))
                        .session(httpSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deviceType").exists())
                .andExpect(jsonPath("$.backUpId").exists())
                .andExpect(jsonPath("$.saveTime").exists())
                .andExpect(jsonPath("$.osVersion").exists())
                .andExpect(jsonPath("$.count").exists());
    }

    @Test
    @DisplayName("만약 backUpController시에 세션 값이 잘못된 경우 상태코드 300 출력한다.")
    public void backUpControllerFailTest1() throws Exception {
        //given
        BackUp backUp = BackUp.of("os1", "devicetype1", 2, avatar);
        Diary diary1 = Diary.of(DiaryKind.FREE, "testtitle1", "testcontent", stringToLocalDateTime("2022-07-25 12:45:09 +0000"), "devicetype1", "device1", avatar, backUp);
        Diary diary2 = Diary.of(DiaryKind.FREE, "testtitle1", "testcontent", stringToLocalDateTime("2022-07-25 12:45:09 +0000"), "devicetype1", "device1", avatar, backUp);

        backUpRepository.save(backUp);
        diaryRepository.save(diary1);
        diaryRepository.save(diary2);

        DiaryRequest diaryRequest1 = new DiaryRequest(diary1.getId(), DiaryKind.FREE, diary1.getTitle(), diary1.getContent(), "2022-07-25 12:45:09 +0000");
        DiaryRequest diaryRequest2 = new DiaryRequest(diary2.getId(), DiaryKind.FREE, diary2.getTitle(), diary2.getContent(), "2022-07-25 12:45:09 +0000");
        DiaryRequest diaryRequest3 = new DiaryRequest(null, DiaryKind.FREE, "testtitle3", "testcontent", "2022-07-25 12:45:09 +0000");
        List<DiaryRequest> diaryRequests = new ArrayList<>();

        diaryRequests.add(diaryRequest1);
        diaryRequests.add(diaryRequest2);
        diaryRequests.add(diaryRequest3);

        BackUp findBackUp = backUpRepository.findBackUpByIdLatest(avatar.getId()).get();
        BackUpDiaryRequest backUpDiaryRequest = new BackUpDiaryRequest(diaryRequests, "devicetype1", "device1", "os1", findBackUp.getId());


        httpSession.clearAttributes();
        httpSession.setAttribute(ConstValue.sessionName, 30L);
        //when
        //then
        mockMvc.perform(post("/api/diary/backup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(backUpDiaryRequestToString(backUpDiaryRequest))
                        .session(httpSession))
                .andExpect(status().isMultipleChoices());
    }

    @Test
    @DisplayName("만약 backUpController시에 backup 값이 잘못된 경우 상태코드 404을 출력한다.")
    public void backUpControllerFailTest2() throws Exception {
        //given
        BackUp backUp = BackUp.of( "os1", "devicetype1", 2, avatar);
        Diary diary1 = Diary.of(DiaryKind.FREE, "testtitle1", "testcontent", stringToLocalDateTime("2022-07-25 12:45:09 +0000"), "devicetype1", "device1", avatar, backUp);
        Diary diary2 = Diary.of(DiaryKind.FREE, "testtitle1", "testcontent", stringToLocalDateTime("2022-07-25 12:45:09 +0000"), "devicetype1", "device1", avatar, backUp);

        backUpRepository.save(backUp);
        diaryRepository.save(diary1);
        diaryRepository.save(diary2);

        DiaryRequest diaryRequest1 = new DiaryRequest(diary1.getId(), DiaryKind.FREE, diary1.getTitle(), diary1.getContent(), "2022-07-25 12:45:09 +0000");
        DiaryRequest diaryRequest2 = new DiaryRequest(diary2.getId(), DiaryKind.FREE, diary2.getTitle(), diary2.getContent(), "2022-07-25 12:45:09 +0000");
        DiaryRequest diaryRequest3 = new DiaryRequest(null, DiaryKind.FREE, "testtitle3", "testcontent", "2022-07-25 12:45:09 +0000");
        List<DiaryRequest> diaryRequests = new ArrayList<>();

        diaryRequests.add(diaryRequest1);
        diaryRequests.add(diaryRequest2);
        diaryRequests.add(diaryRequest3);

        BackUp findBackUp = backUpRepository.findBackUpByIdLatest(avatar.getId()).get();
        BackUpDiaryRequest backUpDiaryRequest = new BackUpDiaryRequest(diaryRequests, "devicetype1", "device1", "os1", "asdasdasdas");
        //when
        //then
        mockMvc.perform(post("/api/diary/backup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(backUpDiaryRequestToString(backUpDiaryRequest))
                        .session(httpSession))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("[API][DIARY] 가장 최근 일기 조회 테스트")
    public void getDiaryListByBackUpIdTest() throws Exception {
        //given
        BackUp backUp = BackUp.of( "os1", "devicetype1", 2, avatar);
        Diary diary1 = Diary.of(DiaryKind.FREE, "testtitle1", "testcontent", stringToLocalDateTime("2022-07-25 12:45:09 +0000"), "devicetype1", "device1", avatar, backUp);
        Diary diary2 = Diary.of(DiaryKind.FREE, "testtitle1", "testcontent", stringToLocalDateTime("2022-07-25 12:45:09 +0000"), "devicetype1", "device1", avatar, backUp);

        backUpRepository.save(backUp);

        diaryRepository.save(diary1);
        diaryRepository.save(diary2);
        //when
        //then
        mockMvc.perform(get("/api/diary?id=" + backUp.getId())
                        .session(httpSession)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]").isArray());
    }

    @Test
    @DisplayName("[API][DIARY] 가장 최근 일기 조회 할때 백업 아이디가 존재하지 않으면 상태코드 404을 출력한다.")
    public void getDiaryListByBackUpIdFailTest() throws Exception {
        //given
        //when
        //then
        mockMvc.perform(get("/api/diary?id=")
                        .session(httpSession)
                )
                .andExpect(status().isNotFound());
    }
}