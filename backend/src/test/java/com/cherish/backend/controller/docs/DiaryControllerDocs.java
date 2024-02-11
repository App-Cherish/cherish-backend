package com.cherish.backend.controller.docs;

import com.cherish.backend.controller.ConstValue;
import com.cherish.backend.controller.dto.request.BackUpDiaryRequest;
import com.cherish.backend.controller.dto.request.DiaryRequest;
import com.cherish.backend.controller.dto.request.FirstTimeBackUpDiaryRequest;
import com.cherish.backend.controller.dto.response.BackUpDairyResponse;
import com.cherish.backend.controller.dto.response.DiaryResponse;
import com.cherish.backend.domain.DiaryKind;
import com.cherish.backend.repositroy.AvatarRepository;
import com.cherish.backend.service.BackUpService;
import com.cherish.backend.service.DiaryService;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.epages.restdocs.apispec.SimpleType;
import com.fasterxml.jackson.databind.ObjectMapper;
import jdk.swing.interop.SwingInterOpUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class DiaryControllerDocs {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    WebApplicationContext context;

    ObjectMapper objectMapper = new ObjectMapper();

    MockHttpSession session;

    @MockBean
    DiaryService diaryService;

    @MockBean
    BackUpService backUpService;

    @Autowired
    AvatarRepository avatarRepository;

    @BeforeEach
    void init(RestDocumentationContextProvider restDocumentationContextProvider) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(documentationConfiguration(restDocumentationContextProvider))
                .build();
        session = new MockHttpSession();

        session.setAttribute(ConstValue.sessionName, 1L);
    }

    private String firstTimeBackUpDiaryRequestToString(FirstTimeBackUpDiaryRequest request) {
        List<String> list = request.getDiaryRequestList().stream().map(req -> diaryRequestToString(req))
                .toList();

        String json = "{\"diaryRequestList\" : [";

        for(int i=0;i<list.size();i++){
            if(i==list.size()-1) {
                json += diaryRequestToString(request.getDiaryRequestList().get(i)) + "],";
                break;
            }
            json += diaryRequestToString(request.getDiaryRequestList().get(i)) + ",";
        }
        return json + "\"deviceType\":\""+request.getDeviceType()+"\"," +
                "\"deviceId\":\""+request.getDeviceId()+"\"," +
                "\"osVersion\":\""+request.getOsVersion()+"\"}";
    }

    private String backUpDiaryRequestToString(BackUpDiaryRequest request) {
        List<String> list = request.getDiaryRequestList().stream().map(req -> diaryRequestToString(req))
                .toList();

        String json = "{\"diaryRequestList\" : [";

        for(int i=0;i<list.size();i++){
            if(i==list.size()-1) {
                json += diaryRequestToString(request.getDiaryRequestList().get(i)) + "],";
                break;
            }
            json += diaryRequestToString(request.getDiaryRequestList().get(i)) + ",";
        }
        return json + "\"deviceType\":\""+request.getDeviceType()+"\"," +
                "\"deviceId\":\""+request.getDeviceId()+"\"," +
                "\"osVersion\":\""+request.getOsVersion()+"\"," +
                "\"backUpId\":\""+request.getBackUpId()+"\"" +
                "}";
    }

    private String diaryRequestToString(DiaryRequest diaryRequest) {
        return "{" +
                "\"id\":\""+diaryRequest.getId()+"\"," +
                "\"kind\":\"" + diaryRequest.getKind().getValue() + "\"," +
                "\"title\":\""+diaryRequest.getTitle()+"\"," +
                "\"content\":\""+diaryRequest.getContent()+"\"," +
                "\"date\":\""+diaryRequest.getDate()+"\"" +
                "}";
    }


    @Test
    @DisplayName("[DOCS]일기 처음 백업 기능 문서화")
    public void firstTimeBackUpDocsSuccessTest() throws Exception {
        //given
        given(diaryService.firstTimeBackUp(any(FirstTimeBackUpDiaryRequest.class), eq(1L)))
                .willReturn(new BackUpDairyResponse("os1", "iphone15", "backup1", LocalDateTime.now(),3));

        List<DiaryRequest> diaryRequests = new ArrayList<>();
        DiaryRequest diaryRequest1 = new DiaryRequest(null, DiaryKind.FREE, "testtitle1", "testcontent", "2022-07-25 12:45:09 +0000");
        DiaryRequest diaryRequest2 = new DiaryRequest(null, DiaryKind.FREE, "testtitle2", "testcontent", "2022-07-25 12:45:09 +0000");
        diaryRequests.add(diaryRequest1);
        diaryRequests.add(diaryRequest2);

        FirstTimeBackUpDiaryRequest firstTimeBackUpDiaryRequest = new FirstTimeBackUpDiaryRequest(diaryRequests, "device1", "deviceId1", "os1");


        String json = firstTimeBackUpDiaryRequestToString(firstTimeBackUpDiaryRequest);
        //when
        //then
        mockMvc.perform(post("/api/diary/firsttimebackup")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("diaryFirstBackUp",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .tag("DIARY API(Header에 로그인시 받은 SESSION 쿠키값이 항상 입력이 되있어야 합니다!)")
                                .summary("기존에 백업 기록 없이 처음으로 백업하는 경우")
                                .requestFields(
                                        fieldWithPath("diaryRequestList[].id").type("String").description("서버에서 발급받은 id를 입력해주세요. 만약 처음 업로드 하는 경우 null로 입력해주세요.."),
                                        fieldWithPath("diaryRequestList[].title").type("String").description("기기에서 입력한 제목을 입력해주세요."),
                                        fieldWithPath("diaryRequestList[].content").type("String").description("기기에서 입력한 일기의 내용을 입력해주세요."),
                                        fieldWithPath("diaryRequestList[].date").type("String").description("기기에서 일기가 작성된 date를 입력해주세요. (yyyy-mm-dd hh:mm:ss +SSSS) \n ex) 2023-12-13 23:13:10 +1409"),
                                        fieldWithPath("diaryRequestList[].kind").type("String").description("일기의 종류를 입력해주세요, 만약 포함이 되지않는 일기의 종류가 있다면 오류를 뱉어냅니다. (허용되는 종류 : 감정형식,자유형식,질문형식)"),
                                        fieldWithPath("deviceType").type("String").description("기기의 종류를 입력해주세요."),
                                        fieldWithPath("deviceId").type("String").description("기기의 고유값을 입력해주세요."),
                                        fieldWithPath("osVersion").type("String").description("기기의 os버전을 입력해주세요.")
                                )
                                .requestSchema(Schema.schema("Diary Scheme"))
                                .build())));
    }

    @Test
    @DisplayName("[DOCS]일기 백업 기능(두번째이상) 문서화")
    public void backUpDocsSuccessTest() throws Exception {
        //given
        given(diaryService.backUp(any(BackUpDiaryRequest.class),eq(1L)))
                .willReturn(new BackUpDairyResponse("os1", "iphone150", "asdasdas22", LocalDateTime.now(),2));

        List<DiaryRequest> diaryRequests = new ArrayList<>();
        DiaryRequest diaryRequest1 = new DiaryRequest("asdasdasd", DiaryKind.FREE, "testtitle1", "testcontent", "2022-07-25 12:45:09 +0000");
        DiaryRequest diaryRequest2 = new DiaryRequest("asdasdww2", DiaryKind.FREE, "testtitle2", "testcontent", "2022-07-25 12:45:09 +0000");
        diaryRequests.add(diaryRequest1);
        diaryRequests.add(diaryRequest2);

        BackUpDiaryRequest backUpDiaryRequest = new BackUpDiaryRequest(diaryRequests, "device1", "deviceId1", "os1", "newBackUpID");
        String json = backUpDiaryRequestToString(backUpDiaryRequest);
        System.out.println(json);
        //when
        //then
        mockMvc.perform(post("/api/diary/backup")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("diaryBackUpRequest",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .tag("DIARY API(Header에 로그인시 받은 SESSION 쿠키값이 항상 입력이 되있어야 합니다!)")
                                .summary("기존에 백업이 존재하는 상태에서 백업하는 경우")
                                .requestFields(
                                        fieldWithPath("diaryRequestList[].id").type("String").description("서버에서 발급받은 id를 입력해주세요. 만약 처음 업로드 하는 경우 json 포맷안에 포함하지 않아도 됩니다."),
                                        fieldWithPath("diaryRequestList[].title").type("String").description("기기에서 입력한 제목을 입력해주세요."),
                                        fieldWithPath("diaryRequestList[].content").type("String").description("기기에서 입력한 일기의 내용을 입력해주세요."),
                                        fieldWithPath("diaryRequestList[].date").type("String").description("기기에서 일기가 작성된 date를 입력해주세요. (yyyy-mm-dd hh:mm:ss +SSSS) \n ex) 2023-12-13 23:13:10 +1409"),
                                        fieldWithPath("diaryRequestList[].kind").type("String").description("일기의 종류를 입력해주세요, 만약 포함이 되지않는 일기의 종류가 있다면 오류를 뱉어냅니다. (허용되는 종류 : 감정형식,자유형식,질문형식)"),
                                        fieldWithPath("deviceType").type("String").description("기기의 종류를 입력해주세요."),
                                        fieldWithPath("deviceId").type("String").description("기기의 고유값을 입력해주세요."),
                                        fieldWithPath("osVersion").type("String").description("기기의 os버전을 입력해주세요."),
                                        fieldWithPath("backUpId").type("String").description("기존의 백업 ID를 입력해주세요.")
                                )
                                .requestSchema(Schema.schema("Diary Scheme"))
                                .build())));
    }

    @Test
    @DisplayName("[DOCS][DIARY] 저장되어있는 일기 목록 불러오기")
    public void getDiaryListByBackUpIdList() throws Exception {
        //given
        List<DiaryResponse> diaryDtos = new ArrayList<>();
        diaryDtos.add(new DiaryResponse("id1", "title1", "content1", LocalDateTime.now(), "device1", "devicetype1"));
        diaryDtos.add(new DiaryResponse("id2", "title2", "content2", LocalDateTime.now(), "device2", "devicetype2"));
        diaryDtos.add(new DiaryResponse("id3", "title3", "content3", LocalDateTime.now(), "device3", "devicetype3"));

        given(diaryService.getRecentDiaryList(anyString(), eq(1L)))
                .willReturn(diaryDtos);
        //when
        //then
        mockMvc.perform(get("/api/diary?id=" + "backupId1")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(document("diaryCall",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .tag("DIARY API(Header에 로그인시 받은 SESSION 쿠키값이 항상 입력이 되있어야 합니다!)")
                                .summary("백업 ID를 사용해서 해당 백업 ID의 일기들을 불러오는 API")
                                .queryParameters(parameterWithName("id").type(SimpleType.STRING).description("백업 ID를 입력해주세요."))
                                .build())));
    }


}
