package com.cherish.backend.controller.docs;

import com.cherish.backend.controller.ConstValue;
import com.cherish.backend.controller.dto.request.DiaryEventRequest;
import com.cherish.backend.controller.dto.request.DiaryEventRequestList;
import com.cherish.backend.controller.dto.response.BackUpHistoryResponse;
import com.cherish.backend.controller.dto.response.DiaryResponse;
import com.cherish.backend.controller.dto.response.RestoreDiaryResponse;
import com.cherish.backend.domain.*;
import com.cherish.backend.exception.NotExistBackUpHistoryException;
import com.cherish.backend.repositroy.AvatarRepository;
import com.cherish.backend.repositroy.BackUpRepository;
import com.cherish.backend.repositroy.DiaryRepository;
import com.cherish.backend.service.BackUpService;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.SimpleType;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureRestDocs
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class BackUpControllerDocs {


    @Autowired
    MockMvc mockMvc;

    @Autowired
    WebApplicationContext webApplicationContext;

    ObjectMapper objectMapper = new ObjectMapper();

    MockHttpSession session = new MockHttpSession();

    @Autowired
    AvatarRepository avatarRepository;

    @Autowired
    WebApplicationContext context;

    @MockBean
    BackUpService backUpService;

    @Autowired
    DiaryRepository diaryRepository;

    @Autowired
    EntityManager em;

    Avatar avatar;

    BackUp backUp;

    Diary diary1, diary2, diary3;

    @Autowired
    BackUpRepository backUpRepository;

    DiaryEventRequest createEventRequest1, createEventRequest2, createEventRequest3;
    DiaryEventRequest editEventRequest1_1, editEventRequest1_2, editEventRequest1Last;

    DiaryEventRequest editEventRequest2, editEventRequest2Last, deleteEventRequest1, editExistEventRequest1, editExistEventRequest1_1, editExistEventRequest1_Last, editExistEventRequest2, deleteExistEventRequest1;

    @BeforeEach
    void init(RestDocumentationContextProvider restDocumentationContextProvider) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(documentationConfiguration(restDocumentationContextProvider))
                .build();
        session = new MockHttpSession();


        avatar = Avatar.of("name1", LocalDate.now(), Gender.MALE);

        avatarRepository.save(avatar);
        avatar = em.find(Avatar.class, avatar.getId());

        backUp = BackUp.of("os1", "device1", avatar);

        backUpRepository.save(backUp);
        backUp = em.find(BackUp.class, backUp.getId());

        System.out.println(avatar);

        session.setAttribute(ConstValue.sessionName, avatar.getId());

        diary1 = Diary.of(DiaryKind.FREE, "diary1", "title1", "content1", LocalDateTime.now(), avatar, backUp);
        diary2 = Diary.of(DiaryKind.FREE, "diary2", "title2", "content2", LocalDateTime.now(), avatar, backUp);
        diary3 = Diary.of(DiaryKind.FREE, "diary3", "title3", "content3", LocalDateTime.now(), avatar, backUp);


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
                "\"clientWritingDate\":\"" + diaryEventRequest.getClientWritingDate() + "\"," +
                "\"eventDate\":\"" + diaryEventRequest.getEventDate() + "\"" +
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
    @DisplayName("일기 event 리스트가 정상적으로 들어온 경우에 대한 테스트")
    public void backUpDiaryDocsTest() throws Exception {
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
        DiaryEventRequestList requestList = new DiaryEventRequestList(createList, editList, deleteList, "os1", "divice1");
        String requestJson = diaryEventRequestListString(requestList);

        given(backUpService.backup(any(DiaryEventRequestList.class), eq(avatar.getId()))).willReturn(new BackUpHistoryResponse("qgas112", LocalDateTime.now(), "iphone99", "ios99", 3));
        //when
        //then
        mockMvc.perform(post("/api/backup")
                        .session(session)
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("일기 저장하기",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .tag("BackUp API")
                                .summary("일기 백업하기")
                                .description("일기 백업이 성공하는 경우 입니다.")
                                .requestFields(
                                        fieldWithPath("createEventList[].title").type("String").description("일기의 제목 입니다."),
                                        fieldWithPath("createEventList[].clientId").type("String").description("일기의 클라이언트 ID 저장값 입니다."),
                                        fieldWithPath("createEventList[].content").type("String").description("일기의 내용 입니다."),
                                        fieldWithPath("createEventList[].diaryKind").type("String").description("일기의 형식 입니다."),
                                        fieldWithPath("createEventList[].clientWritingDate").type("String").description("일기의 클라이언트 저장 시간 입니다."),
                                        fieldWithPath("createEventList[].eventDate").type("String").description("일기의 이벤트가 일어난 시간입니다.."),
                                        fieldWithPath("editEventList[].clientId").type("String").description("일기의 클라이언트 ID 저장값 입니다."),
                                        fieldWithPath("editEventList[].title").type("String").description("일기의 제목 입니다."),
                                        fieldWithPath("editEventList[].content").type("String").description("일기의 내용 입니다."),
                                        fieldWithPath("editEventList[].diaryKind").type("String").description("일기의 형식 입니다."),
                                        fieldWithPath("editEventList[].clientWritingDate").type("String").description("일기의 클라이언트 저장 시간 입니다."),
                                        fieldWithPath("editEventList[].eventDate").type("String").description("일기의 이벤트가 일어난 시간입니다.."),
                                        fieldWithPath("deleteEventList[].clientId").type("String").description("일기의 클라이언트 ID 저장값 입니다."),
                                        fieldWithPath("deleteEventList[].title").type("String").description("일기의 제목 입니다."),
                                        fieldWithPath("deleteEventList[].content").type("String").description("일기의 내용 입니다."),
                                        fieldWithPath("deleteEventList[].diaryKind").type("String").description("일기의 형식 입니다."),
                                        fieldWithPath("deleteEventList[].clientWritingDate").type("String").description("일기의 클라이언트 저장 시간 입니다."),
                                        fieldWithPath("deleteEventList[].eventDate").type("String").description("일기의 이벤트가 일어난 시간입니다.."),
                                        fieldWithPath("osVersion").type("String").description("기기 OS Version을 입력해주세요."),
                                        fieldWithPath("deviceType").type("String").description("기기의 이름을 입력해주시요")
                                )
                                .responseFields(
                                        fieldWithPath("backUpId").type("String").description("일기가 저장되어 있는 최신 백업 아이디입니다."),
                                        fieldWithPath("date").type("String").description("저장된 시간 입니다."),
                                        fieldWithPath("deviceModel").type("String").description("기기 모델명 입니다."),
                                        fieldWithPath("osVersion").type("String").description("osVersion입니다."),
                                        fieldWithPath("count").type("number").description("저장된 일기의 수 입니다.")
                                ).build())));


    }

    @Test
    @DisplayName("가장 최신의 백업이 저장되어있는 경우 요청시 가장 최신 백업을 찾아 전달한다.")
    public void getBackUpDocsSuccessTest() throws Exception {
        //given
        given(backUpService.getBackUp(avatar.getId())).willReturn(new BackUpHistoryResponse("qgas112", LocalDateTime.now(), "iphone99", "ios99", 3));
        //when
        //then
        mockMvc.perform(get("/api/backup")
                        .session(session))
                .andExpect(status().isOk())
                .andDo(document("가장 최신 백업 정보 가져오기",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .tag("BackUp API")
                                .summary("가장 최신 백업 정보 가져오기")
                                .description("가장 최신 백업이 존재하는 경우 갖아 최신 백업 정보를 가져오는 경우 입니다.")
                                .responseFields(
                                        fieldWithPath("backUpId").type("String").description("일기가 저장되어 있는 최신 백업 아이디입니다."),
                                        fieldWithPath("date").type("String").description("저장된 시간 입니다."),
                                        fieldWithPath("deviceModel").type("String").description("기기 모델명 입니다."),
                                        fieldWithPath("osVersion").type("String").description("osVersion입니다."),
                                        fieldWithPath("count").type("number").description("저장된 일기의 수 입니다.")
                                ).build())));


    }

    @Test
    @DisplayName("가장 최신의 백업이 저장되어 있지 않은 경우 예외를 출력한다.")
    public void getBackUpDocsFailTest() throws Exception {
        given(backUpService.getBackUp(avatar.getId())).willThrow(new NotExistBackUpHistoryException());
        //when
        //then
        mockMvc.perform(get("/api/backup/")
                        .session(session))
                .andExpect(status().isNotFound())
                .andDo(document("가장 최신 백업 정보 가져오기",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .tag("BackUp API")
                                .summary("가장 최신 백업 정보 가져오기")
                                .description("최신 백업 정보가 없는 경우 예외 메세지를 출력하는 경우 입니다.")
                                .build()
                        )));
    }

    @Test
    @DisplayName("존재하는 백업 아이디 요청을 보낸 경우 저장되어있는 일기를 출력한다.")
    public void restoreDiaryDocsTest() throws Exception {
        //given
        DiaryResponse response1 = new DiaryResponse("diaryId1", "diarytitle1", "content1", DiaryKind.FREE, LocalDateTime.now());
        DiaryResponse response2 = new DiaryResponse("diaryId2", "diarytitle2", "content2", DiaryKind.FREE, LocalDateTime.now());
        DiaryResponse response3 = new DiaryResponse("diaryId3", "diarytitle3", "content3", DiaryKind.FREE, LocalDateTime.now());
        List<DiaryResponse> list = new ArrayList<>();
        list.add(response1);
        list.add(response2);
        list.add(response3);

        given(backUpService.restoreDiary(avatar.getId(), backUp.getId())).willReturn(new RestoreDiaryResponse(list, backUp.getId(), LocalDateTime.now(), 9, "ios99", "os1"));
        //when
        //then
        mockMvc.perform(get("/api/backup/restore?backupId=" + backUp.getId())
                        .session(session))
                .andExpect(status().isOk())
                .andDo(document("백업 아이디를 이용해 일기 가져오기",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .tag("BackUp API")
                                .summary("백업 아이디를 이용해 일기 가져오기")
                                .description("백업 아이디가 존재하는 경우 이를 url pathvariable에 입력해준 경우 일기 리스트를 출력합니다.")
                                .queryParameters(
                                        parameterWithName("backupId").type(SimpleType.STRING).description("backupId를 입력해주세요.")
                                )
                                .responseFields(
                                        fieldWithPath("diaryList.[].clientId").type("String").description("일기의 클라이언트 ID 저장값 입니다."),
                                        fieldWithPath("diaryList.[].title").type("String").description("일기의 제목 입니다."),
                                        fieldWithPath("diaryList.[].content").type("String").description("일기의 내용 입니다."),
                                        fieldWithPath("diaryList.[].diaryKind").type("String").description("일기의 형식 입니다."),
                                        fieldWithPath("diaryList.[].clientWritingDate").type("String").description("일기의 클라이언트 저장 시간 입니다."),
                                        fieldWithPath("backUpId").type("String").description("일기가 저장되어 있는 최신 백업 아이디입니다."),
                                        fieldWithPath("date").type("String").description("저장된 시간 입니다."),
                                        fieldWithPath("deviceModel").type("String").description("기기 모델명 입니다."),
                                        fieldWithPath("osVersion").type("String").description("osVersion입니다."),
                                        fieldWithPath("count").type("number").description("저장된 일기의 수 입니다.")
                                ).build())));
    }

    @Test
    @DisplayName("백업 아이디로 요청시 일기가 존재하지 않는 경우 예외 메세지를 출력한다.")
    public void restoreDiaryDocsTest2() throws Exception {
        //given
        given(backUpService.restoreDiary(avatar.getId(), backUp.getId())).willThrow(new NotExistBackUpHistoryException());
        //when
        //then
        mockMvc.perform(get("/api/backup/restore?backupId=" + backUp.getId())
                        .session(session))
                .andExpect(status().isNotFound())
                .andDo(document("백업 아이디를 이용해 일기 가져오기",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .tag("BackUp API")
                                .summary("백업 아이디를 이용해 일기 가져오기")
                                .description("백업 아이디에 일기가 존재하지 않는 경우 예외를 출력합니다.")
                                .queryParameters(
                                        parameterWithName("backupId").type(SimpleType.STRING).description("backupId를 입력해주세요.")
                                ).build())));
    }


}
