package sm.playground.simulator.kisapi.token.service;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import sm.playground.simulator.kisapi.token.config.KoreaInvestProperties;

@Service
public class StockApiTestService {

//    private final TokenService tokenService;
//    private final KoreaInvestProperties properties;
//    private final RestClient restClient;
//
//    public StockApiTestService(TokenService tokenService, KoreaInvestProperties properties) {
//        this.tokenService = tokenService;
//        this.properties = properties;
//        this.restClient = RestClient.builder()
//                .baseUrl(properties.baseUrl())
//                .build();
//    }
//
//    public void callPriceApi() {
//        String accessToken = tokenService.getAccessToken();
//        String stockCode = "005930";
//
//        String uri = "https://openapi.koreainvestment.com:9443/uapi/domestic-stock/v1/quotations/inquire-price" +
//                "?fid_cond_mrkt_div_code=J&fid_input_iscd=" + stockCode;
//
//        String response = restClient.get()
//                .uri(uri)
//                .headers(headers -> {
//                    headers.setContentType(MediaType.APPLICATION_JSON);
//                    headers.set("authorization", "Bearer " + accessToken);
//                    headers.set("appkey", properties.appKey());
//                    headers.set("appsecret", properties.appSecret());
//                    headers.set("tr_id", "FHKST01010100");
//                    headers.set("custtype", "P");
//                })
//                .retrieve()
//                .body(String.class);
//
//        System.out.println("📦 시세 API 응답:");
//        System.out.println(response);
//    }
}
