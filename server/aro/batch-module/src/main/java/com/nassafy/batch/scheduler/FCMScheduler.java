package com.nassafy.batch.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.common.net.HttpHeaders;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.gson.JsonParseException;
import com.nassafy.batch.dto.notificcation.FcmMessage;
import com.nassafy.core.entity.Interest;
import com.nassafy.core.entity.Member;
import com.nassafy.core.entity.Probability;
import com.nassafy.core.respository.MemberRepository;
import com.nassafy.core.respository.ProbabilityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FCMScheduler {

    private static final Logger logger = LoggerFactory.getLogger(FCMScheduler.class);

    private final MemberRepository memberRepository;
    private final ProbabilityRepository probabilityRepository;
    private final ObjectMapper objectMapper;
    private FirebaseApp firebaseApp;

    // application yml 설정파일에 설정한 값 사용
    @Value("${fcm.key.path}")
    private String firebaseConfig;

    @Value("${fcm.api_url}")
    private String API_URL;

    @Value("${fcm.key.scope}")
    private String scope;

    @Value("${fcm.projectID}")
    private String projectID;

    @Value("${prob.pivot}")
    private Integer pivot;


    @PostConstruct
    private void initialize() {
        try {
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(new ClassPathResource("firebase/" + firebaseConfig).getInputStream()))
                    .setProjectId(projectID)
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                this.firebaseApp = FirebaseApp.initializeApp(options);
                logger.info("Firebase application has been initialized");
            } else {
                this.firebaseApp = FirebaseApp.getInstance();
            }
        } catch (IOException e) {
            logger.error("Create FirebaseApp Error", e);
        }
    }

    @Scheduled(cron = "0 12 * * * ?")
//    @Scheduled(cron = "0/10 * * * * ?")
    @Transactional
    public void pushMessage() throws IOException {
        log.info("pushMessage - scheduler ");

        StringBuilder sb;
        Probability maxProbability;
        // 1. 모든 유저에 대해서
        List<Member> members = memberRepository.findAll();
        for(Member member : members){
            maxProbability = null;
            // 2. 알람 여부 확인 및 FCM 토큰 확인
            if(!member.getAlarm() || member.getFcmToken() == null || member.getFcmToken().equals("")) continue;
            logger.info("member : " + member.getEmail() + ", " + member.getNickname());

            // 3. 유저의 관심지역에 대해서
            for(Interest interest : member.getInterests()){
                Long attrectionId = interest.getAttraction().getId();

                // 4. 관심지역의 확률이 가장 높은 하나 선택
                List<Probability> probabilities = probabilityRepository.findByAttractionId(attrectionId);

                for(Probability probability : probabilities){
                    if(maxProbability == null || maxProbability.getProb() < probability.getProb()){
                        maxProbability = probability;
                    }
                }
            }

            if(maxProbability != null && maxProbability.getProb() >= pivot){
                sb  = new StringBuilder();

                String[] date = maxProbability.getDateTime().toString().split("-");
                int month = Integer.parseInt(date[1]);
                int day = Integer.parseInt(date[2].substring(0, 2));
                sb.append(member.getNickname()).append("님! ")
                        .append(maxProbability.getAttraction().getAttractionName()).append("에서 ")
                        .append(month).append("월 ")
                        .append(day).append("일에 오로라를 볼 수 있을것 같아요!");

                log.info("pushMessage - maxProbability");
                log.info(maxProbability.toString());
                log.info("pushMessage - getFcmToken : " + member.getFcmToken());
                log.info("pushMessage : " + sb.toString());
                sendMessageTo(
                        member.getFcmToken(),
                        "Aro",
                        " " +
                        sb.toString()
                );


            }

        }

    }

    public void sendMessageTo(String targetToken, String title, String body) throws IOException {
        String message = makeMessage(targetToken, title, body);

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(message,
                MediaType.get("application/json; charset=utf-8"));
        String accessToken = getAccessToken();
        Request request = new Request.Builder()
                .url(API_URL)
                .post(requestBody)
                .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .addHeader(HttpHeaders.CONTENT_TYPE, "application/json; UTF-8")
                .build();

        log.info(request.toString());
        Response response = client.newCall(request).execute();

        log.info(response.body().string());
    }

    private String makeMessage(String targetToken, String title, String body) throws JsonParseException, JsonProcessingException {
        FcmMessage fcmMessage = FcmMessage.builder()
                .message(FcmMessage.Message.builder()
                        .token(targetToken)
                        .notification(FcmMessage.Notification.builder()
                                .title(title)
                                .body(body)
                                .image(null)
                                .build()
                        ).build()).validateOnly(false).build();

        return objectMapper.writeValueAsString(fcmMessage);
    }

    private String getAccessToken() throws IOException {

        String firebaseConfigPath = "firebase/" + firebaseConfig;
        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new ClassPathResource(firebaseConfigPath).getInputStream())
                .createScoped(List.of(scope));
        googleCredentials.refreshIfExpired();
        return googleCredentials.getAccessToken().getTokenValue();
    }

}
